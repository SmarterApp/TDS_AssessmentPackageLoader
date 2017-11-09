package tds.support.tool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.tool.handlers.loader.TestPackageFileHandler;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.services.JobService;

@Service
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final TestPackageFileHandler testPackageFileHandler;

    @Autowired
    public JobServiceImpl(final JobRepository jobRepository, final TestPackageFileHandler testPackageFileHandler) {
        this.jobRepository = jobRepository;
        this.testPackageFileHandler = testPackageFileHandler;
    }

    @Override
    public Job startPackageImport(final String packageName, final InputStream testPackage, long testPackageSize) {
        Job job = new Job();
        job.setStatus(Status.IN_PROGRESS);
        job.setType(JobType.LOADER);

        Job persistedJob = jobRepository.save(job);

        Step step = testPackageFileHandler.handleTestPackage(persistedJob.getId(), packageName, testPackage, testPackageSize);

        job.addStep(step);

        return jobRepository.save(job);
    }
}
