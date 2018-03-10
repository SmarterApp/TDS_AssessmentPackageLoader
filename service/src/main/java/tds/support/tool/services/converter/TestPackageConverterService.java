package tds.support.tool.services.converter;

import java.io.File;
import java.io.IOException;

/**
 * An interface for a service responsible for unzipping and deserializing test packages
 */
public interface TestPackageConverterService {
    /**
     * @param testPackageName The name of the test package to convert to
     * @param file            The zip file containing legacy {@link tds.testpackage.legacy.model.Testspecification} files
     *
     * @return An XML blob of the converted {@link tds.testpackage.model.TestPackage}
     * @throws IOException If an error occurs while unzipping the test package zip file, or deserializing the content
     */
    String extractAndConvertTestSpecifications(final String testPackageName, final File file) throws IOException;
}
