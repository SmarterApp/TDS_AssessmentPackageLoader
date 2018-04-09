package tds.support.tool.validation;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;
import tds.common.Algorithm;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.Item;
import tds.testpackage.model.ItemScoreDimension;
import tds.testpackage.model.TestPackage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemScoreDimensionValidator implements TestPackageValidator {
    private static final Set<String> RECOGNIZED_MEASUREMENT_MODELS = ImmutableSet.of(
            "IRT3PLN",
            "IRTPCL",
            "RAW",
            "RAWSCORE",
            "IRT3PL",
            "IRTGPC"
    );

    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        List<ItemScoreDimension> itemScoreDimensions = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream()
                        .flatMap(segment -> {
                            if (segment.getAlgorithmType().equalsIgnoreCase(Algorithm.FIXED_FORM.getType())) {
                                return segment.segmentForms().stream()
                                        .flatMap(segmentForm -> segmentForm.itemGroups().stream()
                                                .flatMap(itemGroup -> itemGroup.items().stream()
                                                        .map(Item::getItemScoreDimension)
                                                )
                                        );
                            } else { // assume adaptive
                                return segment.pool().stream()
                                        .flatMap(itemGroup -> itemGroup.items().stream()
                                                .map(Item::getItemScoreDimension)
                                        );
                            }
                        }))
                .collect(Collectors.toList());

        itemScoreDimensions.stream()
                .filter(itemScoreDimension -> !RECOGNIZED_MEASUREMENT_MODELS.contains(itemScoreDimension.getMeasurementModel().toUpperCase()))
                .forEach(itemScoreDimension -> errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                        String.format("An unrecognized measurement model %s was detected in the test package", itemScoreDimension.getMeasurementModel()))));
    }
}
