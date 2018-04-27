package tds.support.tool.validation;

import org.junit.Before;
import org.junit.Test;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SegmentFormValidatorTest extends TestPackageValidationBaseTest {
    private SegmentFormValidator validator;

    @Before
    public void setup() {
        validator = new SegmentFormValidator();
    }

    @Test
    public void shouldPassValidation() {
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(validTestPackage, errors);
        assertThat(errors).isEmpty();
    }

    @Test
    public void shouldFailValidationForSegmentForms() throws IOException {
        TestPackage invalidTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-INVALID-SEGMENTFORM.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(invalidTestPackage, errors);
        assertThat(errors).hasSize(2);
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(0).getMessage()).isEqualTo("Multiple segment forms with the id IRP::MathG11::Perf::SP15 were detected. Each segment form must contain a unique id.");
        assertThat(errors.get(1).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(1).getMessage()).isEqualTo("An item contained a presentation code \"ESN\" that was not declared at the segment form level. A segment form presentation list must contain the set of all presentation codes within its contained items");
    }
}
