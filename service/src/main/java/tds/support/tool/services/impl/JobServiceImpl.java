package tds.support.tool.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tds.support.job.*;
import tds.support.tool.handlers.loader.TestPackageFileHandler;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.services.JobService;
import tds.support.tool.services.TestPackageStatusService;
import tds.support.tool.services.loader.MessagingService;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
        final Job job = new TestPackageLoadJob(packageName, skipArt, skipScoring);

        final Step step = job.getSteps().stream()
                .filter(potentialStep -> TestPackageLoadJob.FILE_UPLOAD.equals(potentialStep.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("First step in the loader job is not correctly configured"));

        step.setStatus(Status.IN_PROGRESS);

        final Job persistedJob = jobRepository.save(job);

        testPackageFileHandler.handleTestPackage(step, persistedJob.getId(), packageName, testPackage, testPackageSize);

        // If we have errors from deserializing  or saving the XML, no need to trigger step execution
        if (step.getErrors().isEmpty()) {
            messagingService.sendJobStepExecute(job.getId());
        } else {
            persistedJob.getSteps().stream()
                    .filter(incompleteSteps -> !incompleteSteps.isComplete())
                    .forEach(incompleteSteps -> incompleteSteps.setStatus(Status.FAIL));
        }

        testPackageStatusService.save(persistedJob);

        // Update the job to indicate the test package XML file has been stored in the database, meaning the downstream
        // systems can now be loaded
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
        final Job job = jobRepository.findOne(jobId);

        // Handle each job step
        job.getSteps().stream()
                .filter(step -> !step.isComplete())
                .forEach(step -> {
                    if (testPackageLoaderStepHandlers.containsKey(step.getName())) {
                        // Initialize the step status to "in progress"
                        step.setStatus(Status.IN_PROGRESS);
                        jobRepository.save(job);

                        testPackageLoaderStepHandlers.get(step.getName()).handle(job, step);

                        // Update job after each step has been processed to set step status/errors
                        jobRepository.save(job);

                        // If we failed validation, lets bail out and mark remaining steps as "failed"
                        if (step.getName().equals(TestPackageLoadJob.VALIDATE) && step.getStatus() == Status.FAIL) {
                            job.getSteps().stream()
                                    .filter(nonValidationSteps -> nonValidationSteps.getJobStepTarget() != TargetSystem.Internal)
                                    .forEach(nonValidationSteps -> nonValidationSteps.setStatus(Status.FAIL));

                            jobRepository.save(job);
                             throw new RuntimeException("Error: The test package failed validation. Aborting test package load.");
                        }

                        if (job.getType().equals(JobType.LOAD)) {
                            testPackageStatusService.save(job);
                        }

                    } else {
                        log.error("Attempting to call the step {} when it has not been registered to the loader step handler map.", step.getName());
                    }
                });

        // Only rollback failed "LOAD" jobs.  When the ROLLBACK job for this failed LOAD job runs, the status record
        // will be deleted.
        if (job.getType() == JobType.LOAD && hasJobStepFailure(job)) {
            createRollbackJobFromFailedJob((TestPackageLoadJob) job);
        }

        // Delete the test package's status record if this is a DELETE or ROLLBACK job
        if (job.getType().equals(JobType.DELETE) || job.getType().equals(JobType.ROLLBACK)) {
            testPackageStatusService.delete(job.getName());
        }
    }

    @Override
    public void startPackageDelete(final String testPackageName) {
        final Job mostRecentLoaderJob = jobRepository.findOneByNameAndTypeOrderByCreatedAtDesc(testPackageName,
                JobType.LOAD);

        // If there's no previous loader job for the specified test package, exit (because there's nothing to do).
        if (mostRecentLoaderJob == null) {
            return;
        }

        // Create a delete job from the most recent load job for the same test package.
        final TestPackageLoadJob loadJobForPackageToDelete = (TestPackageLoadJob) mostRecentLoaderJob;
        final Job persistedDeleteJob = jobRepository.save(new TestPackageDeleteJob(testPackageName,
                loadJobForPackageToDelete.isSkipArt(),
                loadJobForPackageToDelete.isSkipScoring()));

        // Update the test package status to indicate it is now being handled  by a DELETE job.
        testPackageStatusService.save(persistedDeleteJob);

        messagingService.sendJobStepExecute(persistedDeleteJob.getId());
    }

    private boolean hasJobStepFailure(final Job job) {
        return job.getSteps().stream()
                .anyMatch(step -> step.getStatus() == Status.FAIL);
    }

    private void createRollbackJobFromFailedJob(final TestPackageLoadJob job) {
        if (job.getStatus() != Status.FAIL) {
            log.warn("Attempting to create a rollback job from a successful job");
            return;
        }

        // Create a "delete" rollback job based on the failed loader job's options
        Job rollbackJob = new TestPackageRollbackJob(job.getId(), job.getName(), job.isSkipArt(), job.isSkipScoring());
        rollbackJob = jobRepository.save(rollbackJob);
        messagingService.sendJobStepExecute(rollbackJob.getId());
    }
}
