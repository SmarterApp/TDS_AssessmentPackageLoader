package tds.support.tool.validation;

import tds.testpackage.model.TestPackage;

import java.util.List;

public interface TestPackageValidator {
    void validate(final TestPackage testPackage, final List<ValidationError> errors);
}
