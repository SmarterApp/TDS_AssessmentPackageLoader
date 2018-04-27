package tds.support.tool.validation;

import org.junit.Before;
import org.junit.Test;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BlueprintValidatorTest extends TestPackageValidationBaseTest {
    private BlueprintValidator validator;

    @Before
    public void setup() {
        validator = new BlueprintValidator();
    }

    @Test
    public void shouldPassValidation() {
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(validTestPackage, errors);
        assertThat(errors).isEmpty();
    }

    @Test
    public void shouldFailValidationUnknownTestToolTypes() throws IOException {
        TestPackage invalidTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-INVALID-BLUEPRINT.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(invalidTestPackage, errors);
        assertThat(errors).hasSize(7);
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.WARN);
        assertThat(errors.get(0).getMessage()).isEqualTo("A top-level blueprint element type of target was detected for blueprint element with id of 'foo'. Recognized top-level blueprint element types include: [affinitygroup, claim, package, sock, test]");

        assertThat(errors.get(1).getSeverity()).isEqualTo(ErrorSeverity.WARN);
        assertThat(errors.get(1).getMessage()).isEqualTo("A top-level blueprint element type of strand was detected for blueprint element with id of '4'. Recognized top-level blueprint element types include: [affinitygroup, claim, package, sock, test]");

        assertThat(errors.get(2).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(2).getMessage()).isEqualTo("Child elements of a 'package' blueprint element should be of a 'test' blueprint element type");

        assertThat(errors.get(3).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(3).getMessage()).isEqualTo("A 'test' type blueprint element with the id 'SBAC-IRP-CAT-MATH-11-EXAMPLE' was identified, but no corresponding <Test> element was identified with a matching id");

        assertThat(errors.get(4).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(4).getMessage()).isEqualTo("Child elements of a 'claim' or 'target' blueprint element should be of a 'target' blueprint element type");

        assertThat(errors.get(5).getSeverity()).isEqualTo(ErrorSeverity.WARN);
        assertThat(errors.get(5).getMessage()).isEqualTo("A blueprint type of 'strand' was detected for the blueprint element '4'. It is recommended that the blueprint element type is renamed to 'claim'");

        assertThat(errors.get(6).getSeverity()).isEqualTo(ErrorSeverity.WARN);
        assertThat(errors.get(6).getMessage()).isEqualTo("A blueprint type of 'contentlevel' was detected for the blueprint element '4|S-ID'. It is recommended that the blueprint element type is renamed to 'target'");
    }
}
