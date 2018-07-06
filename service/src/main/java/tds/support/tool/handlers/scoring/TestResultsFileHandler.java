package tds.support.tool.handlers.scoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tds.support.job.*;
import tds.support.job.Error;
import tds.support.tool.handlers.loader.TestPackageFileHandler;
import tds.support.tool.services.scoring.TestResultsService;

import java.io.InputStream;

@Component
public class TestResultsFileHandler {
    private final Logger log = LoggerFactory.getLogger(TestPackageFileHandler.class);
    private final TestResultsService testResultsService;

    @Autowired
    TestResultsFileHandler(final TestResultsService testResultsService) {
        this.testResultsService = testResultsService;
    }

    /**
     * Handles loading the test package.
     *
     * @param job             the test results load job
     * @param testResultsName the name of the package
     * @param testResults     the {@link java.io.InputStream} with the package contents
     * @param testResultsSize the size of the input stream
     * @return {@link tds.support.job.Step} defining the information for this step in the job
     */
    public Step handleTestResults(final Step step, final TestResultsScoringJob job, final String testResultsName,
                                  final InputStream testResults, long testResultsSize) {
        try {
            testResultsService.saveTestResults(job, testResultsName, testResults, testResultsSize);
            step.setStatus(Status.SUCCESS);
        } catch (Exception e) {
            log.error("Unexpected Error uploading the test package file " + testResults, e);
            step.setStatus(Status.FAIL);
            step.addError(new Error(e.getMessage(), ErrorSeverity.CRITICAL));
        }
        step.setComplete(true);

        return step;
    }
}
