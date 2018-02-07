package tds.support.tool.services.impl;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import tds.support.job.Error;
import tds.support.job.ErrorSeverity;
import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageDeleteJob;
import tds.support.job.TestPackageLoadJob;
import tds.support.job.TestPackageRollbackJob;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.handlers.loader.TestPackageFileHandler;
import tds.support.tool.handlers.loader.TestPackageHandler;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.services.TestPackageStatusService;
import tds.support.tool.services.loader.MessagingService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceImplTest {

    @Mock
    private TestPackageFileHandler mockTestPackageFileHandler;

    @Mock
    private JobRepository mockJobRepository;

    @Mock
    private MessagingService mockMessagingService;

    @Mock
    private TestPackageHandler mockTestPackageHandler;

    @Mock
    private TestPackageStatusService mockTestPackageStatusService;

    private JobServiceImpl jobService;

    @Before
    public void setUp() {
        jobService = new JobServiceImpl(mockJobRepository,
            mockTestPackageFileHandler,
            mockMessagingService,
            mockTestPackageStatusService,
            ImmutableMap.of(TestPackageLoadJob.TDS_UPLOAD, mockTestPackageHandler));
    }

    @Test
    public void shouldStartImportAndTestPackageLoaderJob() {
        InputStream testPackageStream = mock(InputStream.class);
        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        final String packageName = "packageName";
        Job job = new TestPackageLoadJob(packageName, false, false);
        job.setId(UUID.randomUUID().toString());
        job.setType(JobType.LOADER);

        Step step = job.getSteps().stream()
            .filter(s -> TestPackageLoadJob.FILE_UPLOAD.equals(s.getName()))
            .findFirst().get();

        when(mockTestPackageFileHandler.handleTestPackage(isA(Step.class), isA(String.class), eq("packageName"), isA(InputStream.class), eq(100L))).thenReturn(step);

        step.setStatus(Status.IN_PROGRESS);
        when(mockJobRepository.save(isA(Job.class))).thenReturn(job);

        jobService.startPackageImport("packageName", testPackageStream, 100L, false, false);

        verify(mockJobRepository, times(2)).save(jobArgumentCaptor.capture());

        //Validate the Job called the first time with Save
        Job jobBeforeUpload = jobArgumentCaptor.getAllValues().get(1);
        assertThat(jobBeforeUpload.getSteps()).hasSize(6);
        assertThat(jobBeforeUpload.getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(jobBeforeUpload.getType()).isEqualTo(JobType.LOADER);

        Step fileUploadStep = jobBeforeUpload.getSteps().stream()
            .filter(s -> s.getName().equals(TestPackageLoadJob.FILE_UPLOAD))
            .findFirst().get();

        assertThat(fileUploadStep.getStatus()).isEqualTo(Status.IN_PROGRESS);

        //Validate the Job called the second time with save
        Job jobAfterUpload = jobArgumentCaptor.getAllValues().get(0);
        assertThat(jobAfterUpload.getSteps()).hasSize(6);

        Step tdsJobStep = jobAfterUpload.getSteps().stream()
            .filter(s -> s.getJobStepTarget() == TargetSystem.TDS)
            .findFirst().get();

        assertThat(tdsJobStep.getName()).contains("tds-upload");
        assertThat(tdsJobStep.getErrors()).isEmpty();
        assertThat(tdsJobStep.getStatus()).isEqualTo(Status.NOT_STARTED);
    }

    @Test
    public void shouldFindAllJobsForNullJobType() {
        Job mockLoaderJob = new TestPackageLoadJob("FileName", false, false);
        mockLoaderJob.setType(JobType.LOADER);
        Job mockScoringJob = new TestPackageDeleteJob("FileName", false, false);
        mockScoringJob.setType(JobType.SCORING);

        List<Job> mockJobs = Arrays.asList(mockLoaderJob, mockScoringJob);
        when(mockJobRepository.findAll()).thenReturn(mockJobs);

        List<Job> retJobs = jobService.findJobs(null);
        assertThat(retJobs).hasSize(2);
    }

    @Test
    public void shouldFindAllLoaderJobs() {
        Job mockLoaderJob = new TestPackageLoadJob("FileName", false, false);
        mockLoaderJob.setType(JobType.LOADER);

        when(mockJobRepository.findByTypeIn(JobType.LOADER)).thenReturn(Collections.singletonList(mockLoaderJob));

        List<Job> retJobs = jobService.findJobs(JobType.LOADER);
        assertThat(retJobs).hasSize(1);
    }

    @Test
    public void shouldExecuteAllStepsSuccessfullyLoaderJob() {
        final Job loaderJob = new TestPackageLoadJob("TestPackageName", false, false);
        loaderJob.setId("myId");

        final List<TestPackageTargetSystemStatus> mockTargetSystemStatusList =
            Collections.singletonList(new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS));

        final TestPackageStatus mockLoaderJobStatus = new TestPackageStatus(loaderJob.getName(),
            LocalDateTime.now(),
            mockTargetSystemStatusList);

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        when(mockJobRepository.findOne(loaderJob.getId())).thenReturn(loaderJob);
        when(mockTestPackageStatusService.save(loaderJob)).thenReturn(mockLoaderJobStatus);

        jobService.executeJobSteps(loaderJob.getId());

        verify(mockJobRepository).findOne(loaderJob.getId());
        verify(mockTestPackageHandler).handle(eq(loaderJob), isA(Step.class));
        verify(mockJobRepository, times(2)).save(jobArgumentCaptor.capture());
        // Only called once because there is only one handler configured in this test
        verify(mockTestPackageStatusService, times(1)).save(jobArgumentCaptor.capture());

        List<Job> savedJobs = jobArgumentCaptor.getAllValues();
        assertThat(savedJobs).hasSize(2);
    }


    @Test
    public void shouldCreateRollbackJobIfJobIsUnsuccessful() {
        final Job loaderJob = new TestPackageLoadJob("TestPackageName", false, false);
        loaderJob.setId("myId");
        Step failedStep = loaderJob.getSteps().get(0);
        failedStep.setStatus(Status.FAIL);
        failedStep.addError(new Error("An error", ErrorSeverity.CRITICAL));

        final List<TestPackageTargetSystemStatus> mockFailedSystemStatusList =
            Collections.singletonList(new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS));

        final TestPackageStatus mockFailedJobStatus = new TestPackageStatus(loaderJob.getName(),
            LocalDateTime.now(),
            mockFailedSystemStatusList);

        final Job rollbackJob = new TestPackageRollbackJob(loaderJob.getId(), "TestPackageName", false, false);
        rollbackJob.setId("rollbackId");
        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        when(mockJobRepository.findOne(loaderJob.getId())).thenReturn(loaderJob);
        when(mockJobRepository.save(isA(Job.class))).thenReturn(rollbackJob);
        when(mockTestPackageStatusService.save(loaderJob)).thenReturn(mockFailedJobStatus);

        jobService.executeJobSteps(loaderJob.getId());

        verify(mockJobRepository).findOne(loaderJob.getId());
        verify(mockTestPackageHandler).handle(eq(loaderJob), isA(Step.class));
        verify(mockMessagingService).sendJobStepExecute(isA(String.class));
        // 2 saves for the loader job (in progress/fail status) and 1 for the rollback job creation
        verify(mockJobRepository, times(3)).save(jobArgumentCaptor.capture());
        // Still called once to indicate the loader job's failure
        verify(mockTestPackageStatusService, times(1)).save(jobArgumentCaptor.capture());

        List<Job> savedJobs = jobArgumentCaptor.getAllValues();
        assertThat(savedJobs).hasSize(3);

        Job loaderJobPreStepHandlers = savedJobs.get(0);
        assertThat(loaderJobPreStepHandlers.getType()).isEqualTo(JobType.LOADER);
        // Second save is our new "rollback" job
        Job savedRollbackJob = savedJobs.get(1);
        assertThat(savedRollbackJob.getType()).isEqualTo(JobType.ROLLBACK);
        assertThat(savedRollbackJob.getStatus()).isEqualTo(Status.NOT_STARTED);
        assertThat(savedRollbackJob.getId()).isNotEqualTo(loaderJobPreStepHandlers.getId());
        // Third save is our loader jobs "fail" status save
        Job savedFailedLoaderJob = savedJobs.get(2);
        assertThat(savedFailedLoaderJob.getType()).isEqualTo(JobType.LOADER);
        assertThat(savedFailedLoaderJob.getStatus()).isEqualTo(Status.FAIL);
        assertThat(savedFailedLoaderJob.getId()).isEqualTo(loaderJobPreStepHandlers.getId());
    }

    @Test
    public void shouldDeleteTestPackageStatusRecordWhenJobIsARollbackJob() {
        final Job rollbackJob = new TestPackageRollbackJob("parentJobId",
            "TestPackageName",
            false,
            false);
        rollbackJob.setId("rollbackId");

        when(mockJobRepository.findOne(rollbackJob.getId())).thenReturn(rollbackJob);
        when(mockJobRepository.save(isA(Job.class))).thenReturn(rollbackJob);

        jobService.executeJobSteps(rollbackJob.getId());
        verify(mockTestPackageStatusService, times(0)).save(rollbackJob);
        verify(mockTestPackageStatusService, times(1)).delete(rollbackJob.getName());
    }

    @Test
    public void shouldDeleteTestPackageStatusRecordWhenJobIsADeleteJob() {
        final Job deleteJob = new TestPackageDeleteJob("TestPackageName",
            false,
            false);
        deleteJob.setId("deleteId");

        when(mockJobRepository.findOne(deleteJob.getId())).thenReturn(deleteJob);
        when(mockJobRepository.save(isA(Job.class))).thenReturn(deleteJob);

        jobService.executeJobSteps(deleteJob.getId());
        verify(mockTestPackageStatusService, times(0)).save(deleteJob);
        verify(mockTestPackageStatusService, times(1)).delete(deleteJob.getName());
    }

    @Test
    public void shouldCreateATestPackageDeleteJobForATestPackageThatWasPreviouslyLoaded() {
        final String testPackageName = "delete-me";
        final TestPackageLoadJob previouslyLoadedJob = new TestPackageLoadJob(testPackageName,
            false,
            false);
        previouslyLoadedJob.setId("previously-loaded-job-id");

        when(mockJobRepository.findOneByNameAndTypeOrderByCreatedAtDesc(testPackageName, JobType.LOADER))
            .thenReturn(previouslyLoadedJob);
        doAnswer(new JobWithIdAnswer(UUID.randomUUID().toString()))
            .when(mockJobRepository).save(isA(TestPackageDeleteJob.class));
        ArgumentCaptor<TestPackageDeleteJob> testPackageDeleteJobArgumentCaptor =
            ArgumentCaptor.forClass(TestPackageDeleteJob.class);

        jobService.startPackageDelete(testPackageName);

        verify(mockJobRepository).findOneByNameAndTypeOrderByCreatedAtDesc(testPackageName, JobType.LOADER);
        verify(mockJobRepository).save(testPackageDeleteJobArgumentCaptor.capture());

        final TestPackageDeleteJob createdTestPackageDeleteJob = testPackageDeleteJobArgumentCaptor.getValue();
        assertThat(createdTestPackageDeleteJob.getId()).isNotEqualTo(previouslyLoadedJob.getId());
        assertThat(createdTestPackageDeleteJob.isSkipArt()).isEqualTo(previouslyLoadedJob.isSkipArt());
        assertThat(createdTestPackageDeleteJob.isSkipScoring()).isEqualTo(previouslyLoadedJob.isSkipArt());
        assertThat(createdTestPackageDeleteJob.getName()).isEqualTo(previouslyLoadedJob.getName());
    }

    @Test
    public void shouldNotCreateTestPackageDeleteJobWhenThereIsNoPreviousLoaderJob() {
        final String testPackageName = "i-was-loaded";

        when(mockJobRepository.findOneByNameAndTypeOrderByCreatedAtDesc(testPackageName, JobType.LOADER))
            .thenReturn(null);

        jobService.startPackageDelete(testPackageName);

        verify(mockJobRepository).findOneByNameAndTypeOrderByCreatedAtDesc(testPackageName, JobType.LOADER);
        verifyNoMoreInteractions(mockJobRepository);
        verifyZeroInteractions(mockMessagingService);
    }

    /**
     * Set a {@link tds.support.job.Job}'s id field to some arbitrary value when simulating
     * {@link org.springframework.data.repository.CrudRepository#save(Object)} methods that should return a "new" job
     * with an identifier (that would've otherwise been generated by the database as part of the save process).
     */
    class JobWithIdAnswer implements Answer<Job> {
        private final String jobId;

        JobWithIdAnswer(final String jobId) {
            this.jobId = jobId;
        }

        @Override
        public Job answer(final InvocationOnMock invocation) throws Throwable {
            Assert.assertEquals(1, invocation.getArguments().length);
            final Job job = (Job) invocation.getArguments()[0];
            job.setId(jobId);
            return job;
        }
    }
}