package tds.support.tool.validation;

import org.springframework.stereotype.Component;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;

import javax.xml.bind.DatatypeConverter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

@Component
public class TestPackageRootValidator implements TestPackageValidator {
    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        validateTestPackageVersionIsLong(testPackage, errors);

        if (!isValidFormat(testPackage.getPublishDate())) {
            errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                    "The test package \"publishdate\" is not in a valid date format. Example format: 2015-08-19T22:44:00Z"));
        }
    }

    private void validateTestPackageVersionIsLong(final TestPackage testPackage, final List<ValidationError> errors) {
        try {
            Long.parseLong(testPackage.getVersion());
        } catch (NumberFormatException e) {
            errors.add(new ValidationError(ErrorSeverity.CRITICAL, "The test package version must be a long or integer value"));
        }
    }

    public static boolean isValidFormat(final String value) {
        try {
            DatatypeConverter.parseDateTime(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
