package com.jh.rabbit;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log4j2
@Service
public class ProducerService {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    Binding bindingTopicExchNonDurableQueue;
    @Autowired
    Binding bindingTopicExchDurableQueue;

    private int counter1 = 0;
    private int counter2 = 10000;
    private int counter3 = 20000;
    private int counter4 = 30000;
    private int counter5 = 40000;

    @Scheduled(fixedDelay = 1000)
    public void produceMessageToQueue() {

//        String message = String.format("[%s] Current time is %s", Thread.currentThread().getName(),
//                LocalDateTime.now());
        String message = String.format("[%s] (Topic) msg number %d (non durable)",
                Thread.currentThread().getName(), ++counter1);

        rabbitTemplate.convertAndSend(
                bindingTopicExchNonDurableQueue.getExchange(),
                bindingTopicExchNonDurableQueue.getRoutingKey(),
                message);
        log.info("Message sent: {}", message);
    }

    @Scheduled(fixedDelay = 1500)
    public void produceMessageToQueueDurable() {

//        String message = String.format("[%s] (Durable) Current time is %s", Thread.currentThread().getName(),
//                LocalDateTime.now());
        String message = String.format("[%s] (Topic) msg number %d (durable)",
                Thread.currentThread().getName(), ++counter2);

        rabbitTemplate.convertAndSend(
                bindingTopicExchDurableQueue.getExchange(),
                bindingTopicExchDurableQueue.getRoutingKey(),
                message);
        log.info("Message sent: {}", message);
    }

    @Scheduled(fixedDelay = 2000)
    public void produceMessageFanout() {

//        String message = String.format("[%s] (Fanout) Current time is %s", Thread.currentThread().getName(),
//                LocalDateTime.now());
        String message = String.format("[%s] (Fanout) msg number %d",
                Thread.currentThread().getName(), ++counter3);

        rabbitTemplate.convertAndSend(RabbitMqConfig.FANOUT_EXCHANGE_0, "", message);
        log.info("Message sent: {}", message);
    }

    @Scheduled(fixedDelay = 2500)
    public void produceMessageTopic() {

//        String message = String.format("[%s] (Topic) Current time is %s", Thread.currentThread().getName(),
//                LocalDateTime.now());
        String message = String.format("[%s] (Topic) user important msg number %d",
                Thread.currentThread().getName(), ++counter4);

        if (counter4 % 3 == 0) {
            message = message + " [ERR]";
            rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE_0,
                    RabbitMqConfig.ROUTING_KEY_USER_ERROR, message);
        } else {
            rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE_0,
                    RabbitMqConfig.ROUTING_KEY_USER_IMPORTANT, message);
        }
        log.info("Message sent: {}", message);
    }

    @Scheduled(fixedDelay = 3000)
    public void produceMessageTopicFileUploaded() {

        String message = String.format("[%s] (Topic) upload number %d", Thread.currentThread().getName(),
                ++counter5);

        if (counter5 % 3 == 0) {
            message = message + " (to planner)";
            rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE_FILE_SERVICE,
                        RabbitMqConfig.ROUTING_KEY_FILE_UPLOADED_PLANNER, message);
                log.info("Message sent: {} [{}]", message, RabbitMqConfig.ROUTING_KEY_FILE_UPLOADED_PLANNER);
        } else if (counter5 % 3 == 1) {
            message = message + " (to meeting)";
            rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE_FILE_SERVICE,
                    RabbitMqConfig.ROUTING_KEY_FILE_UPLOADED_MEETING, message);
            log.info("Message sent: {} [{}]", message, RabbitMqConfig.ROUTING_KEY_FILE_UPLOADED_MEETING);
        } else if (counter5 % 3 == 2) {
            message = message + " (to task)";
            rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE_FILE_SERVICE,
                    RabbitMqConfig.ROUTING_KEY_FILE_UPLOADED_TASK, message);
            log.info("Message sent: {} [{}]", message, RabbitMqConfig.ROUTING_KEY_FILE_UPLOADED_TASK);
        }
    }
}
