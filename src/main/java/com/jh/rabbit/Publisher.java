package com.jh.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class Publisher {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    Binding bindingTopicExchNonDurableQueue;

    @GetMapping(value = "/send/{msg}")
    @ResponseStatus(code = HttpStatus.OK)
    public String send(@PathVariable("msg") final String message) {

        rabbitTemplate.convertAndSend(
                bindingTopicExchNonDurableQueue.getExchange(),
                bindingTopicExchNonDurableQueue.getRoutingKey(),
                message);
        return "Message sent successfully to the queue";
    }
}
