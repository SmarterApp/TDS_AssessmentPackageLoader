package tds.support.tool.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tds.support.tool.messaging.JobStepExecutionMessageListener;

@Configuration
public class JobMessagingConfiguration {
    public final static String QUEUE_JOB_EXECUTE = "job_execute_queue";
    public final static String TOPIC_EXCHANGE = "tds.support.tool.exchange";
    public final static String TOPIC_TEST_PACKAGE_LOAD = "test.package.load";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public Queue jobStepQueue() {
        return new Queue(QUEUE_JOB_EXECUTE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public Binding examReportedBinding(@Qualifier("jobStepQueue") final Queue queue,
                                       @Qualifier("exchange") final TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(TOPIC_TEST_PACKAGE_LOAD);
    }

    @Bean
    public SimpleMessageListenerContainer examReportedListenerContainer(final ConnectionFactory connectionFactory,
                                                                        final JobStepExecutionMessageListener listener) {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_JOB_EXECUTE);
        container.setMessageListener(new MessageListenerAdapter(listener, "handleMessage"));
        return container;
    }
}
