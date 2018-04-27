package tds.support.tool.validation;

import org.junit.Before;
import org.junit.Test;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemGroupValidatorTest extends TestPackageValidationBaseTest {
    private ItemGroupValidator validator;

    @Before
    public void setup() {
        validator = new ItemGroupValidator();
    }

    @Test
    public void shouldPassValidation() {
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(validTestPackage, errors);
        assertThat(errors).isEmpty();
    }

    @Test
    public void shouldFailValidationForItemGroups() throws IOException {
        TestPackage invalidTestPackage = testPackageMapper.readValue(this.getClass().getResourceAsStream(
                "/validation/TESTPACKAGE-SAMPLE-INVALID-ITEMGROUP.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(invalidTestPackage, errors);
        assertThat(errors).hasSize(3);
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(0).getMessage()).isEqualTo("The item group with id 1234 contains a \"maxItems\" value that is neither 'ALL' nor numeric.");

        assertThat(errors.get(1).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(1).getMessage()).isEqualTo("The item group with id 1234 contains a \"maxResponses\" value that is neither 'ALL' nor numeric.");

        assertThat(errors.get(2).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(2).getMessage()).isEqualTo("Unable to parse the item group id 187-3688 into a LONG. Currently, TDS only supports LONG item group ids");
    }
}
