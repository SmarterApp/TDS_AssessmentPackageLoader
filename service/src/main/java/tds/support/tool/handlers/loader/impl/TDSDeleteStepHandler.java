package tds.support.tool.handlers.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import tds.support.job.Error;
import tds.support.job.*;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.services.TDSTestPackageService;
import tds.testpackage.model.TestPackage;

@Component
public class TDSDeleteStepHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(TDSDeleteStepHandler.class);
    private final TDSTestPackageService tdsTestPackageService;
    private final MongoTestPackageRepository mongoTestPackageRepository;
    private final TestPackageMetadataRepository testPackageMetadataRepository;
    private final JobRepository jobRepository;

    @Autowired
    public TDSDeleteStepHandler(final TDSTestPackageService tdsTestPackageService,
                                final MongoTestPackageRepository mongoTestPackageRepository,
                                final TestPackageMetadataRepository testPackageMetadataRepository,
                                final JobRepository jobRepository) {
        this.tdsTestPackageService = tdsTestPackageService;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public void handle(final Job job, final Step step) {
        try {
            Job loaderJob = jobRepository.findOneByNameAndTypeOrderByCreatedAtDesc(job.getName(), JobType.LOAD);
            TestPackageMetadata metadata = testPackageMetadataRepository.findByJobId(loaderJob.getId());
            TestPackage testPackage = mongoTestPackageRepository.findOne(metadata.getTestPackageId());
            tdsTestPackageService.deleteTestPackage(testPackage);
            step.setStatus(Status.SUCCESS);
        } catch (HttpClientErrorException hcee) {
            if(404 == hcee.getRawStatusCode()) {
                // Accept a 404 on delete as OK - it's not there - mission accomplished! But log it.
                log.warn("Accepting a 404 as OK when deleting: step {}, job ID {}", step.getName(), job.getId());
                step.setStatus(Status.SUCCESS);
                step.addError(new Error(String.format(
                    "Warning: Received a 404 NOT FOUND when deleting package from %s. " +
                    "This is OK if the package is not actually on the system.", step.getJobStepTarget()),
                    ErrorSeverity.WARN));
            } else {
                Step.handleException(job, step, hcee);
            }
        } catch (Exception e) {
            Step.handleException(job, step, e);
        }

        step.setComplete(true);
    }
}
