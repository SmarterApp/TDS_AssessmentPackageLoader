package tds.support.tool.messaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import tds.support.job.Job;
import tds.support.job.TestPackageLoadJob;
import tds.support.job.TestResultsScoringJob;
import tds.support.tool.repositories.JobRepository;
import tds.support.tool.services.TestPackageJobService;
import tds.support.tool.services.TestResultsJobService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobStepExecutionMessageListenerTest {
    @Mock
    private TestPackageJobService mockTestPackageJobService;

    @Mock
    private TestResultsJobService mockTestResultsJobService;

    @Mock
    private JobRepository mockJobRepository;

    private JobStepExecutionMessageListener listener;

    @Before
    public void setUp() {
        this.listener = new JobStepExecutionMessageListener(mockJobRepository, mockTestResultsJobService,
                mockTestPackageJobService);
    }

    @Test
    public void shouldHandleLoadJobMessage() {
        final String jobId = "JobA";
        final Job job = new TestPackageLoadJob(jobId, false, false);

        when(mockJobRepository.findOne(jobId)).thenReturn(job);
        listener.handleMessage(jobId);
        verify(mockTestPackageJobService).executeJobSteps(jobId);
        verify(mockJobRepository).findOne(jobId);
    }

    @Test
    public void shouldHandleScoringJobMessage() {
        final String jobId = "JobA";
        final String username = "user1";
        final Job job = new TestResultsScoringJob(jobId, username);

        when(mockJobRepository.findOne(jobId)).thenReturn(job);
        listener.handleMessage(jobId);
        verify(mockTestResultsJobService).executeJobSteps(jobId);
        verify(mockJobRepository).findOne(jobId);
    }
}
