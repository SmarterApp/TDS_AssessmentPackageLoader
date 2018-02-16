package tds.support.tool.services;

import tds.common.ValidationError;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

public interface ARTTestPackageService {
    Optional<ValidationError> loadTestPackage(final String tenantId, final TestPackage testPackage);

    Optional<ValidationError> deleteTestPackage(final TestPackage testPackage);
}
