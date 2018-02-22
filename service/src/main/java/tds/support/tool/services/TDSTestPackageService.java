package tds.support.tool.services;

import tds.common.ValidationError;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

/**
 * An interface for a service responsible for loading and deleting test packages from TDS
 */
public interface TDSTestPackageService {
    /**
     * Loads a {@link TestPackage} into TDS
     *
     * @param name,       The name of the test package (usually the file name without the extension)
     * @param testPackage The {@link TestPackage} to load
     * @return A validation error, if one occurs during load
     */
    Optional<ValidationError> loadTestPackage(final String name, final TestPackage testPackage);

    /**
     * Deletes a {@link TestPackage} from TDS
     *
     * @param testPackage The test package to delete
     */
    void deleteTestPackage(final TestPackage testPackage);
}
