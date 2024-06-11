package com.example.emailservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

@Configuration
public class MailConfig {

    private final Dotenv dotenv;

    public MailConfig() {
        this.dotenv = Dotenv.load();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(dotenv.get("EMAIL_HOST"));
        mailSender.setPort(Integer.parseInt(Objects.requireNonNull(dotenv.get("EMAIL_PORT"))));

        mailSender.setUsername(dotenv.get("EMAIL_USERNAME"));
        mailSender.setPassword(dotenv.get("EMAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;
    }
}
