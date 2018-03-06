package tds.support.tool.services.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tds.common.Algorithm;
import tds.support.tool.utils.TestPackageUtils;
import tds.testpackage.legacy.model.*;
import tds.testpackage.legacy.model.Property;
import tds.testpackage.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestPackageMapper {
    private static final Logger log = LoggerFactory.getLogger(TestPackageMapper.class);

    public static TestPackage toNew(final String testPackageName, final List<Testspecification> testSpecifications) {
        List<Testspecification> adminTestPackages = testSpecifications.stream()
                .filter(TestPackageUtils::isAdministrationPackage)
                .collect(Collectors.toList());
        List<Testspecification> scoringTestPackages = testSpecifications.stream()
                .filter(TestPackageUtils::isScoringPackage)
                .collect(Collectors.toList());

        log.debug("Converting {} administration and {} scoring packages to a new TestPackage called {}",
                adminTestPackages.size(), scoringTestPackages.size(), testPackageName);
        log.debug("Administration: ");
        adminTestPackages.forEach(admin -> log.debug("\t {}", admin.getIdentifier().getUniqueid()));
        log.debug("Administration: ");
        scoringTestPackages.forEach(scoring -> log.debug("\t {}", scoring.getIdentifier().getUniqueid()));

        // We will assume that the first "test specification" contains the root-level "TestPackage" metadata such as
        // bank key and publisher information
        Testspecification testSpecification = adminTestPackages.get(0);
        return TestPackage.builder()
                .setVersion(String.valueOf(testSpecification.getVersion()))
                .setPublisher(testSpecification.getPublisher())
                .setPublishDate(testSpecification.getPublishdate())
                // This value is not found in the legacy spec, but is required in the new spec
                // we will use the current year as a placeholder
                .setAcademicYear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                .setSubject(findSingleProperty(testSpecification.getProperty(), "subject"))
                .setType(findSingleProperty(testSpecification.getProperty(), "type"))
                .setBankKey(findBankKey(testSpecification.getAdministration()))
                .setAssessments(mapAssessments(adminTestPackages))
                .setBlueprint(TestPackageBlueprintMapper.mapBlueprint(testPackageName, testSpecifications))
                .build();
    }

    private static List<Assessment> mapAssessments(final List<Testspecification> adminTestPackages) {
        List<Assessment> assessments = new ArrayList<>();

        for (Testspecification testSpecification : adminTestPackages) {
            assessments.add(Assessment.builder()
                    .setId(testSpecification.getIdentifier().getName())
                    .setLabel(testSpecification.getIdentifier().getLabel())
                    .setGrades(mapGrades(testSpecification.getProperty()))
                    .setSegments(mapSegments(testSpecification.getAdministration()))
                    .setTools(new ArrayList<>()) //TODO: Remove this - should be optional
                    .build());
        }

        return assessments;
    }

    private static List<Segment> mapSegments(final Administration administration) {
        return administration.getAdminsegment().stream()
                .map(adminSegment -> {
                    boolean isFixedForm = adminSegment.getItemselection().equalsIgnoreCase(Algorithm.FIXED_FORM.getType());
                    return Segment.builder()
                            // The legacy spec calls the segmentKey a "segment id". We need to fetch the actual segmentId from the blueprint
                            .setId(findSegmentIdFromBlueprint(adminSegment.getSegmentid(), administration.getTestblueprint().getBpelement()))
                            .setPosition(Integer.parseInt(adminSegment.getPosition()))
                            //TODO: replace with Algorithm enum
                            .setAlgorithmType(adminSegment.getItemselection().equalsIgnoreCase(Algorithm.ADAPTIVE_2.getType())
                                    ? "adaptive" : adminSegment.getItemselection())
                            .setAlgorithmImplementation(adminSegment.getItemselector().getIdentifier().getUniqueid())
                            .setLabel(Optional.of(adminSegment.getSegmentid()))
                            .setPool(isFixedForm ? null : mapItemGroups(adminSegment.getSegmentpool().getItemgroup(),
                                    administration.getItempool()))
                            .setSegmentForms(mapSegmentForms(adminSegment.getSegmentform(),
                                    administration.getTestform(), administration.getItempool()))
                            .setSegmentBlueprint(mapSegmentBlueprint(adminSegment.getSegmentblueprint()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static List<SegmentBlueprintElement> mapSegmentBlueprint(final Segmentblueprint segmentBlueprint) {
        return segmentBlueprint.getSegmentbpelement().stream()
                .map(legacySegmentBp -> SegmentBlueprintElement.builder()
                        // The part before the "-" is the publisher/clientname - lets strip that out.
                        .setIdRef(TestPackageUtils.parseIdFromKey(legacySegmentBp.getBpelementid()))
                        .setMaxExamItems(legacySegmentBp.getMaxopitems().intValue())
                        .setMinExamItems(legacySegmentBp.getMinopitems().intValue())
                        .setMaxFieldTestItems(legacySegmentBp.getMaxftitems() != null
                                ? Optional.of(legacySegmentBp.getMaxftitems().intValue()) : Optional.empty())
                        .setMinFieldTestItems(legacySegmentBp.getMinftitems() != null
                                ? Optional.of(legacySegmentBp.getMinftitems().intValue()) : Optional.empty())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private static List<SegmentForm> mapSegmentForms(final List<Segmentform> legacySegmentForms, final List<Testform> testForms,
                                                     final Itempool itemPool) {
        List<SegmentForm> segmentForms = new ArrayList<>();

        // Get the set of unique formKeys - usually in a similar format as item/stimuli keys (<bankKey>-<formId>)
        Set<String> formPartitionIds = legacySegmentForms.stream().map(Segmentform::getFormpartitionid).collect(Collectors.toSet());
        // We need data from both the parent "Testform" and "Formpartition". This map contains a mapping from the
        // form partition id to it's parent testform
        Map<String, Testform> testFormMap = new HashMap<>();
        testForms.forEach(testForm ->
                testForm.getFormpartition().stream()
                        .filter(formPartition -> formPartitionIds.contains(formPartition.getIdentifier().getUniqueid()))
                        .forEach(formPartition -> testFormMap.put(formPartition.getIdentifier().getUniqueid(), testForm))
        );

        for (Segmentform legacyForm : legacySegmentForms) {
            Testform testForm = testFormMap.get(legacyForm.getFormpartitionid());
            // Find the particular form partition we are dealing with from within it's parent Testform
            Formpartition formPartition = testForm.getFormpartition().stream()
                    .filter(fp -> fp.getIdentifier().getUniqueid().equalsIgnoreCase(legacyForm.getFormpartitionid()))
                    .findFirst().orElseThrow(IllegalArgumentException::new);

            segmentForms.add(SegmentForm.builder()
                    .setCohort(TestPackageUtils.parseCohort(testForm.getIdentifier().getUniqueid()))
                    .setId(formPartition.getIdentifier().getName())
                    .setPresentations(mapPresentations(testForm.getPoolproperty()))
                    .setItemGroups(mapItemGroups(formPartition.getItemgroup(), itemPool))
                    .build());
        }
        return segmentForms;
    }

    private static List<ItemGroup> mapItemGroups(final List<Itemgroup> itemGroups, final Itempool itemPool) {
        return itemGroups.stream()
                .map(ig ->
                        ItemGroup.builder()
                                // If legacy itemgroup id is "G-187-1234-0", parse out "1234"
                                .setId(TestPackageUtils.parseItemGroupId(ig.getIdentifier().getUniqueid()))
                                .setMaxItems(Optional.of(ig.getMaxitems()))
                                .setMaxResponses(Optional.of(ig.getMaxresponses()))
                                .setStimulus(mapStimuli(ig.getPassageref()))
                                .setItems(mapItems(ig.getGroupitem(), itemPool))
                                .build())
                .collect(Collectors.toList());
    }

    private static List<Item> mapItems(final List<Groupitem> groupItems, final Itempool itemPool) {
        Set<String> groupItemIds = groupItems.stream().map(Groupitem::getItemid).collect(Collectors.toSet());
        Map<String, Testitem> testItemMap = itemPool.getTestitem().stream()
                .filter(testItem -> groupItemIds.contains(testItem.getIdentifier().getUniqueid()))
                .collect(Collectors.toMap(ti -> ti.getIdentifier().getUniqueid(), Function.identity()));

        return groupItems.stream()
                .map(gi -> Item.builder()
                        // If the item key is "187-1234" the item ID is "1234"
                        .setId(gi.getItemid())
                        .setAdministrationRequired(Optional.ofNullable(gi.getAdminrequired()))
                        .setFieldTest(Optional.ofNullable(gi.getIsfieldtest()))
                        .setActive(Optional.ofNullable(gi.getIsactive()))
                        .setResponseRequired(Optional.ofNullable(gi.getResponserequired()))
                        .setType(testItemMap.get(gi.getItemid()).getItemtype())
                        .setPresentations(mapPresentations(testItemMap.get(gi.getItemid()).getPoolproperty()))
                        .setItemScoreDimension(mapItemScoreDimensions(testItemMap.get(gi.getItemid()).getItemscoredimension().get(0)))
                        .setBlueprintReferences(mapBlueprintReferences(testItemMap.get(gi.getItemid()).getBpref()))
                        .build())
                .collect(Collectors.toList());
    }

    private static List<BlueprintReference> mapBlueprintReferences(final List<Bpref> bpref) {
        return bpref.stream()
                .map(bpRef -> BlueprintReference.builder()
                        .setIdRef(TestPackageUtils.parseIdFromKey(bpRef.getContent()))
                        .build())
                .collect(Collectors.toList());
    }

    private static ItemScoreDimension mapItemScoreDimensions(final Itemscoredimension legacyISD) {
        return ItemScoreDimension.builder()
                .setMeasurementModel(legacyISD.getMeasurementmodel())
                .setScorePoints(legacyISD.getScorepoints().intValue())
                .setWeight(legacyISD.getWeight())
                .setDimension(legacyISD.getDimension() == null ? Optional.empty() : Optional.of(legacyISD.getDimension()))
                .setItemScoreParameters(
                        // map the legacy item score params
                        legacyISD.getItemscoreparameter().stream()
                                .map(param -> ItemScoreParameter.builder()
                                        .setMeasurementParameter(param.getMeasurementparameter())
                                        .setValue(param.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }

    private static List<Presentation> mapPresentations(final List<Poolproperty> poolProperties) {
        return poolProperties.stream()
                .filter(property -> property.getProperty().equalsIgnoreCase("Language"))
                .map(property -> Presentation.builder()
                        .setCode(property.getValue())
                        .setLabel(property.getLabel())
                        .build())
                .collect(Collectors.toList());
    }

    private static Optional<Stimulus> mapStimuli(final List<Passageref> passageRefs) {
        if (passageRefs.size() == 0) {
            return Optional.empty();
        }

        // The stimulus id is the last part of the stimulus key (after the "-")
        final String stimuliKey = passageRefs.get(0).getContent();
        return Optional.of(Stimulus.builder()
                .setId(TestPackageUtils.parseIdFromKey(stimuliKey))
                .build());
    }

    private static List<Grade> mapGrades(final List<Property> properties) {
        return properties.stream()
                .filter(property -> property.getName().equalsIgnoreCase("grade"))
                .map(property -> Grade.builder()
                        .setValue(property.getValue())
                        .setLabel(property.getLabel() != null ? Optional.of(property.getLabel()) : Optional.empty())
                        .build())
                .collect(Collectors.toList());
    }

    private static String findSegmentIdFromBlueprint(final String segmentKey, final List<Bpelement> bpElements) {
        return bpElements.stream()
                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.SEGMENT)
                        && bpEl.getIdentifier().getUniqueid().equalsIgnoreCase(segmentKey))
                .map(bpEl -> bpEl.getIdentifier().getName())
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Could not find a segment id in the blueprint for the segment with key " + segmentKey));
    }

    private static int findBankKey(final Administration administration) {
        // We will assume the bank key is the same for all items/forms. In our case we'll just get the first item in the item
        // pool and peek at it's item key. The bank key is the first part of the item key (e.g., "187" in "187-1234")
        final String anyItemId = administration.getItempool().getTestitem().get(0).getIdentifier().getUniqueid();
        return Integer.parseInt(anyItemId.substring(0, anyItemId.indexOf("-")));
    }

    private static String findSingleProperty(final List<Property> properties, final String propertyName) {
        return properties.stream()
                .filter(property -> property.getName().equalsIgnoreCase(propertyName))
                .map(Property::getValue)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No subject property was found in the test specification package"));
    }
}
