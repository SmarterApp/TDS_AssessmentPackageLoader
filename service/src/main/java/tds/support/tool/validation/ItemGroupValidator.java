package tds.support.tool.validation;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import tds.common.Algorithm;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.ItemGroup;
import tds.testpackage.model.TestPackage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemGroupValidator implements TestPackageValidator {

    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        List<ItemGroup> itemGroups = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream()
                        .flatMap(segment -> {
                            if (segment.getAlgorithmType().equalsIgnoreCase(Algorithm.FIXED_FORM.getType())) {
                                return segment.segmentForms().stream()
                                        .flatMap(segmentForm -> segmentForm.itemGroups().stream());
                            } else { // assume adaptive
                                return segment.pool().stream();
                            }
                        }))
                .collect(Collectors.toList());

        validateMaxItemsAndResponses(errors, itemGroups);
        validateItemGroupIdsAreLongs(errors, itemGroups);
    }

    private void validateMaxItemsAndResponses(final List<ValidationError> errors, final List<ItemGroup> itemGroups) {
        // Validate that the maxItems values are either numeric or "ALL"
        itemGroups.stream()
                .filter(itemGroup -> !itemGroup.maxItems().equalsIgnoreCase("ALL")
                        && !StringUtils.isNumeric(itemGroup.maxItems()))
                .forEach(itemGroup -> errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                        String.format("The item group with id %s contains a \"maxItems\" value that is neither 'ALL' nor numeric.", itemGroup.getId()))));

        // Validate that the maxItems values are either numeric or "ALL"
        itemGroups.stream()
                .filter(itemGroup -> !itemGroup.maxResponses().equalsIgnoreCase("ALL")
                        && !StringUtils.isNumeric(itemGroup.maxResponses()))
                .forEach(itemGroup -> errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                        String.format("The item group with id %s contains a \"maxResponses\" value that is neither 'ALL' nor numeric.", itemGroup.getId()))));
    }

    private void validateItemGroupIdsAreLongs(final List<ValidationError> errors, final List<ItemGroup> itemGroups) {
        itemGroups.forEach(itemGroup -> {
            try {
                Long.parseLong(itemGroup.getId());
            } catch (NumberFormatException e) {
                errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                        String.format("Unable to parse the item group id %s into a LONG. Currently, TDS only supports LONG item group ids",
                                itemGroup.getId())));
            }
        });
    }
}