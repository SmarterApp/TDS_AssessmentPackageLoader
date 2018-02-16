package tds.support.tool.services;

import tds.common.ValidationError;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

public interface TDSTestPackageService {
    Optional<ValidationError> loadTestPackage(final String name, final TestPackage testPackage);

    Optional<ValidationError> deleteTestPackage(final TestPackage testPackage);
}
