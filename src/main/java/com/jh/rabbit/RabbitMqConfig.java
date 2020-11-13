package com.jh.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String FANOUT_QUEUE_1 = "fanout.queue1";
    public static final String FANOUT_QUEUE_2 = "fanout.queue2";
    public static final String FANOUT_EXCHANGE_0 = "fanout.exchange0";

    public static final String TOPIC_QUEUE_1 = "topic.Queue1";
    public static final String TOPIC_QUEUE_2 = "topic.Queue2-errors";
    public static final String TOPIC_EXCHANGE_0 = "topic.exchange0";

    public static final String ROUTING_KEY_USER_IMPORTANT = "user.important.info";
    public static final String ROUTING_KEY_USER_ERROR = "user.important.error";
    public static final String BINDING_PATTERN_IMPORTANT = "*.important.*";
    public static final String BINDING_PATTERN_ERROR = "#.error";

    public static final String TOPIC_EXCHANGE_FILE_SERVICE = "topic.services.file-service";
    public static final String ROUTING_KEY_FILE_UPLOADED_PLANNER = "file.uploaded.to-planner";
    public static final String ROUTING_KEY_FILE_UPLOADED_MEETING = "file.uploaded.to-meeting";
    public static final String ROUTING_KEY_FILE_UPLOADED_TASK = "file.uploaded.to-task";

    @Value("${rabbitmq.queue}")
    private String queueName;
    @Value("${rabbitmq.queue2}")
    private String queue2Name;

    @Value("${rabbitmq.exchange}")
    private String exchange;
    @Value("${rabbitmq.exchange2}")
    private String exchange2;

    @Value("${rabbitmq.routingkey}")
    private String routingKey;
    @Value("${rabbitmq.routingkey2}")
    private String routingKey2;

    @Bean
    Queue queue() {
        // Creating a queue
        return new Queue(queueName, Boolean.FALSE);
    }

    @Bean
    Queue queue2Durable() {
        // Creating a queue
        return new Queue(queue2Name, Boolean.TRUE);
    }

    @Bean
    TopicExchange topicExchange() {
        // Creating a topic exchange
        return new TopicExchange(exchange);
    }

    @Bean
    TopicExchange topicExchange2() {
        // Creating a topic exchange
        return new TopicExchange(exchange2);
    }

    @Bean
    Binding bindingTopicExchNonDurableQueue(final Queue queue, final TopicExchange topicExchange) {
        // Binding the queue to the topic with a routing key
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }

    @Bean
    Binding bindingTopicExchDurableQueue(final Queue queue2Durable, final TopicExchange topicExchange2) {
        // Binding the queue to the topic with a routing key
        return BindingBuilder.bind(queue2Durable).to(topicExchange2).with(routingKey2);
    }

    // FANOUT

    @Bean
    public Declarables fanoutBindings() {
        Queue fanoutQueue1 = new Queue(FANOUT_QUEUE_1, true);
        Queue fanoutQueue2 = new Queue(FANOUT_QUEUE_2, true);
        FanoutExchange fanoutExchange = new FanoutExchange(FANOUT_EXCHANGE_0);

        return new Declarables(
                fanoutQueue1,
                fanoutQueue2,
                fanoutExchange,
                BindingBuilder.bind(fanoutQueue1).to(fanoutExchange),
                BindingBuilder.bind(fanoutQueue2).to(fanoutExchange));
    }

    @Bean
    public Declarables topicBindings() {
        Queue topicQueue1 = new Queue(TOPIC_QUEUE_1, false);
        Queue topicQueue2 = new Queue(TOPIC_QUEUE_2, false);

        TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE_0);

        return new Declarables(
                topicQueue1,
                topicQueue2,
                topicExchange,
                BindingBuilder
                        .bind(topicQueue1)
                        .to(topicExchange).with(BINDING_PATTERN_IMPORTANT),
                BindingBuilder
                        .bind(topicQueue2)
                        .to(topicExchange).with(BINDING_PATTERN_ERROR));
    }
}
