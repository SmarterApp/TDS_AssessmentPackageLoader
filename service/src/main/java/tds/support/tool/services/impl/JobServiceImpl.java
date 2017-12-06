package tds.support.tool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import tds.support.job.Error;
import tds.support.job.ErrorSeverity;
import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.job.System;
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

        for (Error error : step.getErrors()) {
            if (error.getSeverity().equals(ErrorSeverity.CRITICAL)) {
                job.setStatus(Status.FAIL);
                break;
            }
        }

        createTdsStep(job);
        createArtStep(job);
        createTisStep(job);
        createThssStep(job);

        return jobRepository.save(job);
    }

    //TODO: Placeholder - this code should call the TDS CREATE endpoint once it is complete to load the test package
    private Job createTdsStep(final Job job) {
        Step tdsLoadStep = new Step("Loading into TDS", Status.IN_PROGRESS, System.TDS);
        job.addStep(tdsLoadStep);
        return job;
    }

    //TODO: Placeholder - this code should call the ART CREATE endpoint once it is complete to load the test package
    private Job createArtStep(final Job job) {
        Step artLoadStep = new Step("Loading into ART", Status.IN_PROGRESS, System.ART);
        job.addStep(artLoadStep);
        return job;
    }

    //TODO: Placeholder - this code should call the TIS CREATE endpoint once it is complete to load the test package
    private Job createTisStep(final Job job) {
        Step tisLoadStep = new Step("Loading into TIS", Status.IN_PROGRESS, System.TIS);
        job.addStep(tisLoadStep);
        return job;
    }

    //TODO: Placeholder - this code should call the THSS CREATE endpoint once it is complete to load the test package
    private Job createThssStep(final Job job) {
        Step thssLoadStep = new Step("Loading into THSS", Status.IN_PROGRESS, System.THSS);
        job.addStep(thssLoadStep);
        return job;
    }

    @Override
    public List<Job> findJobs(final JobType jobType) {
        List<Job> allJobs =  jobRepository.findAll();
        return allJobs.stream()
            .filter(job -> job.getType() == jobType)
            .collect(Collectors.toList());
    }
}
