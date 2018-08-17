package tds.support.tool.validation;

import org.springframework.stereotype.Component;
import tds.common.Algorithm;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.Stimulus;
import tds.testpackage.model.TestPackage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StimulusValidator implements TestPackageValidator {
    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        // Validate that stimuli id are parseable to long values
        List<Stimulus> stimuli = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream()
                        .flatMap(segment -> {
                            if (segment.getAlgorithmType().equalsIgnoreCase(Algorithm.FIXED_FORM.getType())) {
                                return segment.segmentForms().stream()
                                        .flatMap(segmentForm -> segmentForm.itemGroups().stream()
                                                .filter(itemGroup -> itemGroup.getStimulus().isPresent())
                                                .map(itemGroup -> itemGroup.getStimulus().get())
                                        );
                            } else { // assume adaptive
                                return segment.pool().stream()
                                        .filter(itemGroup -> itemGroup.getStimulus().isPresent())
                                        .map(itemGroup -> itemGroup.getStimulus().get());
                            }
                        }))
                .collect(Collectors.toList());

        stimuli.forEach(stimulus -> {
            try {
                Long.parseLong(stimulus.getId());
            } catch (NumberFormatException e) {
                errors.add(new ValidationError(ErrorSeverity.CRITICAL, String.format("The stimulus with id \"%s\" has an id that is not a LONG value. " +
                        "Currently, TDS only supports stimuli ids that are of a 'LONG' data type.", stimulus.getId())));
            }
        });
    }
}
