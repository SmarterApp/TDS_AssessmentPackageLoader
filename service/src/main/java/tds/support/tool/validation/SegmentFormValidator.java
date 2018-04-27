package tds.support.tool.validation;

import org.springframework.stereotype.Component;
import tds.common.Algorithm;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.Presentation;
import tds.testpackage.model.SegmentForm;
import tds.testpackage.model.TestPackage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SegmentFormValidator implements TestPackageValidator {

    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        List<SegmentForm> forms = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream()
                        .filter(segment -> segment.getAlgorithmType().equalsIgnoreCase(Algorithm.FIXED_FORM.getType()))
                        .flatMap(segment -> segment.segmentForms().stream()))
                .collect(Collectors.toList());

        validateSegmentFormIdsAreUnique(errors, forms);
        validateItemPresentationsAreAlsoAtFormLevel(errors, forms);
    }

    private void validateItemPresentationsAreAlsoAtFormLevel(final List<ValidationError> errors, final List<SegmentForm> forms) {
        for (SegmentForm form : forms) {
            Set<String> languageCodes = form.getPresentations().stream().map(Presentation::getCode).collect(Collectors.toSet());

            form.itemGroups().stream()
                    .flatMap(itemGroup -> itemGroup.items().stream()
                            .flatMap(item -> item.getPresentations().stream()
                                    .filter(presentation -> !languageCodes.contains(presentation.getCode()))
                            ))
                    .forEach(presentation -> errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                            String.format("An item contained a presentation code \"%s\" that was not declared at the segment form level. " +
                                    "A segment form presentation list must contain the set of all presentation codes within its contained items", presentation.getCode()))));
        }
    }

    private void validateSegmentFormIdsAreUnique(final List<ValidationError> errors, final List<SegmentForm> forms) {
        Map<String, List<SegmentForm>> formsById = forms.stream().collect(Collectors.groupingBy(SegmentForm::getId));
        // If there are multiple segment forms with the same ID, we have a problem. They must be unique, certainly within the test package
        formsById.entrySet().stream()
                .filter(segmentForms -> segmentForms.getValue().size() > 1)
                .forEach(segmentForms -> errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                        String.format("Multiple segment forms with the id %s were detected. Each segment form must contain a unique id.", segmentForms.getKey()))));
    }
}
