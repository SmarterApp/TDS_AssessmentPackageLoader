package tds.support.tool.handlers.scoring;

import tds.support.job.Job;
import tds.support.job.Step;

public interface TestResultsHandler {
    /**
     * Handles a test results scoring job step
     *
     * @param job  The job containing the step that will be handled
     * @param step The job step to handle
     */
    void handle(final Job job, final Step step);
}
