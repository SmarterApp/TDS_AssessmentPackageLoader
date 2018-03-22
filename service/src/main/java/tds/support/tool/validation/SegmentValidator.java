package tds.support.tool.validation;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;
import tds.common.Algorithm;
import tds.support.job.ErrorSeverity;
import tds.support.tool.utils.ValidationUtils;
import tds.testpackage.model.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SegmentValidator implements TestPackageValidator {
    private static final Set<String> knownAlgorithmTypes =
            ImmutableSet.of(Algorithm.FIXED_FORM.getType(), Algorithm.ADAPTIVE.getType());

    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        List<Segment> segments = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream())
                .collect(Collectors.toList());
        validateSegmentIdLength(segments, errors);
        validateSegmentsHaveUniqueIds(testPackage, errors);
        validateFixedFormSegmentsHaveSegmentForms(segments, errors);
        validateAdaptiveSegmentsHaveItemPool(segments, errors);
        validateKnownAlgorithms(segments, errors);

    }

    private static void validateKnownAlgorithms(final List<Segment> segments, final List<ValidationError> validationErrors) {
        segments.stream()
                .filter(segment -> !knownAlgorithmTypes.contains(segment.getAlgorithmType()))
                .forEach(segment -> validationErrors.add(
                        new ValidationError(ErrorSeverity.WARN, "Unrecognized algorithm type found for segment " + segment.getId())));
    }

    private static void validateSegmentsHaveUniqueIds(final TestPackage testPackage,
                                                      final List<ValidationError> validationErrors) {
        testPackage.getAssessments().stream()
                .filter(Assessment::isSegmented)
                .flatMap(assessment -> assessment.getSegments().stream()
                        .filter(segment -> assessment.getId().equalsIgnoreCase(segment.getId())))
                .forEach(segment -> validationErrors.add(new ValidationError(ErrorSeverity.CRITICAL, String.format("An assessment with more than one segment cannot have an assessment " +
                        "id that matches a segment id. Segment ids must be unique. Assessment id: %s", segment.getId()))));

    }

    private static void validateSegmentIdLength(final List<Segment> segments, final List<ValidationError> errors) {
        segments.stream()
                .filter(segment -> !ValidationUtils.isNotNullAndHasMaxLength(segment.getId(), 255))
                .forEach(segment -> errors.add(new ValidationError(ErrorSeverity.CRITICAL, "Segment ids must be less than 255 characters long")));
    }

    private static void validateFixedFormSegmentsHaveSegmentForms(final List<Segment> segments, final List<ValidationError> validationErrors) {
        List<Segment> fixedFormSegmentsWithNoForms = segments.stream()
                .filter(segment -> segment.getAlgorithmType().equalsIgnoreCase(Algorithm.FIXED_FORM.getType())
                        && segment.segmentForms().isEmpty())
                .collect(Collectors.toList());

        fixedFormSegmentsWithNoForms.forEach(segment -> validationErrors.add(
                new ValidationError(ErrorSeverity.CRITICAL, String.format("The segment %s has an algorithm type of \"fixed form\" " +
                        "but did not contain any forms", segment.getId()))));
    }

    private static void validateAdaptiveSegmentsHaveItemPool(final List<Segment> segments, final List<ValidationError> validationErrors) {
        List<Segment> catSegmentsWithNoPool = segments.stream()
                .filter(segment -> segment.getAlgorithmType().contains(Algorithm.ADAPTIVE.getType())
                        && segment.pool().isEmpty())
                .collect(Collectors.toList());

        catSegmentsWithNoPool.forEach(segment -> validationErrors.add(
                new ValidationError(ErrorSeverity.CRITICAL, String.format("The segment %s has an algorithm type of \"adaptive\" " +
                        "but did not contain any item pool defined", segment.getId()))));
    }
}
