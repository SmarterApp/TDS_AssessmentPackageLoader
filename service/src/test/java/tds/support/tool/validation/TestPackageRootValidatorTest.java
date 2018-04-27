package tds.support.tool.validation;

import org.junit.Before;
import org.junit.Test;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestPackageRootValidatorTest extends TestPackageValidationBaseTest{
    private TestPackageRootValidator validator;

    @Before
    public void setup() {
        validator = new TestPackageRootValidator();
    }

    @Test
    public void shouldPassValidation() {
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(validTestPackage, errors);
        assertThat(errors).isEmpty();
    }

    @Test
    public void shouldFailValidationForBadPublisherDateFormat() throws IOException {
        TestPackage invalidTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-INVALID-PUBLISHERDATE-VERSION.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(invalidTestPackage, errors);

        assertThat(errors).hasSize(2);
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(0).getMessage()).isEqualTo("The test package version must be a long or integer value");

        assertThat(errors.get(1).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(1).getMessage()).isEqualTo("The test package \"publishdate\" is not in a valid date format. Example format: 2015-08-19T22:44:00Z");
    }

}
