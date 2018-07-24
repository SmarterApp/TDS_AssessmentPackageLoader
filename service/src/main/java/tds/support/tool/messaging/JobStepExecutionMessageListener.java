package tds.support.tool.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.services.TestPackageJobService;
import tds.support.tool.services.TestResultsJobService;

@Component
public class JobStepExecutionMessageListener {
    private static final Logger log = LoggerFactory.getLogger(JobStepExecutionMessageListener.class);
    private final TestPackageJobService testPackageJobService;
    private final TestResultsJobService testResultsJobService;
    private final JobRepository jobRepository;

    @Autowired
    public JobStepExecutionMessageListener(final JobRepository jobRepository,
                                           final TestResultsJobService testResultsJobService,
                                           final TestPackageJobService testPackageJobService) {
        this.testPackageJobService = testPackageJobService;
        this.testResultsJobService = testResultsJobService;
        this.jobRepository = jobRepository;
    }

    public void handleMessage(final String jobId) {
        log.debug("Processing message for jobId: " + jobId);

        try {
            final Job job = jobRepository.findOne(jobId);

            if (job.getType() == JobType.SCORING) {
                testResultsJobService.executeJobSteps(jobId);
            } else {
                testPackageJobService.executeJobSteps(jobId);
            }
        } catch (Exception e) {
            log.error("An exception occurred while processing the job execution message for job ID {}", jobId, e);
        }
    }
}
