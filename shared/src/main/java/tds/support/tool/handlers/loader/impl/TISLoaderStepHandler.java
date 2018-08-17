package tds.support.tool.handlers.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tds.common.ValidationError;
import tds.support.job.Error;
import tds.support.job.*;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.loader.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.services.TISTestPackageService;
import tds.testpackage.model.TestPackage;

import java.util.Optional;

@Component
public class TISLoaderStepHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(TISLoaderStepHandler.class);
    private final TISTestPackageService tisTestPackageService;
    private final MongoTestPackageRepository mongoTestPackageRepository;
    private final TestPackageMetadataRepository testPackageMetadataRepository;

    public TISLoaderStepHandler(final TISTestPackageService tisTestPackageService,
                                final MongoTestPackageRepository mongoTestPackageRepository,
                                final TestPackageMetadataRepository testPackageMetadataRepository) {
        this.tisTestPackageService = tisTestPackageService;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
    }

    @Override
    public void handle(final Job job, final Step step) {
        try {
            TestPackageMetadata metadata = testPackageMetadataRepository.findByJobId(job.getId());
            TestPackage testPackage = mongoTestPackageRepository.findOne(metadata.getTestPackageId());
            Optional<ValidationError> maybeError = tisTestPackageService.loadTestPackage(job.getName(), testPackage);

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
