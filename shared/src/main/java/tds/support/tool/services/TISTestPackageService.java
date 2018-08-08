package tds.support.tool.services;

import tds.common.ValidationError;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

public interface TISTestPackageService {
    /**
     * Loads a {@link TestPackage} into TIS
     *
     * @param name,       The name of the test package (usually the file name without the extension)
     * @param testPackage The {@link TestPackage} to load
     * @return A validation error, if one occurs during load
     */
    Optional<ValidationError> loadTestPackage(final String name, final TestPackage testPackage);

    /**
     * Deletes a {@link TestPackage} from TIS
     *
     * @param testPackage The test package to delete
     */
    void deleteTestPackage(final TestPackage testPackage);
}
