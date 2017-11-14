package tds.support.tool.handlers.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

import tds.support.job.Error;
import tds.support.job.ErrorSeverity;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.tool.services.loader.TestPackageService;

/**
 * Handles uploading the test package to S3 or other file store location.  This is the first step in the loader job process.
 */
@Component
public class TestPackageFileHandler {
    private final Logger log = LoggerFactory.getLogger(TestPackageFileHandler.class);
    private final TestPackageService testPackageService;

    /**
     * @param testPackageService the {@link tds.support.tool.services.loader.TestPackageService} that handles loading the test package
     */
    @Autowired
    TestPackageFileHandler(final TestPackageService testPackageService) {
        this.testPackageService = testPackageService;
    }

    /**
     * Handles loading the test package.
     *
     * @param jobId           the unique job identifier
     * @param packageName     the name of the package
     * @param testPackage     the {@link java.io.InputStream} with the package contents
     * @param testPackageSize the size of the input stream
     * @return {@link tds.support.job.Step} defining the information for this step in the job
     */
    public Step handleTestPackage(final Step step, final String jobId, final String packageName, final InputStream testPackage, long testPackageSize) {
        try {
            testPackageService.saveTestPackage(jobId, packageName, testPackage, testPackageSize);
        } catch (Exception e) {
            log.error("Unexpected Error uploading the test package file " + testPackage, e);
            step.addError(new Error("Failed to upload file", ErrorSeverity.CRITICAL));
            step.setStatus(Status.FAIL);
        }

        step.setStatus(Status.SUCCESS);

        return step;
    }
}
