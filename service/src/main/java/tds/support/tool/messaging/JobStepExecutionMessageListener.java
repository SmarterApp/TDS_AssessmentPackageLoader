package tds.support.tool.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tds.support.tool.services.JobService;

@Component
public class JobStepExecutionMessageListener {
    private static final Logger log = LoggerFactory.getLogger(JobStepExecutionMessageListener.class);
    private final JobService jobService;

    @Autowired
    public JobStepExecutionMessageListener(final JobService jobService) {
        this.jobService = jobService;
    }

    public void handleMessage(final String jobId) {
        log.debug("Processing message for jobId: " + jobId);
        jobService.executeJobSteps(jobId);
    }
}
