package tds.support.tool.validation;

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

    @Test
    public void shouldPassValidation() {
        List<ValidationError> errors = new ArrayList<>();
        when(supportToolProperties.getSubjects()).thenReturn(
            new ArrayList<String>(Arrays.asList(new String[] {"MATH"})));
        validator = new TestPackageRootValidator(supportToolProperties);
        validator.validate(validTestPackage, errors);
        assertThat(errors).isEmpty();
    }

    @Test
    public void shouldPassValidationWithMismatchedCaseSubject() {
        // This assessment has subject 'MATH' which would fail case sensitive validation.
        List<ValidationError> errors = new ArrayList<>();
        when(supportToolProperties.getSubjects()).thenReturn(
            new ArrayList<String>(Arrays.asList(new String[] {"math"})));
        validator = new TestPackageRootValidator(supportToolProperties);
        validator.validate(validTestPackage, errors);
        assertThat(errors).isEmpty();
    }

    @Test
    public void shouldFailValidationForBadSubjectPublisherDateFormat() throws IOException {
        TestPackage invalidTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-INVALID-PUBLISHERDATE-VERSION.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        when(supportToolProperties.getSubjects()).thenReturn(
            new ArrayList<String>(Arrays.asList(new String[] {"foo", "baz", "math"})));
        validator = new TestPackageRootValidator(supportToolProperties);
        validator.validate(invalidTestPackage, errors);

        assertThat(errors).hasSize(3);

        // TDS-1659 - some test suite files contained 'Mathematics' instead of 'MATH'.
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(0).getMessage()).isEqualTo(
            "The test package subject 'MATHEMATICS' must be one of: [foo, baz, math]");

        assertThat(errors.get(1).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(1).getMessage()).isEqualTo(
            "The test package version must be a long or integer value");

        assertThat(errors.get(2).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(2).getMessage()).isEqualTo(
            "The test package \"publishdate\" is not in a valid date format. Example format: 2015-08-19T22:44:00Z");
    }

}
