package tds.support.tool.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.util.Collections;
import java.util.List;

import tds.support.job.ErrorSeverity;
import tds.support.tool.configuration.SupportToolProperties;
import tds.testpackage.model.TestPackage;

@Component
public class TestPackageRootValidator implements TestPackageValidator {

    private static final Logger logger = LoggerFactory.getLogger(TestPackageRootValidator.class);
    private final List<String> subjects;

    @Autowired
    public TestPackageRootValidator(final SupportToolProperties properties) {
        if(properties.getSubjects() != null && properties.getSubjects().size() > 0) {
            this.subjects = properties.getSubjects();
        } else {
            this.subjects = Collections.emptyList();
            logger.error("No test package subjects defined in application.yml - all packages will fail validation!");
        };
    }

    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        if (!subjects.contains(testPackage.getSubject())) {
            errors.add(new ValidationError(ErrorSeverity.CRITICAL,
                String.format("The test package subject '%s' must be one of: %s",
                    testPackage.getSubject(), subjects)));
        }

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
