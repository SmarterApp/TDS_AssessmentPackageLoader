package tds.support.tool.services;

import tds.common.ValidationError;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

/**
 * An interface for a service responsible for loading and deleting test packages from ART
 */
public interface ARTTestPackageService {
    /**
     * Loads a {@link TestPackage} into ART
     *
     * @param tenantId    The tenantId to load the test packages under
     * @param testPackage The {@link TestPackage} to load
     * @return A validation error, if one occurs during load
     */
    Optional<ValidationError> loadTestPackage(final String tenantId, final TestPackage testPackage);

    /**
     * Deletes a {@link TestPackage} from ART
     *
     * @param testPackage The {@link TestPackage} to delete
     */
    void deleteTestPackage(final TestPackage testPackage);
}
