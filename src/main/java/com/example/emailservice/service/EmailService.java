package com.example.emailservice.service;

import com.example.emailservice.dto.EmailDTO;
import com.example.emailservice.model.Email;
import com.example.emailservice.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailRepository emailRepository;

    public void processEmailSending(Email email) {
        System.out.println("public void processEmailSending(Email email)");
        try {
            sendEmail(email);
            email.setStatus("sent");
        } catch (Exception e) {
            email.setStatus("error");
            email.setErrorMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
            email.setAttemptNumber(email.getAttemptNumber() + 1); // Увеличение номера попытки
            email.setLastAttemptTime(LocalDateTime.now()); // Обновление времени последней попытки
        } finally {
            emailRepository.save(email); // Сохранение сообщения в Elasticsearch
        }
    }

    public void processEmailSending(EmailDTO emailDTO) {
        Email email = new Email();
        email.setRecipient(emailDTO.getRecipient());
        email.setSubject(emailDTO.getSubject());
        email.setContent(emailDTO.getContent());

        processEmailSending(email);
    }


    public void sendEmail(Email email) {

//        throw new IllegalArgumentException("Не указан адрес получателя");

        SimpleMailMessage message = new SimpleMailMessage(); // CHANGES!!
        message.setTo(email.getRecipient()); // CHANGES!!
        message.setSubject(email.getSubject()); // CHANGES!!
        message.setText(email.getContent()); // CHANGES!!

        mailSender.send(message);

        System.out.println("Отправлен имеил");

        System.out.println(message);
    }

    @Scheduled(fixedRate = 300000)
    public void retryFailedEmails() {
        List<Email> failedEmails = emailRepository.findByStatus("error");
        System.out.println(failedEmails);
        for (Email email : failedEmails) {
            processEmailSending(email);
        }
    }
}
