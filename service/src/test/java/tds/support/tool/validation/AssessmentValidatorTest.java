package tds.support.tool.validation;

import org.junit.Before;
import org.junit.Test;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AssessmentValidatorTest extends TestPackageValidationBaseTest{
    private AssessmentValidator validator;

    @Before
    public void setup() {
        validator = new AssessmentValidator();
    }

    @Test
    public void shouldPassValidation() {
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(validTestPackage, errors);
        assertThat(errors).isEmpty();
    }

    @Test
    public void shouldFailValidationForAssessmentIdWithLargeName() throws IOException {
        TestPackage invalidTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-INVALID-LONG-ASSESSMENTID.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(invalidTestPackage, errors);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(0).getMessage()).isEqualTo("A 'test' BlueprintElement was not found in the test blueprint for the test with id 'SOMETHINGSUPERLONGSOMETHINGSUPERLONGSOMETHINGSUPERLONGSOMETHINGSUPERLONGSOMETHINGSUPERLONGSOMETHINGSUPERLONGSOMETHINGSUPERLONGSOMETHINGSUPERLONGSOMETHINGSUPERLONG'.");
    }

    @Test
    public void shouldFailValidationForAssessmentWithNoMatchingBpElement() throws IOException {
        TestPackage invalidTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-INVALID-MISMATCH-ASSESSMENTID.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(invalidTestPackage, errors);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(0).getMessage()).isEqualTo("A 'test' BlueprintElement was not found in the test blueprint for the test with id 'A MismatchedId'.");
    }
}
