package tds.support.tool.handlers.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import tds.common.ValidationError;
import tds.support.job.Error;
import tds.support.job.ErrorSeverity;
import tds.support.job.Job;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.loader.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.services.ARTTestPackageService;
import tds.support.tool.services.ProgmanClientService;
import tds.testpackage.model.TestPackage;

@Component
public class ARTLoaderStepHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(ARTLoaderStepHandler.class);

    private final ARTTestPackageService artTestPackageService;
    private final MongoTestPackageRepository mongoTestPackageRepository;
    private final TestPackageMetadataRepository testPackageMetadataRepository;
    private final ProgmanClientService progmanClientService;

    @Autowired
    public ARTLoaderStepHandler(final ARTTestPackageService artTestPackageService,
                                final MongoTestPackageRepository mongoTestPackageRepository,
                                final TestPackageMetadataRepository testPackageMetadataRepository,
                                final ProgmanClientService progmanClientService) {
        this.artTestPackageService = artTestPackageService;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
        this.progmanClientService = progmanClientService;
    }

    @Override
    public void handle(final Job job, final Step step) {
        try {
            TestPackageMetadata metadata = testPackageMetadataRepository.findByJobId(job.getId());
            TestPackage testPackage = mongoTestPackageRepository.findOne(metadata.getTestPackageId());

            Optional<ValidationError> maybeError = artTestPackageService.loadTestPackage(progmanClientService.getTenantId(), testPackage);

            if (maybeError.isPresent()) {
                if (!maybeError.get().getCode().equalsIgnoreCase(ErrorSeverity.WARN.name())) {
                    step.setStatus(Status.FAIL);
                    step.addError(new Error(maybeError.get().getMessage(), ErrorSeverity.CRITICAL));
                } else {
                    step.setStatus(Status.SUCCESS);
                    step.addError(new Error(maybeError.get().getMessage(), ErrorSeverity.WARN));
                }
            } else {
                step.setStatus(Status.SUCCESS);
            }
        } catch (Exception e) {
            Step.handleException(job, step, e);
        }

        step.setComplete(true);
    }
}
