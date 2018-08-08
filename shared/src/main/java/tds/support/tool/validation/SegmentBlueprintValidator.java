package tds.support.tool.validation;

import org.springframework.stereotype.Component;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.Segment;
import tds.testpackage.model.SegmentBlueprintElement;
import tds.testpackage.model.TestPackage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SegmentBlueprintValidator implements TestPackageValidator {
    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        List<SegmentBlueprintElement> segmentBlueprintElements = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream()
                        .flatMap(segment -> segment.segmentBlueprint().stream()))
                .collect(Collectors.toList());


        validateMinExamItemCountLessThanMax(segmentBlueprintElements, errors);
        validateMinFieldTestItemCountLessThanMax(segmentBlueprintElements, errors);
        validateTestPackageContainsSegmentBlueprintForSegmentId(testPackage, errors);
    }

    private void validateTestPackageContainsSegmentBlueprintForSegmentId(final TestPackage testPackage, final List<ValidationError> validationErrors) {
        Set<String> segmentIds = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream()
                        .map(Segment::getId))
                .collect(Collectors.toSet());

        Set<String> segmentBlueprintElementIds = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream()
                        .flatMap(segment -> segment.segmentBlueprint().stream()
                                .map(SegmentBlueprintElement::getIdRef)
                        ))
                .collect(Collectors.toSet());

        segmentIds.stream()
                .filter(segmentId -> !segmentBlueprintElementIds.contains(segmentId))
                .forEach(segmentId -> validationErrors.add(new ValidationError(ErrorSeverity.CRITICAL,
                        String.format("No segment blueprint element was found with the id for the segment %s", segmentId))));
    }

    private void validateMinExamItemCountLessThanMax(final List<SegmentBlueprintElement> segmentBlueprintElements,
                                                     final List<ValidationError> validationErrors) {
        segmentBlueprintElements.stream()
                .filter(segmentBp -> segmentBp.getMinExamItems() > segmentBp.getMaxExamItems())
                .forEach(segmentBp -> validationErrors.add(
                        new ValidationError(ErrorSeverity.CRITICAL, String.format("Cannot have a minExamItem value that " +
                                "is greater than the maxExamItem value for segment blueprint element %s", segmentBp))));
    }


    private void validateMinFieldTestItemCountLessThanMax(final List<SegmentBlueprintElement> segmentBlueprintElements,
                                                          final List<ValidationError> validationErrors) {
        segmentBlueprintElements.stream()
                .filter(segmentBp -> segmentBp.minFieldTestItems() > segmentBp.maxFieldTestItems())
                .forEach(segmentBp -> validationErrors.add(
                        new ValidationError(ErrorSeverity.CRITICAL, String.format("Cannot have a minFieldTestItem value that " +
                                "is greater than the maxFieldTestItem value for segment blueprint element %s", segmentBp))));
    }
}
