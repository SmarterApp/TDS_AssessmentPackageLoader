package tds.support.tool.messaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tds.support.tool.services.JobService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JobStepExecutionMessageListenerTest {
    @Mock
    private JobService mockJobService;

    private JobStepExecutionMessageListener listener;

    @Before
    public void setUp() {
        this.listener = new JobStepExecutionMessageListener(mockJobService);
    }

    @Test
    public void shouldHandleMessage() {
        final String jobId = "JobA";
        listener.handleMessage(jobId);
        verify(mockJobService).executeJobSteps(jobId);
    }
}
