package tds.support.tool.handlers.loader.impl;

import org.springframework.stereotype.Component;

import tds.support.job.Job;
import tds.support.job.Step;
import tds.support.tool.handlers.loader.TestPackageHandler;

@Component
public class ParseAndValidateHandler implements TestPackageHandler {

    @Override
    public void handle(final Job job, final Step step) {
        return;
    }
}
