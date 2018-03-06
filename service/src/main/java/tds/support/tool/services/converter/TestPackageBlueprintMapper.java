package tds.support.tool.services.converter;

import tds.support.tool.utils.TestPackageUtils;
import tds.testpackage.legacy.model.Bpelement;
import tds.testpackage.legacy.model.Testspecification;
import tds.testpackage.model.BlueprintElement;
import tds.testpackage.model.BlueprintElementTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestPackageBlueprintMapper {
    public static List<BlueprintElement> mapBlueprint(final String testPackageName, final List<Testspecification> testSpecifications) {
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

    private static List<BlueprintElement> mapTestAndSegmentBlueprintElements(final String testPackageName, final List<Testspecification> testSpecifications) {
        List<BlueprintElement> blueprintElements = new ArrayList<>();
        //  If the number of administration packages > 1, this is a combined test package and needs a root level "package" bpEl
        boolean isCombinedTestPackage = testSpecifications.stream()
                .filter(TestPackageUtils::isAdministrationPackage)
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
                    .filter(TestPackageUtils::isAdministrationPackage)
                    .findFirst().get();

            blueprintElements.add(mapAssessmentBlueprintElements(assessment));
        }

        return blueprintElements;
    }

    private static BlueprintElement mapAssessmentBlueprintElements(final Testspecification assessment) {
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

    // Map Affinity Groups, SOCK, and other miscellaneous blueprint elements
    private static List<BlueprintElement> mapMiscellaneousBlueprintElements(final List<Testspecification> testSpecifications) {
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

    private static List<BlueprintElement> mapClaimsAndTargetBlueprintElements(final List<Testspecification> testSpecifications) {
        // Nest and map Claims/Targets (strands/content levels in legacy)
        Map<String, List<Bpelement>> parentIdToBpElement = testSpecifications.stream()
                .flatMap(testSpecification -> {
                    if (TestPackageUtils.isAdministrationPackage(testSpecification)) {
                        return testSpecification.getAdministration().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.CONTENT_LEVEL));
                    } else {
                        return testSpecification.getScoring().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.CONTENT_LEVEL));
                    }
                })
                .collect(Collectors.groupingBy(bpEl -> TestPackageUtils.parseIdFromKey(bpEl.getParentid())));

        // Map all claims - these are our root level elements
        List<BlueprintElement> claimBlueprintElements = testSpecifications.stream()
                .flatMap(testSpecification -> {
                    if (TestPackageUtils.isAdministrationPackage(testSpecification)) {
                        return testSpecification.getAdministration().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.STRAND))
                                .map(bpEl -> BlueprintElement.builder()
                                        .setId(bpEl.getIdentifier().getName())
                                        .setType(BlueprintElementTypes.CLAIM)
                                        .setBlueprintElements(new ArrayList<>())
                                        .build()
                                );
                    } else {
                        return testSpecification.getScoring().getTestblueprint().getBpelement()
                                .stream()
                                .filter(bpEl -> bpEl.getElementtype().equalsIgnoreCase(BlueprintElementTypes.STRAND))
                                .map(bpEl -> BlueprintElement.builder()
                                        .setId(bpEl.getIdentifier().getName())
                                        .setType(BlueprintElementTypes.CLAIM)
                                        .setBlueprintElements(new ArrayList<>())
                                        .build()
                                );
                    }
                })
                .collect(Collectors.toList());

        claimBlueprintElements.forEach(blueprintElement -> mapBlueprintElement(blueprintElement, parentIdToBpElement));
        return claimBlueprintElements;
    }

    private static void mapBlueprintElement(BlueprintElement currentEl, Map<String, List<Bpelement>> parentMap) {
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
                mapBlueprintElement(currBpEl, parentMap);
            });
        }
    }
}
