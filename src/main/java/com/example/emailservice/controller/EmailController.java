package com.example.emailservice.controller;

import com.example.emailservice.model.Email;
import com.example.emailservice.publisher.EmailPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API for sending notifications by e-mail.
 */
@RestController
@RequestMapping("/api/emails")
public class EmailController {

    /**
     * Email publisher, used to send emails via Message Broker.
     */
    @Autowired
    private EmailPublisher emailPublisher;

    @PostMapping
    public void sendEmail(@RequestBody Email email) {
        emailPublisher.publishEmail(email);
    }
}
