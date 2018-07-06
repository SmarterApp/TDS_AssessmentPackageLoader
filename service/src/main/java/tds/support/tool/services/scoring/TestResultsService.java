package tds.support.tool.services.scoring;

import tds.support.job.TestResultsScoringJob;
import tds.support.tool.model.TestResultsMetadata;

import java.io.InputStream;

public interface TestResultsService {
    /**
     * Saves the test results for the scoring job
     *
     * @param job                    the test results load job
     * @param testResultsName        the test results file name
     * @param testResultsInputStream the {@link java.io.InputStream} with the test results contents
     * @param testResultsSize        the size of the content
     */
    TestResultsMetadata saveTestResults(final TestResultsScoringJob job, final String testResultsName, final InputStream testResultsInputStream,
                                        final long testResultsSize);
}
