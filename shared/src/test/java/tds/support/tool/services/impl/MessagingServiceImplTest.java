package tds.support.tool.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import tds.support.tool.services.loader.MessagingService;
import tds.support.tool.services.loader.impl.MessagingServiceImpl;

import static org.mockito.Mockito.verify;
import static tds.support.tool.configuration.JobMessagingConfiguration.TOPIC_EXCHANGE;
import static tds.support.tool.configuration.JobMessagingConfiguration.TOPIC_TEST_PACKAGE_LOAD;

@RunWith(MockitoJUnitRunner.class)
public class MessagingServiceImplTest {
    @Mock
    private RabbitTemplate mockRabbitTemplate;

    private MessagingService messageService;

    @Before
    public void setUp() {
        messageService = new MessagingServiceImpl(mockRabbitTemplate);
    }

    @Test
    public void shouldSendJobMessage() {
        final String jobId = "SomeId";
        messageService.sendJobStepExecute(jobId);
        verify(mockRabbitTemplate).convertAndSend(TOPIC_EXCHANGE, TOPIC_TEST_PACKAGE_LOAD, jobId);
    }
}
