package tds.support.tool.repositories.loader;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.InputStream;


/**
 * Handles storing the test package file
 */
public interface TestPackageRepository {
    /**
     * Saves the test package for the job
     *
     * @param jobId           the unique job identifier
     * @param packageName     the test package file name
     * @param inputStream     the {@link java.io.InputStream} with the test package contents
     * @param testPackageSize the size of the content
     * @return the location of the test package
     */
    String savePackage(final String jobId, final String packageName, final InputStream inputStream, long testPackageSize);
}
