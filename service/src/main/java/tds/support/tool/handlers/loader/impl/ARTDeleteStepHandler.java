package tds.support.tool.handlers.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tds.common.ValidationError;
import tds.support.job.Error;
import tds.support.job.*;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.services.ARTTestPackageService;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

@Component
public class ARTDeleteStepHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(ARTDeleteStepHandler.class);
    private final ARTTestPackageService artTestPackageService;
    private final MongoTestPackageRepository mongoTestPackageRepository;
    private final TestPackageMetadataRepository testPackageMetadataRepository;

    public ARTDeleteStepHandler(final ARTTestPackageService artTestPackageService,
                                final MongoTestPackageRepository mongoTestPackageRepository, final TestPackageMetadataRepository testPackageMetadataRepository) {
        this.artTestPackageService = artTestPackageService;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
    }

    @Override
    public void handle(final Job job, final Step step) {
        try {
            TestPackageMetadata metadata = testPackageMetadataRepository.findByJobId(((TestPackageRollbackJob) job).getParentJobId());
            TestPackage testPackage = mongoTestPackageRepository.findOne(metadata.getTestPackageId());

            artTestPackageService.deleteTestPackage(testPackage);
            step.setStatus(Status.SUCCESS);
        } catch (Exception e) {
            log.error("An error occurred while attempting to process the job step {} for job with ID {}",
                    step.getName(), job.getId(), e);

            step.setStatus(Status.FAIL);
            step.addError(new Error(String.format("Error occurred while communicating with ART: %s", e.getMessage()),
                    ErrorSeverity.CRITICAL));
        }

        step.setComplete(true);
    }
}
