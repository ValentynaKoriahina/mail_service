package com.example.emailservice.publisher;

import com.example.emailservice.model.Email;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Email publisher, used to send emails via Message Broker.
 * It is used for processing messages in the queue received through the service REST API.
 */
@Component
public class EmailPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "emailExchange";
    private static final String ROUTING_KEY = "emailRoutingKey";

    public void publishEmail(Email email) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, email);
    }
}
