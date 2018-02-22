package tds.support.tool.handlers.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tds.common.ValidationError;
import tds.support.job.Error;
import tds.support.job.ErrorSeverity;
import tds.support.job.Job;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.services.TDSTestPackageService;
import tds.support.tool.services.THSSService;
import tds.teacherhandscoring.model.TeacherHandScoringApiResult;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

@Component
public class THSSLoaderStepHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(THSSLoaderStepHandler.class);
    private final THSSService thssService;
    private final MongoTestPackageRepository mongoTestPackageRepository;
    private final TestPackageMetadataRepository testPackageMetadataRepository;

    @Autowired
    public THSSLoaderStepHandler(final THSSService thssService,
                                final TestPackageMetadataRepository testPackageMetadataRepository,
                                final MongoTestPackageRepository mongoTestPackageRepository) {
        this.thssService = thssService;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
    }

    @Override
    public void handle(final Job job, final Step step) {
        try {
            TestPackageMetadata metadata = testPackageMetadataRepository.findByJobId(job.getId());
            TestPackage testPackage = mongoTestPackageRepository.findOne(metadata.getTestPackageId());
            Optional<ValidationError> maybeError = thssService.loadTestPackage(job.getName(), testPackage);
            step.setStatus(Status.SUCCESS);
        } catch (Exception e) {
            log.error("An error occurred while attempting to process the job step {} for job with ID {}",
                step.getName(), job.getId(), e);

            // TODO: If errors were returned from the request, set those errors in the step here
            step.setStatus(Status.FAIL);
            step.addError(new Error(String.format("Error occurred while communicating with THSS: %s", e.getMessage()),
                    ErrorSeverity.CRITICAL));
        }

        step.setComplete(true);
    }
}
