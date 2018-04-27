package tds.support.tool.handlers.loader;

import tds.support.job.Job;
import tds.support.job.Step;

/**
 * An interface for all Test package handler implementations. A test package handler is meant to handle a particular
 * step for a test package job
 */
public interface TestPackageHandler {
    /**
     * Handles a test package job step
     *
     * @param job  The job containing the step that will be handled
     * @param step The job step to handle
     */
    void handle(final Job job, final Step step);
}
