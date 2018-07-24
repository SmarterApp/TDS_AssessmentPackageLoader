package tds.support.tool.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tds.support.job.*;
import tds.support.tool.handlers.scoring.TestResultsFileHandler;
import tds.support.tool.handlers.scoring.TestResultsHandler;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.repositories.scoring.MongoTestResultsRepository;
import tds.support.tool.services.TestResultsJobService;
import tds.support.tool.services.loader.MessagingService;
import tds.support.tool.services.scoring.TestResultsService;
import tds.trt.model.TDSReport;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TestResultsJobServiceImpl implements TestResultsJobService {
    private static final Logger log = LoggerFactory.getLogger(TestResultsJobServiceImpl.class);
    private final JobRepository jobRepository;
    private final MongoTestResultsRepository testResultsRepository;
    private final TestResultsService testResultsService;
    private final TestResultsFileHandler testResultsFileHandler;
    private final Map<String, TestResultsHandler> testResultsLoaderStepHandlers;
    private final MessagingService messagingService;

    @Autowired
    public TestResultsJobServiceImpl(final JobRepository jobRepository,
                                     final MongoTestResultsRepository testResultsRepository,
                                     final TestResultsService testResultsService,
                                     final TestResultsFileHandler testResultsFileHandler,
                                     final MessagingService messagingService,
                                     @Qualifier("testResultsLoaderStepHandlers") final Map<String, TestResultsHandler> testResultsLoaderStepHandlers) {
        this.jobRepository = jobRepository;
        this.testResultsRepository = testResultsRepository;
        this.testResultsService = testResultsService;
        this.testResultsFileHandler = testResultsFileHandler;
        this.testResultsLoaderStepHandlers = testResultsLoaderStepHandlers;
        this.messagingService = messagingService;
    }

    @Override
    public Job startTestResultsImport(final String packageName, final String username, final InputStream testResults,
                                      final long testResultsSize) {
        final Job job = new TestResultsScoringJob(packageName, username);

        final Step step = job.getSteps().stream()
                .filter(potentialStep -> TestResultsScoringJob.FILE_UPLOAD.equals(potentialStep.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("First step in the loader job is not correctly configured"));

        step.setStatus(Status.IN_PROGRESS);

        final TestResultsScoringJob persistedJob = (TestResultsScoringJob) jobRepository.save(job);

        testResultsFileHandler.handleTestResults(step, persistedJob, packageName, testResults, testResultsSize);

        // If we have errors from de-serializing  or saving the XML, no need to trigger step execution
        if (step.getErrors().isEmpty()) {
            // Update the job to indicate the test results XML file has been stored in the database, meaning the
            // results can be sent to ExamService
            jobRepository.save(persistedJob);
            messagingService.sendJobStepExecute(job.getId());
        } else {
            persistedJob.getSteps().stream()
                    .filter(incompleteSteps -> !incompleteSteps.isComplete())
                    .forEach(incompleteSteps -> incompleteSteps.setStatus(Status.FAIL));
            jobRepository.save(persistedJob);
        }
        return persistedJob;
    }

    @Override
    public void executeJobSteps(final String jobId) {
        final Job job = jobRepository.findOne(jobId);

        // Handle each job step
        job.getSteps().stream()
                .filter(step -> !step.isComplete())
                .forEach(step -> {
                    if (testResultsLoaderStepHandlers.containsKey(step.getName())) {
                        // Initialize the step status to "in progress"
                        step.setStatus(Status.IN_PROGRESS);
                        jobRepository.save(job);

                        testResultsLoaderStepHandlers.get(step.getName()).handle(job, step);

                        // Update job after each step has been processed to set step status/errors
                        jobRepository.save(job);

                        // If we failed validation, lets bail out and mark remaining steps as "failed"
                        if (step.getName().equals(TestPackageLoadJob.VALIDATE) && step.getStatus() == Status.FAIL) {
                            job.getSteps().stream()
                                    .filter(nonValidationSteps -> nonValidationSteps.getJobStepTarget() != TargetSystem.Internal)
                                    .forEach(nonValidationSteps -> nonValidationSteps.setStatus(Status.FAIL));

                            jobRepository.save(job);
                            throw new RuntimeException("Error: The test results transmission file failed validation. Aborting test results scoring.");
                        }

                    } else {
                        log.error("Attempting to call the step {} when it has not been registered to the loader step handler map.", step.getName());
                    }
                });
    }

    @Override
    public List<Job> findJobs(final String username) {
        return jobRepository.findByUserNameAndTypeIn(username, JobType.SCORING);
    }

    @Override
    public void updateJob(final String jobId, final JobUpdateRequest request) {
        final Job job = jobRepository.findOne(jobId);

        if (job == null) {
            throw new RuntimeException(String.format("Error: Could not locate the job with id %s. Unable to update the status of the requested job.", jobId));
        }

        final Step newStep = new Step(request.getName(), request.getTargetSystem(), request.getDescription(), request.getStatus());
        job.addStep(newStep);
        jobRepository.save(job);
    }

    @Override
    public Optional<Job> findJob(final String jobId) {
        return Optional.ofNullable(jobRepository.findOne(jobId));
    }

    @Override
    public Optional<TDSReport> findOriginalTrt(final String jobId) {
        final TestResultsWrapper wrapper = testResultsRepository.findByJobId(jobId);

        if (wrapper == null) {
            return Optional.empty();
        }

        return Optional.of(wrapper.getTestResults());
    }

    @Override
    public Optional<TDSReport> findRescoredTrt(final String jobId) {
        final TestResultsWrapper wrapper = testResultsRepository.findByJobId(jobId);

        if (wrapper == null) {
            return Optional.empty();
        }

        return Optional.of(wrapper.getRescoredTestResults());
    }

    @Override
    public Optional<ScoringValidationReport> findScoringValidationReport(final String jobId) {
        final TestResultsWrapper wrapper = testResultsRepository.findByJobId(jobId);

        if (wrapper == null) {
            return Optional.empty();
        }

        return Optional.of(wrapper.getScoringValidationReport());
    }

    @Override
    public void saveRescoredTrt(final String jobId, final String rescoredTrtString) {
        testResultsService.saveRescoredTestResults(jobId, rescoredTrtString);

        // Add a completed step to the job to show that re-scoring has been done
        final Job job = jobRepository.findOne(jobId);
        if (!(job instanceof TestResultsScoringJob)) {
            log.warn("Could not update job " + jobId + " with save validation step");
        } else {
            ((TestResultsScoringJob)job).addOrUpdateValidationReportStep();
            jobRepository.save(job);
        }
    }
}
