package tds.support.tool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import tds.support.job.ErrorSeverity;
import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.job.TestPackageLoadJob;
import tds.support.tool.handlers.loader.TestPackageFileHandler;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.services.JobService;

@Service
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final TestPackageFileHandler testPackageFileHandler;
    private final Map<String, TestPackageHandler> testPackageLoaderStepHandlers;

    @Autowired
    public JobServiceImpl(final JobRepository jobRepository,
                          final TestPackageFileHandler testPackageFileHandler,
                          @Qualifier(value = "testPackageLoaderStepHandlers") final Map<String, TestPackageHandler> testPackageLoaderStepHandlers) {
        this.jobRepository = jobRepository;
        this.testPackageFileHandler = testPackageFileHandler;
        this.testPackageLoaderStepHandlers = testPackageLoaderStepHandlers;
    }

    @Override
    public Job startPackageImport(final String packageName, final InputStream testPackage, final long testPackageSize,
                                  final boolean skipArt, final boolean skipScoring) {
        Job job = new TestPackageLoadJob(packageName, skipArt, skipScoring);
        job.setStatus(Status.IN_PROGRESS);

        Step step = job.getSteps().stream().filter(potentialStep -> TestPackageLoadJob.FILE_UPLOAD.equals(potentialStep.getName())).findFirst().orElseThrow(() -> new IllegalStateException("First step in the loader job is not correctly configured"));
        step.setStatus(Status.IN_PROGRESS);

        Job persistedJob = jobRepository.save(job);

        Step updatedStep = testPackageFileHandler.handleTestPackage(step, persistedJob.getId(), packageName, testPackage, testPackageSize);

        if(updatedStep.getErrors().stream().anyMatch(error -> ErrorSeverity.CRITICAL.equals(error.getSeverity()))) {
            job.setStatus(Status.FAIL);
        }

        //Publish
        return jobRepository.save(persistedJob);
    }

    @Override
    public List<Job> findJobs(final JobType jobType) {
        if (jobType != null) {
            return jobRepository.findByType(jobType);
        }

        return jobRepository.findAll();
    }
}
