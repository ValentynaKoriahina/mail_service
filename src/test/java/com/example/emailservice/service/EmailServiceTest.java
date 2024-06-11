package com.example.emailservice.service;

import com.example.emailservice.model.Email;
import com.example.emailservice.repository.EmailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private EmailRepository emailRepository;

    private Email email;

    @BeforeEach
    public void setUp() {
        email = new Email();
        email.setRecipient("test@example.com");
        email.setSubject("Test Subject");
        email.setContent("Test Content");
    }

    @Test
    public void testProcessEmailSending_Success() {
        // Given
        when(emailRepository.save(any(Email.class))).thenReturn(email);

        // When
        emailService.processEmailSending(email);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(1)).save(email);
        assert "sent".equals(email.getStatus());
    }

    @Test
    public void testProcessEmailSending_Failure() {
        // Given
        doThrow(new RuntimeException("Email sending failed")).when(mailSender).send(any(SimpleMailMessage.class));
        when(emailRepository.save(any(Email.class))).thenReturn(email);

        // When
        emailService.processEmailSending(email);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(1)).save(email);
        assert "error".equals(email.getStatus());
        assert email.getErrorMessage().equals("RuntimeException: Email sending failed");
    }

    @Test
    public void testRetryFailedEmails() {
        // Given
        email.setStatus("error");
        email.setAttemptNumber(0);
        when(emailRepository.findByStatus("error")).thenReturn(List.of(email));
        when(emailRepository.save(any(Email.class))).thenReturn(email);

        // When
        emailService.retryFailedEmails();

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(1)).save(email);
        assertEquals("sent", email.getStatus()); // Проверка, что статус изменился на "sent"
    }

    @Test
    public void testRetryFailedEmails_FailureAgain() {
        // Given
        email.setStatus("error");
        email.setAttemptNumber(0);
        email.setLastAttemptTime(LocalDateTime.now().minusDays(1)); // установим время попытки в прошлом
        LocalDateTime initialAttemptTime = email.getLastAttemptTime();
        when(emailRepository.findByStatus("error")).thenReturn(List.of(email));
        doThrow(new RuntimeException("Email sending failed again")).when(mailSender).send(any(SimpleMailMessage.class));
        when(emailRepository.save(any(Email.class))).thenReturn(email);

        // When
        emailService.retryFailedEmails();

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(1)).save(email);
        assertEquals("error", email.getStatus()); // Проверка, что статус остался "error"
        assertTrue(email.getLastAttemptTime().isAfter(initialAttemptTime)); // Проверка, что время последней попытки обновилось
        assertEquals(1, email.getAttemptNumber()); // Проверка, что номер попытки увеличился
    }
}
