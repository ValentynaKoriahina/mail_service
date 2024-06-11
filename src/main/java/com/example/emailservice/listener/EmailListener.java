package com.example.emailservice.listener;

import com.example.emailservice.dto.EmailDTO;
import com.example.emailservice.model.Email;
import com.example.emailservice.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.Map;

/**
 * Component for listening to messages from Message Broker`s queues.
 */
@Component
public class EmailListener {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Method for processing messages of Email type.
     * @param email Email object Email received from the queue.
     */
    @RabbitListener(queues = "emailQueueInternal")
    public void receiveMessage(Email email) {
        try {
            emailService.processEmailSending(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Method for processing messages of Map type.
     * @param message Map-formatted message received from the queue.
     */
    @RabbitListener(queues = "emailQueue")
    public void receiveMessage(Map<String, Object> message) {
        try {
            EmailDTO emailDTO = objectMapper.convertValue(message, EmailDTO.class);

            emailService.processEmailSending(emailDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
