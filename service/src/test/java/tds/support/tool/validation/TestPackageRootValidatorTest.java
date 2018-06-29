package tds.support.tool.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tds.support.job.ErrorSeverity;
import tds.support.tool.configuration.SupportToolProperties;
import tds.testpackage.model.TestPackage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestPackageRootValidatorTest extends TestPackageValidationBaseTest{
    private TestPackageRootValidator validator;

    @Mock
    private SupportToolProperties supportToolProperties;

    @Before
    public void setup() {
        when(supportToolProperties.getSubjects()).thenReturn(
            new ArrayList<String>(Arrays.asList(new String[] {"FOO", "BAZ", "MATH"})));
        validator = new TestPackageRootValidator(supportToolProperties);
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

        assertThat(errors).hasSize(3);

        // TDS-1659 - some test suite files contained 'Mathematics' instead of 'MATH'.
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(0).getMessage()).isEqualTo(
            "The test package subject 'MATHEMATICS' must be one of: [FOO, BAZ, MATH]");

        assertThat(errors.get(1).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(1).getMessage()).isEqualTo(
            "The test package version must be a long or integer value");

        assertThat(errors.get(2).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(2).getMessage()).isEqualTo(
            "The test package \"publishdate\" is not in a valid date format. Example format: 2015-08-19T22:44:00Z");
    }

}
