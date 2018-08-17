package tds.support.tool.validation;

import org.junit.Before;
import org.junit.Test;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolsValidatorTest extends TestPackageValidationBaseTest {
    private ToolsValidator validator;

    @Before
    public void setup() {
        validator = new ToolsValidator();
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
                "/validation/TESTPACKAGE-SAMPLE-INVALID-TOOLS.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(invalidTestPackage, errors);
        assertThat(errors).hasSize(3);
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.WARN);
        assertThat(errors.get(0).getMessage()).isEqualTo("An unrecognized test tool type with the tool name \"New Tool\" was detected");

        assertThat(errors.get(1).getSeverity()).isEqualTo(ErrorSeverity.WARN);
        assertThat(errors.get(1).getMessage()).isEqualTo("The tool 'Expandable Passages' contained an unrecognized ART field name 'Optional[TDSAcc-ExpandablePassagesFoo]'");

        assertThat(errors.get(2).getSeverity()).isEqualTo(ErrorSeverity.WARN);
        assertThat(errors.get(2).getMessage()).isEqualTo("A tool with an unrecognized ISAAP code was detected: TDS_ExpandablePassages2");
    }
}
