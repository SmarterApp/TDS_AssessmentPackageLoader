package tds.support.tool.validation;

import org.springframework.stereotype.Component;
import tds.support.job.ErrorSeverity;
import tds.support.tool.utils.ValidationUtils;
import tds.testpackage.model.BlueprintElement;
import tds.testpackage.model.BlueprintElementTypes;
import tds.testpackage.model.TestPackage;

import java.util.List;
import java.util.Optional;

@Component
public class AssessmentValidator implements TestPackageValidator {
    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        validateAssessmentIdLength(testPackage, errors);
        validateAssessmentsHaveAssociatedBlueprintElement(testPackage, errors);
    }

    private void validateAssessmentIdLength(final TestPackage testPackage, final List<ValidationError> errors) {
        testPackage.getAssessments().stream()
                .filter(assessment -> !ValidationUtils.isNotNullAndHasMaxLength(assessment.getId(), 250))
                .forEach(assessment -> errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                        "Cannot have a test id that exceeds 250 characters")));
    }

    private void validateAssessmentsHaveAssociatedBlueprintElement(final TestPackage testPackage, final List<ValidationError> errors) {
        boolean isMultiTestPackage = testPackage.getAssessments().size() > 1;

        if (isMultiTestPackage) {
            testPackage.getAssessments().forEach(assessment -> {
                        Optional<BlueprintElement> maybeTestBpEl = testPackage.getBlueprint().stream()
                                .filter(bpEl -> bpEl.getType().equalsIgnoreCase(BlueprintElementTypes.PACKAGE))
                                .flatMap(bpEl -> bpEl.blueprintElements().stream()
                                        .filter(testBpEl -> testBpEl.getType().equalsIgnoreCase(BlueprintElementTypes.TEST)
                                                && testBpEl.getId().equalsIgnoreCase(assessment.getId()))
                                ).findFirst();

                        if (!maybeTestBpEl.isPresent()) {
                            errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                                    String.format("A 'test' BlueprintElement was not found in the test blueprint for the test with id '%s'.",
                                            assessment.getId())));
                        }
                    }
            );
        } else {
            testPackage.getAssessments().forEach(assessment -> {
                        Optional<BlueprintElement> maybeTestBpEl = testPackage.getBlueprint().stream()
                                .filter(bpEl -> bpEl.getType().equalsIgnoreCase(BlueprintElementTypes.TEST)
                                        && bpEl.getId().equalsIgnoreCase(assessment.getId()))
                                .findFirst();

                        if (!maybeTestBpEl.isPresent()) {
                            errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                                    String.format("A 'test' BlueprintElement was not found in the test blueprint for the test with id '%s'.",
                                            assessment.getId())));
                        }
                    }
            );
        }
    }
}
