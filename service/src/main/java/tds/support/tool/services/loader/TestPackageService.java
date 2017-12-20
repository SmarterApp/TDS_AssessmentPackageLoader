package tds.support.tool.services.loader;

import java.io.InputStream;

import tds.support.tool.model.TestPackageMetadata;

/**
 * Service representing the operations one can take on a test package.
 */
public interface TestPackageService {
    /**
     * Saves the test package for the job
     *
     * @param jobId                  the unique job identifier
     * @param packageName            the test package file name
     * @param testPackageInputStream the {@link java.io.InputStream} with the test package contents
     * @param testPackageSize        the size of the content
     * @return the location of the test package
     */
    TestPackageMetadata saveTestPackage(final String jobId, final String packageName, final InputStream testPackageInputStream, long testPackageSize);
}
