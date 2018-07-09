package tds.support.tool.handlers.scoring.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tds.common.ValidationError;
import tds.support.job.Error;
import tds.support.job.*;
import tds.support.tool.handlers.scoring.TestResultsHandler;
import tds.support.tool.model.TestResultsMetadata;
import tds.support.job.TestResultsWrapper;
import tds.support.tool.repositories.scoring.MongoTestResultsRepository;
import tds.support.tool.repositories.scoring.TestResultsMetadataRepository;
import tds.support.tool.services.ExamItemRescoreService;

import java.util.Optional;


@Component
public class ExamServiceRescoreStepHandler implements TestResultsHandler {
    private static final Logger log = LoggerFactory.getLogger(ExamServiceRescoreStepHandler.class);

    private final ExamItemRescoreService examItemRescoreService;
    private final MongoTestResultsRepository mongoTestResultsRepository;
    private final TestResultsMetadataRepository testResultsMetadataRepository;

    @Autowired
    public ExamServiceRescoreStepHandler(final ExamItemRescoreService examItemRescoreService,
                                         final TestResultsMetadataRepository testResultsMetadataRepository,
                                         final MongoTestResultsRepository mongoTestResultsRepository) {
        this.examItemRescoreService = examItemRescoreService;
        this.mongoTestResultsRepository = mongoTestResultsRepository;
        this.testResultsMetadataRepository = testResultsMetadataRepository;
    }

    @Override
    public void handle(final Job job, final Step step) {
        try {
            TestResultsMetadata metadata = testResultsMetadataRepository.findByJobId(job.getId());
            TestResultsWrapper testResultsWrapper = mongoTestResultsRepository.findOne(metadata.getTestResultsExamId());
            Optional<ValidationError> maybeError = examItemRescoreService.rescoreItems(((TestResultsScoringJob) job).getExamId(),
                    testResultsWrapper);

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
