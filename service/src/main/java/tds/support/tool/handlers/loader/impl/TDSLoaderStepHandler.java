package tds.support.tool.handlers.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tds.common.ValidationError;
import tds.support.job.Error;
import tds.support.job.*;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.loader.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.services.TDSTestPackageService;
import tds.testpackage.model.TestPackage;

import java.util.Optional;


@Component
public class TDSLoaderStepHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(TDSLoaderStepHandler.class);

    private final TDSTestPackageService tdsTestPackageService;
    private final MongoTestPackageRepository mongoTestPackageRepository;
    private final TestPackageMetadataRepository testPackageMetadataRepository;

    @Autowired
    public TDSLoaderStepHandler(final TDSTestPackageService tdsTestPackageService,
                                final TestPackageMetadataRepository testPackageMetadataRepository,
                                final MongoTestPackageRepository mongoTestPackageRepository) {
        this.tdsTestPackageService = tdsTestPackageService;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
    }

    @Override
    public void handle(final Job job, final Step step) {
        try {
            TestPackageMetadata metadata = testPackageMetadataRepository.findByJobId(job.getId());
            TestPackage testPackage = mongoTestPackageRepository.findOne(metadata.getTestPackageId());
            Optional<ValidationError> maybeError = tdsTestPackageService.loadTestPackage(job.getName(), testPackage);

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
