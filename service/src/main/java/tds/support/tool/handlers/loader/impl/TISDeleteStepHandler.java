package tds.support.tool.handlers.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import tds.support.job.Error;
import tds.support.job.ErrorSeverity;
import tds.support.job.Job;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.tool.handlers.loader.TestPackageHandler;

@Component
public class TISDeleteStepHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(TISDeleteStepHandler.class);

    @Override
    public void handle(final Job job, final Step step) {
        try {
            //TODO: Call the TIS DELETE API and update the step with results
            step.setStatus(Status.SUCCESS);
        } catch (Exception e) {
            log.error("An error occurred while attempting to process the job step {} for job with ID {}",
                step.getName(), job.getId(), e);

            // TODO: If errors were returned from the request, set those errors in the step here
            step.setStatus(Status.FAIL);
            step.addError(new Error(String.format("Error occurred while communicating with TIS: %s", e.getMessage()),
                    ErrorSeverity.CRITICAL));
        }

        step.setComplete(true);
    }
}
