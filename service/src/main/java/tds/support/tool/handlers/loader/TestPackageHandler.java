package tds.support.tool.handlers.loader;

import tds.support.job.Job;
import tds.support.job.Step;

public interface TestPackageHandler {
    Step handle(Job job, Step step);
}
