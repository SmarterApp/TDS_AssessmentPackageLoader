package tds.support.tool.validation;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;
import tds.common.Algorithm;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.Item;
import tds.testpackage.model.TestPackage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemValidator implements TestPackageValidator {
    private static final Set<String> RECOGNIZED_ITEM_TYPES = ImmutableSet.of(
            "ER",
            "GI",
            "MI",
            "MS",
            "EQ",
            "MC",
            "TI",
            "SA",
            "EBSR",
            "WER",
            "HTQ"
    );

    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        // Validate that stimuli id are parseable to long values
        List<Item> items = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream()
                        .flatMap(segment -> {
                            if (segment.getAlgorithmType().equalsIgnoreCase(Algorithm.FIXED_FORM.getType())) {
                                return segment.segmentForms().stream()
                                        .flatMap(segmentForm -> segmentForm.itemGroups().stream()
                                                .flatMap(itemGroup -> itemGroup.items().stream())
                                        );
                            } else { // assume adaptive
                                return segment.pool().stream()
                                        .flatMap(itemGroup -> itemGroup.items().stream());
                            }
                        }))
                .collect(Collectors.toList());

        validateItemIdsAreLongs(errors, items);
        validateItemTypesAreKnownTypes(errors, items);
    }

    private void validateItemTypesAreKnownTypes(final List<ValidationError> errors, final List<Item> items) {
        items.stream()
                .filter(item -> !RECOGNIZED_ITEM_TYPES.contains(item.getType().toUpperCase()))
                .forEach(item -> errors.add(new ValidationError(ErrorSeverity.WARN,
                        String.format("The item \"%s\" contained an item type \"%s\" that is not a recognized TDS item type. Known item types include %s",
                                item.getId(), item.getType(), RECOGNIZED_ITEM_TYPES.toString()))));
    }

    private void validateItemIdsAreLongs(final List<ValidationError> errors, final List<Item> items) {
        items.forEach(item -> {
            try {
                Long.parseLong(item.getId());
            } catch (NumberFormatException e) {
                errors.add(new ValidationError(ErrorSeverity.CRITICAL, String.format("The item with id \"%s\" has an id that is not a LONG value. " +
                        "Currently, TDS only supports item ids that are of a 'LONG' data type", item.getId())));
            }
        });
    }
}
