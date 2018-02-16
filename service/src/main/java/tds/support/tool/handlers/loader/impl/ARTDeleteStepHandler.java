package tds.support.tool.handlers.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tds.support.job.Error;
import tds.support.job.*;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.services.ARTTestPackageService;
import tds.testpackage.model.TestPackage;

@Component
public class ARTDeleteStepHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(ARTDeleteStepHandler.class);
    private final ARTTestPackageService artTestPackageService;
    private final MongoTestPackageRepository mongoTestPackageRepository;

    public ARTDeleteStepHandler(final ARTTestPackageService artTestPackageService,
                                final MongoTestPackageRepository mongoTestPackageRepository) {
        this.artTestPackageService = artTestPackageService;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
    }

    @Override
    public void handle(final Job job, final Step step) {
        try {
            TestPackage testPackage = mongoTestPackageRepository.findOne(job.getName());
            artTestPackageService.deleteTestPackage(testPackage);
            step.setStatus(Status.SUCCESS);
        } catch (Exception e) {
            log.error("An error occurred while attempting to process the job step {} for job with ID {}",
                    step.getName(), job.getId());

            step.setStatus(Status.FAIL);
            step.addError(new Error("Error occurred while communicating with ART", ErrorSeverity.CRITICAL));
        }

        step.setComplete(true);
    }
}
