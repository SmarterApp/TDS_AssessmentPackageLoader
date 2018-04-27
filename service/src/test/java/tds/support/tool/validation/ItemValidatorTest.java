package tds.support.tool.validation;

import org.junit.Before;
import org.junit.Test;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemValidatorTest extends TestPackageValidationBaseTest {
    private ItemValidator validator;

    @Before
    public void setup() {
        validator = new ItemValidator();
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
                "/validation/TESTPACKAGE-SAMPLE-INVALID-ITEMS.xml")
                , TestPackage.class);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(invalidTestPackage, errors);

        assertThat(errors).hasSize(2);
        assertThat(errors.get(0).getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(errors.get(0).getMessage()).isEqualTo("The item with id \"1434-A\" has an id that is not a LONG value. Currently, TDS only supports item ids that are of a 'LONG' data type");

        assertThat(errors.get(1).getSeverity()).isEqualTo(ErrorSeverity.WARN);
        assertThat(errors.get(1).getMessage()).isEqualTo("The item \"1432\" contained an item type \"ERNIE\" that is not a recognized TDS item type. Known item types include [ER, GI, MI, MS, EQ, MC, TI, SA, EBSR, WER, HTQ]");
    }
}
