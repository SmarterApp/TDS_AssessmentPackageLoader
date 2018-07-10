package tds.support.tool.handlers.scoring.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.HttpClientErrorException;
import tds.common.ValidationError;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.job.TestResultsScoringJob;
import tds.support.tool.handlers.scoring.TestResultsHandler;
import tds.support.tool.model.TestResultsMetadata;
import tds.support.job.TestResultsWrapper;
import tds.support.tool.repositories.scoring.MongoTestResultsRepository;
import tds.support.tool.repositories.scoring.TestResultsMetadataRepository;
import tds.support.tool.services.ExamItemRescoreService;
import tds.trt.model.TDSReport;

import java.util.Optional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamServiceRescoreStepHandlerTest {
    private TestResultsHandler handler;
    private TestResultsWrapper mockTestResults;

    @Mock
    private ExamItemRescoreService mockService;

    @Mock
    private MongoTestResultsRepository mockTestResultsRepository;

    @Mock
    private TestResultsMetadataRepository mockTestResultsMetadataRepository;

    @Before
    public void setup() {
        handler = new ExamServiceRescoreStepHandler(mockService, mockTestResultsMetadataRepository, mockTestResultsRepository);
        mockTestResults = new TestResultsWrapper("jobId", new TDSReport());
    }

    @Test
    public void shouldHandleStepSuccessfully() {
        TestResultsScoringJob mockJob = random(TestResultsScoringJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestResultsMetadata mockMetadata = random(TestResultsMetadata.class);

        when(mockTestResultsMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestResultsRepository.findOne(mockMetadata.getTestResultsExamId())).thenReturn(mockTestResults);
        when(mockService.rescoreItems(mockJob.getExamId(), mockTestResults)).thenReturn(Optional.empty());

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.SUCCESS);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore);

        verify(mockTestResultsMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestResultsRepository).findOne(mockMetadata.getTestResultsExamId());
        verify(mockService).rescoreItems(mockJob.getExamId(), mockTestResults);
    }


    @Test
    public void shouldHandleStepSuccessfullyWithWarns() {
        TestResultsScoringJob mockJob = random(TestResultsScoringJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestResultsMetadata mockMetadata = random(TestResultsMetadata.class);

        when(mockTestResultsMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestResultsRepository.findOne(mockMetadata.getTestResultsExamId())).thenReturn(mockTestResults);
        when(mockService.rescoreItems(mockJob.getExamId(), mockTestResults))
                .thenReturn(Optional.of(new ValidationError("WARN", "Just a warning...")));

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.SUCCESS);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isGreaterThan(errorsBefore);

        verify(mockTestResultsMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestResultsRepository).findOne(mockMetadata.getTestResultsExamId());
        verify(mockService).rescoreItems(mockJob.getExamId(), mockTestResults);
    }

    @Test
    public void shouldReturnErrorFromServiceCallAndFailStep() {
        TestResultsScoringJob mockJob = random(TestResultsScoringJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestResultsMetadata mockMetadata = random(TestResultsMetadata.class);

        when(mockTestResultsMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestResultsRepository.findOne(mockMetadata.getTestResultsExamId())).thenReturn(mockTestResults);
        when(mockService.rescoreItems(mockJob.getExamId(), mockTestResults))
                .thenReturn(Optional.of(new ValidationError("Some", "Error")));

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.FAIL);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore + 1);

        verify(mockTestResultsMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestResultsRepository).findOne(mockMetadata.getTestResultsExamId());
        verify(mockService).rescoreItems(mockJob.getExamId(), mockTestResults);
    }

    @Test
    public void shouldCatchExceptionAndFailStep() {
        TestResultsScoringJob mockJob = random(TestResultsScoringJob.class);
        Step mockStep = random(Step.class);
        int errorsBefore = mockStep.getErrors().size();
        TestResultsMetadata mockMetadata = random(TestResultsMetadata.class);

        when(mockTestResultsMetadataRepository.findByJobId(mockJob.getId())).thenReturn(mockMetadata);
        when(mockTestResultsRepository.findOne(mockMetadata.getTestResultsExamId())).thenReturn(mockTestResults);
        when(mockService.rescoreItems(mockJob.getExamId(), mockTestResults))
                .thenThrow(HttpClientErrorException.class);

        handler.handle(mockJob, mockStep);
        assertThat(mockStep.isComplete()).isTrue();
        assertThat(mockStep.getStatus()).isEqualTo(Status.FAIL);
        int errorsAfter = mockStep.getErrors().size();
        assertThat(errorsAfter).isEqualTo(errorsBefore + 1);

        verify(mockTestResultsMetadataRepository).findByJobId(mockJob.getId());
        verify(mockTestResultsRepository).findOne(mockMetadata.getTestResultsExamId());
        verify(mockService).rescoreItems(mockJob.getExamId(), mockTestResults);
    }
}

