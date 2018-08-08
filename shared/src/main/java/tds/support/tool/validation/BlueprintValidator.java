package tds.support.tool.validation;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BlueprintValidator implements TestPackageValidator {
    private final static Set<String> ROOT_BLUEPRINT_ELEMENT_TYPES = ImmutableSet.of(
            BlueprintElementTypes.AFFINITY_GROUP,
            BlueprintElementTypes.CLAIM,
            BlueprintElementTypes.PACKAGE,
            BlueprintElementTypes.SOCK,
            BlueprintElementTypes.TEST
    );

    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        validateTopLevelBlueprintElements(testPackage, errors);
        validateOnlyOneTopLevelTestOrPackageBlueprintElement(testPackage, errors);
        validateEachBlueprintElementRecursive(testPackage, errors, testPackage.getBlueprint());
    }

    private void validateEachBlueprintElementRecursive(final TestPackage testPackage, final List<ValidationError> errors, final List<BlueprintElement> blueprint) {
        for (BlueprintElement bpEl : blueprint) {
            switch (bpEl.getType()) {
                case BlueprintElementTypes.PACKAGE:
                    // Validate that a "package" blueprint has only child test blueprint elements
                    bpEl.blueprintElements().stream()
                            .filter(testBpEl -> !testBpEl.getType().equalsIgnoreCase(BlueprintElementTypes.TEST))
                            .forEach(childEl -> errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                                    "Child elements of a 'package' blueprint element should be of a 'test' blueprint element type")));
                    break;
                case BlueprintElementTypes.TEST:
                    // Validate that a "test" blueprint element has a corresponding Test element
                    Optional<Assessment> maybeAssessment = testPackage.getAssessments().stream()
                            .filter(assessment -> assessment.getId().equalsIgnoreCase(bpEl.getId()))
                            .findFirst();

                    if (!maybeAssessment.isPresent()) {
                        errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                                String.format("A 'test' type blueprint element with the id '%s' was identified, but no corresponding " +
                                        "<Test> element was identified with a matching id", bpEl.getId())));
                    }

                    // Validate that a "test" blueprint has only child segments
                    bpEl.blueprintElements().stream()
                            .filter(segBpEl -> !segBpEl.getType().equalsIgnoreCase(BlueprintElementTypes.SEGMENT))
                            .forEach(childEl -> errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                                    "Child elements of a 'test' blueprint element should be of a 'segment' blueprint element type")));
                    break;
                case BlueprintElementTypes.SEGMENT:
                    // Validate that a "segment" blueprint element has a corresponding Segment element
                    Optional<Segment> maybeSegment = testPackage.getAssessments().stream()
                            .flatMap(assessment -> assessment.getSegments().stream()
                                    .filter(segment -> segment.getId().equalsIgnoreCase(bpEl.getId()))
                            )
                            .findFirst();

                    if (!maybeSegment.isPresent()) {
                        errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                                String.format("A 'segment' type blueprint element with the id '%s' was identified, but no corresponding " +
                                        "<Segment> element was identified with a matching id", bpEl.getId())));
                    }
                    break;
                case BlueprintElementTypes.CLAIM:
                case BlueprintElementTypes.TARGET:
                    // Validate that a "claim" blueprint has only child claims or targets
                    bpEl.blueprintElements().stream()
                            .filter(claimOrTargetEl -> !claimOrTargetEl.getType().equalsIgnoreCase(BlueprintElementTypes.TARGET))
                            .forEach(childEl -> errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                                    "Child elements of a 'claim' or 'target' blueprint element should be of a 'target' blueprint element type")));
                    break;
                case BlueprintElementTypes.STRAND:
                    errors.add(new ValidationError(ErrorSeverity.WARN,
                            String.format("A blueprint type of 'strand' was detected for the blueprint element '%s'. It is recommended that the blueprint " +
                                    "element type is renamed to 'claim'", bpEl.getId())));
                    break;
                case BlueprintElementTypes.CONTENT_LEVEL:
                    errors.add(new ValidationError(ErrorSeverity.WARN,
                            String.format("A blueprint type of 'contentlevel' was detected for the blueprint element '%s'. It is recommended that the blueprint " +
                                    "element type is renamed to 'target'", bpEl.getId())));
                    break;
                case BlueprintElementTypes.AFFINITY_GROUP:
                case BlueprintElementTypes.SOCK:
                    // Nothing to validate
                    break;
                default:
                    errors.add(new ValidationError(ErrorSeverity.WARN, String.format("Unknown blueprint element type detected for id '%s'", bpEl.getId())));
                    break;
            }

            validateEachBlueprintElementRecursive(testPackage, errors, bpEl.blueprintElements());
        }
    }

    private void validateTopLevelBlueprintElements(final TestPackage testPackage, final List<ValidationError> errors) {
        // Validate that top level blueprint element types are recognized
        testPackage.getBlueprint().stream()
                .filter(bp -> !ROOT_BLUEPRINT_ELEMENT_TYPES.contains(bp.getType()))
                .forEach(bp -> errors.add(new ValidationError(ErrorSeverity.WARN,
                        String.format("A top-level blueprint element type of %s was detected for blueprint element with id of '%s'. " +
                                "Recognized top-level blueprint element types include: %s", bp.getType(), bp.getId(), ROOT_BLUEPRINT_ELEMENT_TYPES))));
    }

    private void validateOnlyOneTopLevelTestOrPackageBlueprintElement(final TestPackage testPackage, final List<ValidationError> errors) {
        // Validate that there is either a single "package" or "test" blueprint element type at the top level
        List<BlueprintElement> testOrPackageBlueprintElements = testPackage.getBlueprint().stream()
                .filter(bp -> bp.getType().equalsIgnoreCase(BlueprintElementTypes.TEST)
                        || bp.getType().equalsIgnoreCase(BlueprintElementTypes.PACKAGE))
                .collect(Collectors.toList());

        if (testOrPackageBlueprintElements.isEmpty()) {
            errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                    "The test package did not contain a 'package' or 'test' top-level blueprint element. For " +
                            "combined multi-test packages, a 'package' blueprint element containing the 'test' blueprint elements is needed. " +
                            "For single-test packages, a top level 'test' blueprint element is required."));
        } else if (testOrPackageBlueprintElements.size() > 1) {
            errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                    "The test package contained more than one top-level 'test' or 'package' blueprint element. " +
                            "For combined multi-test packages, a 'package' blueprint element containing the 'test' blueprint elements is needed. " +
                            "For single-test packages, a top level 'test' blueprint element is required."));
        }
    }
}
