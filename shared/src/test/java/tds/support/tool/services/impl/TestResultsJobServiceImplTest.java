package tds.support.tool.services.impl;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import tds.support.job.*;
import tds.support.tool.handlers.scoring.TestResultsFileHandler;
import tds.support.tool.handlers.scoring.TestResultsHandler;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.repositories.scoring.MongoTestResultsRepository;
import tds.support.tool.services.loader.MessagingService;
import tds.support.tool.services.scoring.TestResultsService;
import tds.trt.model.TDSReport;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestResultsJobServiceImplTest {

    @Mock
    private TestResultsFileHandler mockTestResults;

    @Mock
    private JobRepository mockJobRepository;

    @Mock
    private MongoTestResultsRepository mockTestResultsRepository;

    @Mock
    private TestResultsService mockTestResultsService;

    @Mock
    private MessagingService mockMessagingService;

    @Mock
    private TestResultsHandler mockTestResultsHandler;

    private TestResultsJobServiceImpl jobService;

    @Captor
    private ArgumentCaptor<Job> jobArgumentCaptor;

    @Before
    public void setUp() {
        jobService = new TestResultsJobServiceImpl(mockJobRepository, mockTestResultsRepository,
                mockTestResultsService, mockTestResults, mockMessagingService,
                ImmutableMap.of(TestResultsScoringJob.RESCORE, mockTestResultsHandler));
    }

    @Test
    public void shouldStartImportAndTestPackageScoringJob() {
        InputStream testPackageStream = mock(InputStream.class);
        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        final String packageName = "packageName";
        final String username = "user1";
        Job job = new TestResultsScoringJob(packageName, username);
        job.setId(UUID.randomUUID().toString());
        job.setType(JobType.LOAD);

        Step step = job.getSteps().stream()
                .filter(s -> TestResultsScoringJob.FILE_UPLOAD.equals(s.getName()))
                .findFirst().get();

        when(mockTestResults.handleTestResults(isA(Step.class), isA(TestResultsScoringJob.class), eq("packageName"), isA(InputStream.class), eq(100L))).thenReturn(step);

        step.setStatus(Status.IN_PROGRESS);
        when(mockJobRepository.save(isA(Job.class))).thenReturn(job);

        jobService.startTestResultsImport("packageName", username, testPackageStream, 100L);

        verify(mockJobRepository, times(2)).save(jobArgumentCaptor.capture());

        //Validate the Job called the first time with Save
        Job jobBeforeUpload = jobArgumentCaptor.getAllValues().get(1);
        assertThat(jobBeforeUpload.getSteps()).hasSize(2);
        assertThat(jobBeforeUpload.getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(jobBeforeUpload.getType()).isEqualTo(JobType.LOAD);

        Step fileUploadStep = jobBeforeUpload.getSteps().stream()
                .filter(s -> s.getName().equals(TestResultsScoringJob.FILE_UPLOAD))
                .findFirst().get();

        assertThat(fileUploadStep.getStatus()).isEqualTo(Status.IN_PROGRESS);

        //Validate the Job called the second time with save
        Job jobAfterUpload = jobArgumentCaptor.getAllValues().get(0);
        assertThat(jobAfterUpload.getSteps()).hasSize(2);

        Step tdsJobStep = jobAfterUpload.getSteps().stream()
                .filter(s -> s.getJobStepTarget() == TargetSystem.Internal)
                .findFirst().get();

        assertThat(tdsJobStep.getName()).contains("test-results-file-upload");
        assertThat(tdsJobStep.getErrors()).isEmpty();
        assertThat(tdsJobStep.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    public void shouldFindAllScoringJobs() {
        final String username = "user1";
        Job mockLoaderJob = new TestResultsScoringJob("FileName", username);
        mockLoaderJob.setType(JobType.SCORING);
        when(mockJobRepository.findByUserNameAndTypeIn(username, JobType.SCORING)).thenReturn(Collections.singletonList(mockLoaderJob));
        List<Job> retJobs = jobService.findJobs(username);
        assertThat(retJobs).hasSize(1);
    }

    @Test
    public void shouldExecuteAllStepsSuccessfullyLoaderJob() {
        final String username = "user1";
        final Job loaderJob = new TestResultsScoringJob("TestPackageName", username);
        loaderJob.setId("myId");

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        when(mockJobRepository.findOne(loaderJob.getId())).thenReturn(loaderJob);

        jobService.executeJobSteps(loaderJob.getId());

        verify(mockJobRepository).findOne(loaderJob.getId());
        verify(mockTestResultsHandler).handle(eq(loaderJob), isA(Step.class));
        verify(mockJobRepository, times(2)).save(jobArgumentCaptor.capture());

        List<Job> savedJobs = jobArgumentCaptor.getAllValues();
        assertThat(savedJobs).hasSize(2);
    }

    @Test
    public void shouldUpdateJobStatus() {
        final String jobId = "jobId";
        final String username = "user1";
        final JobUpdateRequest request = new JobUpdateRequest("test", TargetSystem.ERT, Status.SUCCESS, "description");
        final Job mockJob = new TestResultsScoringJob("test", username);
        when(mockJobRepository.findOne(jobId)).thenReturn(mockJob);

        jobService.updateJob(jobId, request);

        verify(mockJobRepository).findOne(jobId);
        verify(mockJobRepository).save(jobArgumentCaptor.capture());

        Job job = jobArgumentCaptor.getValue();
        assertThat(job).isNotNull();
        assertThat(job.getSteps()).hasSize(3);
        assertThat(job.getSteps().get(2).getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(job.getSteps().get(2).getJobStepTarget()).isEqualTo(TargetSystem.ERT);
        assertThat(job.getSteps().get(2).getDescription()).isEqualTo("description");
        assertThat(job.getSteps().get(2).getName()).isEqualTo("test");
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowForNoJobFoundUpdateJobStatus() {
        final String jobId = "jobId";
        final JobUpdateRequest request = new JobUpdateRequest("test", TargetSystem.ERT, Status.SUCCESS, "description");
        when(mockJobRepository.findOne(jobId)).thenReturn(null);

        jobService.updateJob(jobId, request);
        verify(mockJobRepository).findOne(jobId);
    }

    @Test
    public void shouldFindSingleJob() {
        final String jobId = "jobId";
        final String username = "user1";
        final Job mockJob = new TestResultsScoringJob("something", username);
        when(mockJobRepository.findOne(jobId)).thenReturn(mockJob);

        Optional<Job> fetchedJob = jobService.findJob(jobId);
        assertThat(fetchedJob).isNotNull();
        assertThat(fetchedJob).isPresent();
        assertThat(fetchedJob.get().getName()).isEqualTo(mockJob.getName());
    }

    @Test
    public void shouldThrowForNoJobFound() {
        final String jobId = "jobId";
        when(mockJobRepository.findOne(jobId)).thenReturn(null);

        Optional<Job> fetchedJob = jobService.findJob(jobId);
        assertThat(fetchedJob).isNotPresent();
    }

    @Test
    public void shouldFindOriginalTrt() {
        final String jobId = "jobId";
        TDSReport report = new TDSReport();
        TestResultsWrapper wrapper = new TestResultsWrapper(jobId, report);
        when(mockTestResultsRepository.findByJobId(jobId)).thenReturn(wrapper);

        Optional<TDSReport> fetchedReport = jobService.findOriginalTrt(jobId);
        assertThat(fetchedReport).isNotNull();
        assertThat(fetchedReport).isPresent();
        assertThat(fetchedReport.get()).isEqualTo(report);
    }

    @Test
    public void shouldReturnEmptyOptionalForNoTrtPresent() {
        final String jobId = "jobId";
        when(mockTestResultsRepository.findByJobId(jobId)).thenReturn(null);

        Optional<TDSReport> fetchedReport = jobService.findOriginalTrt(jobId);
        assertThat(fetchedReport).isNotPresent();
    }

    @Test
    public void shouldFindRescoredTrt() {
        final String jobId = "jobId";
        TDSReport report = new TDSReport();
        TestResultsWrapper wrapper = new TestResultsWrapper(jobId, report);
        wrapper.setRescoredTestResults(report);
        when(mockTestResultsRepository.findByJobId(jobId)).thenReturn(wrapper);

        Optional<TDSReport> fetchedReport = jobService.findRescoredTrt(jobId);
        assertThat(fetchedReport).isNotNull();
        assertThat(fetchedReport).isPresent();
        assertThat(fetchedReport.get()).isEqualTo(report);
    }

    @Test
    public void shouldReturnEmptyOptionalForNoRescoredTrtPresent() {
        final String jobId = "jobId";
        when(mockTestResultsRepository.findByJobId(jobId)).thenReturn(null);

        Optional<TDSReport> fetchedReport = jobService.findRescoredTrt(jobId);
        assertThat(fetchedReport).isNotPresent();
    }

    @Test
    public void shouldFindScoringValidationReport() {
        final String jobId = "jobId";
        ScoringValidationReport report = new ScoringValidationReport();
        TestResultsWrapper wrapper = new TestResultsWrapper(jobId, new TDSReport());
        wrapper.setScoringValidationReport(report);
        when(mockTestResultsRepository.findByJobId(jobId)).thenReturn(wrapper);

        Optional<ScoringValidationReport> fetchedReport = jobService.findScoringValidationReport(jobId);
        assertThat(fetchedReport).isNotNull();
        assertThat(fetchedReport).isPresent();
        assertThat(fetchedReport.get()).isEqualTo(report);
    }

    @Test
    public void shouldReturnEmptyOptionalForNoScoringValidationReportPresent() {
        final String jobId = "jobId";
        when(mockTestResultsRepository.findByJobId(jobId)).thenReturn(null);

        Optional<ScoringValidationReport> fetchedReport = jobService.findScoringValidationReport(jobId);
        assertThat(fetchedReport).isNotPresent();
    }
}