package tds.support.tool.services.converter.impl;

import org.jetbrains.annotations.NotNull;
import tds.common.Algorithm;
import tds.support.tool.services.converter.TestPackageConverterService;
import tds.testpackage.legacy.model.*;
import tds.testpackage.legacy.model.Property;
import tds.testpackage.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static tds.testpackage.model.BlueprintElementTypes.CLAIM;

public class TestPackageConverterServiceImpl implements TestPackageConverterService {
    @Override
    public TestPackage convertToNew(final String testPackageName, final List<Testspecification> testSpecifications) {
        List<Testspecification> adminTestPackages = testSpecifications.stream()
                .filter(testspecification -> testspecification.getAdministration() != null)
                .collect(Collectors.toList());
        List<Testspecification> scoringTestPackages = testSpecifications.stream()
                .filter(testspecification -> testspecification.getScoring() != null)
                .collect(Collectors.toList());

        // We will assume that the first "testspecification" contains the root-level "TestPackage" metadata
        Testspecification testSpecification = adminTestPackages.get(0);
        TestPackage.Builder testPackageBuilder = TestPackage.builder()
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
                .setBlueprint(mapBlueprint(testPackageName, testSpecifications));


        return testPackageBuilder.build();
    }

    private List<Assessment> mapAssessments(final List<Testspecification> adminTestPackages) {
        List<Assessment> assessments = new ArrayList<>();

        for (Testspecification testSpecification : adminTestPackages) {
            assessments.add(Assessment.builder()
                    .setId(testSpecification.getIdentifier().getName())
                    .setLabel(testSpecification.getIdentifier().getLabel())
                    .setGrades(findGrades(testSpecification.getProperty()))
                    .setSegments(mapSegments(testSpecification.getAdministration()))
                    .setTools(new ArrayList<>()) //TODO: Remove this - should be optional
                    .build());
        }

        return assessments;
    }

    private List<Segment> mapSegments(final Administration administration) {
        return administration.getAdminsegment().stream()
                .map(adminSegment -> {
                    boolean isFixedForm = adminSegment.getItemselection().equalsIgnoreCase(Algorithm.FIXED_FORM.getType());
                    return Segment.builder()
                            // The legacy spec calls the segmentKey a "segment id"
                            .setId(findSegmentId(adminSegment.getSegmentid(),
                                    administration.getTestblueprint().getBpelement()))
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

    private List<SegmentBlueprintElement> mapSegmentBlueprint(final Segmentblueprint segmentBlueprint) {
        return segmentBlueprint.getSegmentbpelement().stream()
                .map(legacySegmentBp -> SegmentBlueprintElement.builder()
                        // The part before the "-" is the publisher/clientname - lets strip that out.
                        .setIdRef(legacySegmentBp.getBpelementid().substring(legacySegmentBp.getBpelementid().indexOf("-") + 1, legacySegmentBp.getBpelementid().length()))
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

    private List<SegmentForm> mapSegmentForms(final List<Segmentform> legacySegmentForms, final List<Testform> testForms,
                                              final Itempool itemPool) {
        List<SegmentForm> segmentForms = new ArrayList<>();

        // Get the set of unique formKeys - usually in a similar format as item/stimuli keys (<bankKey>-<formId>)
        Set<String> formPartitionIds = legacySegmentForms.stream().map(Segmentform::getFormpartitionid).collect(Collectors.toSet());
        Map<String, Testform> testFormMap = new HashMap<>();
        testForms.forEach(testForm ->
                testForm.getFormpartition().stream()
                        .filter(formPartition -> formPartitionIds.contains(formPartition.getIdentifier().getUniqueid()))
                        .forEach(formPartition -> testFormMap.put(formPartition.getIdentifier().getUniqueid(), testForm))
        );

        for (Segmentform legacyForm : legacySegmentForms) {
            Testform testForm = testFormMap.get(legacyForm.getFormpartitionid());
            Formpartition formPartition = testForm.getFormpartition().stream()
                    .filter(fp -> fp.getIdentifier().getUniqueid().equalsIgnoreCase(legacyForm.getFormpartitionid()))
                    .findFirst().orElseThrow(IllegalArgumentException::new);

            segmentForms.add(SegmentForm.builder()
                    .setCohort(parseCohort(testForm.getIdentifier().getUniqueid()))
                    .setId(formPartition.getIdentifier().getName())
                    .setPresentations(mapPresentations(testForm.getPoolproperty()))
                    .setItemGroups(mapItemGroups(formPartition.getItemgroup(), itemPool))
                    .build());
        }
        return segmentForms;
    }

    private String parseCohort(final String formKey) {
        // e.g., SBAC-OP-FIXED-G8E-Perf-BrainWorks-Spring-2015-2016:SleepDream-ENU - the cohort is "SleepDream"
        return formKey.substring(formKey.indexOf(":") + 1, formKey.lastIndexOf("-"));
    }

    private List<ItemGroup> mapItemGroups(final List<Itemgroup> itemGroups, final Itempool itemPool) {
        return itemGroups.stream()
                .map(ig ->
                        ItemGroup.builder()
                                // If legacy itemgroup id is "G-187-1234-0", parse out "1234"
                                .setId(parseItemGroupId(ig.getIdentifier().getUniqueid()))
                                .setMaxItems(Optional.of(ig.getMaxitems()))
                                .setMaxResponses(Optional.of(ig.getMaxresponses()))
                                .setStimulus(mapStimuli(ig.getPassageref()))
                                .setItems(mapItems(ig.getGroupitem(), itemPool))
                                .build())
                .collect(Collectors.toList());
    }

    private String parseItemGroupId(final String groupKey) {
        final String[] groupKeyStrSplit = groupKey.split("-");
        return groupKeyStrSplit[groupKeyStrSplit.length - 2];
    }


    private List<Item> mapItems(final List<Groupitem> groupItems, final Itempool itemPool) {

        Set<String> groupItemIds = groupItems.stream().map(Groupitem::getItemid).collect(Collectors.toSet());
        Map<String, Testitem> testItemMap = itemPool.getTestitem().stream()
                .filter(testItem -> groupItemIds.contains(testItem.getIdentifier().getUniqueid()))
                .collect(Collectors.toMap(ti -> ti.getIdentifier().getUniqueid(), Function.identity()));

        return groupItems.stream()
                .map(gi -> Item.builder()
                        .setId(gi.getItemid().substring(gi.getItemid().indexOf("-") + 1, gi.getItemid().length()))
                        .setAdministrationRequired(gi.getAdminrequired() == null ? Optional.empty() : Optional.of(gi.getAdminrequired()))
                        .setFieldTest(gi.getIsfieldtest() == null ? Optional.empty() : Optional.of(gi.getIsfieldtest()))
                        .setActive(gi.getIsactive() == null ? Optional.empty() : Optional.of(gi.getIsactive()))
                        .setResponseRequired(gi.getResponserequired() == null ? Optional.empty() : Optional.of(gi.getResponserequired()))
                        .setType(testItemMap.get(gi.getItemid()).getItemtype())
                        .setPresentations(mapPresentations(testItemMap.get(gi.getItemid()).getPoolproperty()))
                        .setItemScoreDimension(mapItemScoreDimensions(testItemMap.get(gi.getItemid()).getItemscoredimension().get(0)))
                        .setBlueprintReferences(mapBlueprintReferences(testItemMap.get(gi.getItemid()).getBpref()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<BlueprintReference> mapBlueprintReferences(final List<Bpref> bpref) {
        return bpref.stream()
                .map(bpRef -> BlueprintReference.builder()
                        .setIdRef(bpRef.getContent().substring(bpRef.getContent().indexOf("-") + 1, bpRef.getContent().length()))
                        .build())
                .collect(Collectors.toList());
    }

    private ItemScoreDimension mapItemScoreDimensions(final Itemscoredimension legacyISD) {
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

    private List<Presentation> mapPresentations(final List<Poolproperty> poolProperties) {
        return poolProperties.stream()
                .filter(property -> property.getProperty().equalsIgnoreCase("Language"))
                .map(property -> Presentation.builder()
                        .setCode(property.getValue())
                        .setLabel(property.getLabel())
                        .build())
                .collect(Collectors.toList());
    }

    private Optional<Stimulus> mapStimuli(final List<Passageref> passageRefs) {
        if (passageRefs.size() == 0) {
            return Optional.empty();
        }

        // The stimulus id is the last part of the stimulus key (after the "-")
        final String stimuliKey = passageRefs.get(0).getContent();
        return Optional.of(Stimulus.builder()
                .setId(stimuliKey.substring(stimuliKey.indexOf("-") + 1, stimuliKey.length()))
                .build());
    }

    private String findSegmentId(final String segmentKey, final List<Bpelement> bpElements) {
        return bpElements.stream()
                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.SEGMENT)
                        && bpEl.getIdentifier().getUniqueid().equalsIgnoreCase(segmentKey))
                .map(bpEl -> bpEl.getIdentifier().getName())
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Could not find a segment id in the blueprint for the segment with key " + segmentKey));
    }

    private List<Grade> findGrades(final List<Property> properties) {
        return properties.stream()
                .filter(property -> property.getName().equalsIgnoreCase("grade"))
                .map(property -> Grade.builder()
                        .setValue(property.getValue())
                        .setLabel(property.getLabel() != null ? Optional.of(property.getLabel()) : Optional.empty())
                        .build())
                .collect(Collectors.toList());
    }

    private int findBankKey(final Administration administration) {
        // We will assume the bank key is the same for all items/forms. In our case we'll just get the first item in the item
        // pool and peek at it's item key. The bank key is the first part of the item key (e.g., "187" in "187-1234")
        final String anyItemId = administration.getItempool().getTestitem().get(0).getIdentifier().getUniqueid();
        return Integer.parseInt(anyItemId.substring(0, anyItemId.indexOf("-")));
    }

    private List<BlueprintElement> mapBlueprint(final String testPackageName, final List<Testspecification> testSpecifications) {
        /* Three cases to consider here:

            1. "test" and "segment" blueprint elements - these are structural and must be grouped under the single "package" blueprint element
                if more than one test is present in the test package. Similarly if there is only a single segment in an assessment, the "segment"
                blueprint element is optional
            2. The "claims" and "targets" (known formerly as "strands" and "content levels" - these are scoring and adaptive
                algorithm specific and are structurally grouped
            3. The miscellaneous bp element types - these include "affinity groups" and "sock" blueprint element types currently

         */
        List<BlueprintElement> blueprintElements = new ArrayList<>(mapTestAndSegmentBlueprintElements(testPackageName, testSpecifications));
        blueprintElements.addAll(mapMiscellaneousBlueprintElements(testSpecifications));
        blueprintElements.addAll(mapClaimsAndTargetBlueprintElements(testSpecifications));
        return blueprintElements;
    }

    private List<BlueprintElement> mapTestAndSegmentBlueprintElements(final String testPackageName, final List<Testspecification> testSpecifications) {
        List<BlueprintElement> blueprintElements = new ArrayList<>();
        //  If the number of administration packages > 1, this is a combined test package and needs a root level "package" bpEl
        boolean isCombinedTestPackage = testSpecifications.stream()
                .filter(testSpecification -> testSpecification.getAdministration() != null)
                .collect(Collectors.toList()).size() > 1;

        if (isCombinedTestPackage) {
            List<BlueprintElement> combinedAssessmentBlueprintElements = new ArrayList<>();
            testSpecifications.stream()
                    .filter(testSpecification -> testSpecification.getAdministration() != null)
                    .forEach(assessment -> combinedAssessmentBlueprintElements.add(mapAssessmentBlueprintElements(assessment)));

            // Add the root-level "package" blueprint element
            blueprintElements.add(BlueprintElement.builder()
                    .setId(testPackageName)
                    .setType(BlueprintElementTypes.PACKAGE)
                    .setBlueprintElements(combinedAssessmentBlueprintElements)
                    .build());

        } else { // Single assessment
            Testspecification assessment = testSpecifications.stream()
                    .filter(testSpecification -> testSpecification.getAdministration() != null)
                    .findFirst().get();

            blueprintElements.add(mapAssessmentBlueprintElements(assessment));
        }

        return blueprintElements;
    }

    private BlueprintElement mapAssessmentBlueprintElements(final Testspecification assessment) {
        boolean isSegmented = assessment.getAdministration().getAdminsegment().size() > 1;

        if (isSegmented) {
            List<BlueprintElement> segmentBpElements = assessment.getAdministration().getTestblueprint().getBpelement().stream()
                    .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.SEGMENT))
                    .map(bpEl -> BlueprintElement.builder()
                            .setId(bpEl.getIdentifier().getName())
                            .setType(bpEl.getElementtype())
                            .build())
                    .collect(Collectors.toList());

            return BlueprintElement.builder()
                    .setType(BlueprintElementTypes.TEST)
                    .setId(assessment.getIdentifier().getName())
                    .setBlueprintElements(segmentBpElements)
                    .build();
        } else { // single-segmented assessment - no "segment"  bp element required
            return BlueprintElement.builder()
                    .setType(BlueprintElementTypes.TEST)
                    .setId(assessment.getIdentifier().getName())
                    .build();
        }
    }

    /* Map Affinity Groups, SOCK, and other miscellaneous blueprint elements */
    private List<BlueprintElement> mapMiscellaneousBlueprintElements(final List<Testspecification> testSpecifications) {
        return testSpecifications.stream()
                .flatMap(testSpecification -> {
                    if (testSpecification.getAdministration() != null) {
                        return testSpecification.getAdministration().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.AFFINITY_GROUP) ||
                                        bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.SOCK))
                                .map(bpEl -> BlueprintElement.builder()
                                        .setId(bpEl.getIdentifier().getName())
                                        .setType(bpEl.getElementtype())
                                        .build());
                    } else {
                        return testSpecification.getScoring().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.AFFINITY_GROUP) ||
                                        bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.SOCK))
                                .map(bpEl -> BlueprintElement.builder()
                                        .setId(bpEl.getIdentifier().getName())
                                        .setType(bpEl.getElementtype())
                                        .build());
                    }
                })
                .collect(Collectors.toList());
    }

    @NotNull
    private List<BlueprintElement> mapClaimsAndTargetBlueprintElements(final List<Testspecification> testSpecifications) {
        // Nest and map Claims/Targets (strands/content levels in legacy)
        Map<String, List<Bpelement>> parentIdToBpElement = testSpecifications.stream()
                .flatMap(testSpecification -> {
                    if (testSpecification.getAdministration() != null) {
                        return testSpecification.getAdministration().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase("contentlevel"));
                    } else {
                        return testSpecification.getScoring().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase("contentlevel"));
                    }
                })
                .collect(Collectors.groupingBy(bpEl ->
                        bpEl.getParentid().substring(bpEl.getParentid().indexOf("-") + 1, bpEl.getParentid().length())));

        List<BlueprintElement> claimBlueprintElements = testSpecifications.stream()
                .flatMap(testSpecification -> {
                    if (testSpecification.getAdministration() != null) {
                        return testSpecification.getAdministration().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase("strand"))
                                .map(bpEl -> BlueprintElement.builder()
                                        .setId(bpEl.getIdentifier().getName())
                                        .setType(BlueprintElementTypes.CLAIM)
                                        .setBlueprintElements(new ArrayList<>())
                                        .build()
                                );
                    } else {
                        return testSpecification.getScoring().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase("strand"))
                                .map(bpEl -> BlueprintElement.builder()
                                        .setId(bpEl.getIdentifier().getName())
                                        .setType(BlueprintElementTypes.CLAIM)
                                        .setBlueprintElements(new ArrayList<>())
                                        .build()
                                );
                    }
                })
                .collect(Collectors.toList());

        claimBlueprintElements.forEach(blueprintElement -> findBlueprintElement(blueprintElement, parentIdToBpElement));
        return claimBlueprintElements;
    }

    private void findBlueprintElement(BlueprintElement currentEl, Map<String, List<Bpelement>> parentMap) {
        if (parentMap.containsKey(currentEl.getId())) {
            List<BlueprintElement> blueprintElements = currentEl.blueprintElements();
            List<Bpelement> bpEl = parentMap.get(currentEl.getId());

            bpEl.forEach(childEl -> {
                BlueprintElement currBpEl = BlueprintElement.builder()
                        .setId(childEl.getIdentifier().getName())
                        .setType(BlueprintElementTypes.TARGET)
                        .setBlueprintElements(new ArrayList<>())
                        .build();
                blueprintElements.add(currBpEl);
                findBlueprintElement(currBpEl, parentMap);
            });
        }

    }

    private String findSingleProperty(final List<Property> properties, final String propertyName) {
        return properties.stream()
                .filter(property -> property.getName().equalsIgnoreCase(propertyName))
                .map(Property::getValue)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No subject property was found in the test specification package"));
    }
}
