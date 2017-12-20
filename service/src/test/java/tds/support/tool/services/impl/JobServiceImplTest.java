package tds.support.tool.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.job.Status;
import tds.support.job.Step;
import tds.support.tool.handlers.loader.TestPackageFileHandler;
import tds.support.tool.repositories.JobRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceImplTest {

    @Mock
    private TestPackageFileHandler mockTestPackageFileHandler;

    @Mock
    private JobRepository mockJobRepository;

    private JobServiceImpl jobService;

    @Before
    public void setUp() {
        jobService = new JobServiceImpl(mockJobRepository, mockTestPackageFileHandler, new HashMap<>());
    }

    @Test
    public void shouldStartPackageImport() {
        InputStream testPackageStream = mock(InputStream.class);
        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        final String packageName = "packageName";
        Job job = new Job();
        job.setId(UUID.randomUUID().toString());
        job.setStatus(Status.IN_PROGRESS);
        job.setType(JobType.LOADER);

        Step step = new Step(packageName,"description", Status.SUCCESS);

        when(mockJobRepository.save(isA(Job.class))).thenReturn(job);
        when(mockTestPackageFileHandler.handleTestPackage(isA(Step.class), isA(String.class),eq("packageName"), isA(InputStream.class), eq(100L))).thenReturn(step);

        jobService.startPackageImport("packageName", testPackageStream, 100L, false, false);

        verify(mockJobRepository, times(2)).save(jobArgumentCaptor.capture());

        //Validate the Job called the first time with Save
        Job jobBeforeUpload = jobArgumentCaptor.getAllValues().get(1);
        assertThat(jobBeforeUpload.getSteps()).isEmpty();
        assertThat(jobBeforeUpload.getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(jobBeforeUpload.getType()).isEqualTo(JobType.LOADER);

        //Validate the Job called the second time with save
        Job jobAfterUpload = jobArgumentCaptor.getAllValues().get(0);
        assertThat(jobAfterUpload.getSteps()).hasSize(6);
    }

    @Test
    public void shouldFindAllJobsForNullJobType() {
        Job mockLoaderJob = new Job();
        mockLoaderJob.setType(JobType.LOADER);
        Job mockScoringJob = new Job();
        mockScoringJob.setType(JobType.SCORING);

        List<Job> mockJobs = Arrays.asList(mockLoaderJob, mockScoringJob);
        when(mockJobRepository.findAll()).thenReturn(mockJobs);

        List<Job> retJobs = jobService.findJobs(null);
        assertThat(retJobs).hasSize(2);
    }

    @Test
    public void shouldFindAllLoaderJobs() {
        Job mockLoaderJob = new Job();
        mockLoaderJob.setType(JobType.LOADER);

        when(mockJobRepository.findByType(JobType.LOADER)).thenReturn(Collections.singletonList(mockLoaderJob));

        List<Job> retJobs = jobService.findJobs(JobType.LOADER);
        assertThat(retJobs).hasSize(1);
    }
}