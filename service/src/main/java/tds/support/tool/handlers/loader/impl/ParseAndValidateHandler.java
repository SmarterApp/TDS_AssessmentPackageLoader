package tds.support.tool.handlers.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tds.support.job.ErrorSeverity;
import tds.support.job.Job;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.model.TestPackageMetadata;
import tds.support.tool.repositories.MongoTestPackageRepository;
import tds.support.tool.repositories.loader.TestPackageMetadataRepository;
import tds.support.tool.validation.TestPackageValidator;
import tds.support.tool.validation.ValidationError;
import tds.testpackage.model.TestPackage;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParseAndValidateHandler implements TestPackageHandler {
    private static final Logger log = LoggerFactory.getLogger(ParseAndValidateHandler.class);
    private final List<TestPackageValidator> validators;
    private final MongoTestPackageRepository mongoTestPackageRepository;
    private final TestPackageMetadataRepository testPackageMetadataRepository;

    @Autowired
    public ParseAndValidateHandler(final List<TestPackageValidator> validators,
                                 final MongoTestPackageRepository mongoTestPackageRepository,
                                 final TestPackageMetadataRepository testPackageMetadataRepository) {
        this.validators = validators;
        this.mongoTestPackageRepository = mongoTestPackageRepository;
        this.testPackageMetadataRepository = testPackageMetadataRepository;
    }

    @Override
    public void handle(final Job job, final Step step) {
        TestPackageMetadata metadata = testPackageMetadataRepository.findByJobId(job.getId());
        TestPackage testPackage = mongoTestPackageRepository.findOne(metadata.getTestPackageId());
        List<ValidationError> errors = new ArrayList<>();

        validators.forEach(validator -> validator.validate(testPackage, errors));

        if (errors.stream().anyMatch(error -> error.getSeverity() == ErrorSeverity.CRITICAL)) {
            log.error("Validation failed for the test package {}", metadata.getTestPackageId());
            step.setStatus(Status.FAIL);
        } else {
            step.setStatus(Status.SUCCESS);
        }

        errors.forEach(validationError ->
                step.addError(new tds.support.job.Error(validationError.getMessage(), validationError.getSeverity())));

        step.setComplete(true);
    }
}
