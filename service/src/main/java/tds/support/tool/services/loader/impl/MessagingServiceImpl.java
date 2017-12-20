package tds.support.tool.services.loader.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tds.support.job.JobStepTarget;
import tds.support.job.JobType;
import tds.support.tool.configuration.JobMessagingConfiguration;
import tds.support.tool.services.loader.MessagingService;

import static tds.support.tool.configuration.JobMessagingConfiguration.*;

@Service
public class MessagingServiceImpl implements MessagingService {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessagingServiceImpl(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendJobStepExecute(final String jobId) {
        this.rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, TOPIC_TEST_PACKAGE_LOAD, jobId);
    }
}
