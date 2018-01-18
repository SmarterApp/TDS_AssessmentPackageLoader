package tds.support.tool.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageLoadJob;
import tds.support.job.TestPackageRollbackJob;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.handlers.loader.TestPackageFileHandler;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.repositories.loader.TestPackageStatusRepository;
import tds.support.tool.services.JobService;
import tds.support.tool.services.TestPackageStatusService;
import tds.support.tool.services.loader.MessagingService;

@Service
public class JobServiceImpl implements JobService {
    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);
    private final JobRepository jobRepository;
    private final TestPackageFileHandler testPackageFileHandler;
    private final Map<String, TestPackageHandler> testPackageLoaderStepHandlers;
    private final MessagingService messagingService;
    private final TestPackageStatusService testPackageStatusService;

    @Autowired
    public JobServiceImpl(final JobRepository jobRepository,
                          final TestPackageFileHandler testPackageFileHandler,
                          final MessagingService messagingService,
                          final TestPackageStatusService testPackageStatusService,
                          @Qualifier("testPackageLoaderStepHandlers") final Map<String, TestPackageHandler> testPackageLoaderStepHandlers) {
        this.jobRepository = jobRepository;
        this.testPackageFileHandler = testPackageFileHandler;
        this.testPackageLoaderStepHandlers = testPackageLoaderStepHandlers;
        this.messagingService = messagingService;
        this.testPackageStatusService = testPackageStatusService;
    }

    @Override
    public Job startPackageImport(final String packageName, final InputStream testPackage, final long testPackageSize,
                                  final boolean skipArt, final boolean skipScoring) {
        Job job = new TestPackageLoadJob(packageName, skipArt, skipScoring);

        Step step = job.getSteps().stream()
            .filter(potentialStep -> TestPackageLoadJob.FILE_UPLOAD.equals(potentialStep.getName()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("First step in the loader job is not correctly configured"));

        step.setStatus(Status.IN_PROGRESS);

        Job persistedJob = jobRepository.save(job);

        testPackageFileHandler.handleTestPackage(step, persistedJob.getId(), packageName, testPackage, testPackageSize);

        messagingService.sendJobStepExecute(job.getId());

        testPackageStatusService.create(persistedJob, packageName);

        //Publish
        return jobRepository.save(persistedJob);
    }

    @Override
    public List<Job> findJobs(final JobType... jobTypes) {
        if (jobTypes != null) {
            return jobRepository.findByTypeIn(jobTypes);
        }

        return jobRepository.findAll();
    }

    @Override
    @Async
    public void executeJobSteps(final String jobId) {
        Job job = jobRepository.findOne(jobId);
        // Set each step aside from FILE_UPLOAD to "in progress" status prior to execution
        job.getSteps().stream()
            .filter(step -> !step.getName().equals(TestPackageLoadJob.FILE_UPLOAD))
            .forEach(step -> step.setStatus(Status.IN_PROGRESS));
        jobRepository.save(job);

        // Handle each job step
        job.getSteps().stream()
            .filter(step -> !step.isComplete())
            .forEach(step -> {
                if (testPackageLoaderStepHandlers.containsKey(step.getName())) {
                    testPackageLoaderStepHandlers.get(step.getName()).handle(job, step);
                } else {
                    log.error("Attempting to call the step {} when it has not been registered to the loader step handler map.", step.getName());
                }
            });

        // Only rollback failed "LOADER" jobs
        if (job.getType() == JobType.LOADER && hasJobStepFailure(job)) {
            createRollbackJobFromFailedJob((TestPackageLoadJob) job);
        }

        // Update job after each step has been processed to set step status/errors
        jobRepository.save(job);
    }

    private boolean hasJobStepFailure(final Job job) {
        return job.getSteps().stream()
            .filter(step -> step.getStatus() == Status.FAIL)
            .findFirst()
            .isPresent();
    }

    private void createRollbackJobFromFailedJob(final TestPackageLoadJob job) {
        if (job.getStatus() != Status.FAIL) {
            log.warn("Attempting to create a rollback job from a successful job");
            return;
        }

        // Create a "delete" rollback job based on the failed loader job's options
        Job rollbackJob = new TestPackageRollbackJob(job.getId(), job.getTestPackageFileName(), job.isSkipArt(), job.isSkipScoring());
        rollbackJob = jobRepository.save(rollbackJob);
        messagingService.sendJobStepExecute(rollbackJob.getId());
    }
}
