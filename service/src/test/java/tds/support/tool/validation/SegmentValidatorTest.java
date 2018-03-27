package tds.support.tool.validation;

import org.junit.Before;
import org.junit.Test;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SegmentValidatorTest extends TestPackageValidationBaseTest {
    private SegmentValidator validator;

    @Before
    public void setup() {
        validator = new SegmentValidator();
    }

    @Test
    public void shouldPassValidation() {
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(validTestPackage, errors);
        assertThat(errors).isEmpty();
    }

    @Test
    public void shouldFailValidation() throws IOException {
        TestPackage invalidTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-INVALID-SEGMENT.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(invalidTestPackage, errors);
        assertThat(errors).hasSize(5);
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(0).getMessage()).isEqualTo("Segment ids must be less than 255 characters long");

        assertThat(errors.get(1).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(1).getMessage()).isEqualTo("An assessment with more than one segment cannot have an assessment id that matches a segment id. Segment ids must be unique. Assessment id: SBAC-IRP-Perf-MATH-11-EXAMPLE");

        assertThat(errors.get(2).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(2).getMessage()).isEqualTo("The segment SBAC-IRP-Perf-MATH-11-EXAMPLE has an algorithm type of \"fixed form\" but did not contain any forms");

        assertThat(errors.get(3).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(3).getMessage()).isEqualTo("The segment SOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONGSOMETHINGVERYLONG has an algorithm type of \"adaptive\" but did not contain any item pool defined");

        assertThat(errors.get(4).getSeverity()).isEqualTo(ErrorSeverity.WARN);
        assertThat(errors.get(4).getMessage()).isEqualTo("Unrecognized algorithm type found for segment SBAC-IRP-Perf-MATH-11-EXAMPLE-SEG2");

    }
}
