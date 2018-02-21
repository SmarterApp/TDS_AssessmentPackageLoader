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
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.services.TDSTestPackageService;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

@Component
public class TDSDeleteStepHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(TDSDeleteStepHandler.class);
    private final TDSTestPackageService tdsTestPackageService;
    private final MongoTestPackageRepository mongoTestPackageRepository;

    @Autowired
    public TDSDeleteStepHandler(final TDSTestPackageService tdsTestPackageService,
                                final MongoTestPackageRepository mongoTestPackageRepository) {
        this.tdsTestPackageService = tdsTestPackageService;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
    }

    @Override
    public void handle(final Job job, final Step step) {
        try {
            TestPackage testPackage = mongoTestPackageRepository.findOne(job.getName());
            Optional<ValidationError> maybeError = tdsTestPackageService.deleteTestPackage(testPackage);
            if (maybeError.isPresent()) {
                step.setStatus(Status.FAIL);
                step.addError(new Error(maybeError.get().getMessage(), ErrorSeverity.CRITICAL));
            }
            step.setStatus(Status.SUCCESS);
        } catch (Exception e) {
            log.error("An error occurred while attempting to process the job step {} for job with ID {}",
                step.getName(), job.getId());

            step.setStatus(Status.FAIL);
            step.addError(new Error("Error occurred while communicating with TDS", ErrorSeverity.CRITICAL));
        }

        step.setComplete(true);
    }
}
