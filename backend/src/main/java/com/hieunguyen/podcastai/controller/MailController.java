package com.hieunguyen.podcastai.controller;

import java.io.File;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hieunguyen.podcastai.service.EmailService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/v1/mail")
public class MailController {
    
    private final EmailService emailService;

    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-test")
    public String sendTest() {
        emailService.sendEmail("test@example.com", "Hello from Mailtrap", "Đây là email test qua Mailtrap");
        return "Email sent to Mailtrap!";
    }

    @GetMapping("/send-attachment")
    public String sendMailWithAttachment() throws MessagingException {
        File file = new File("C:/Users/ASUS/Documents/TEST.jpg"); // example file
        emailService.sendEmailWithAttachment(
                "recipient@example.com",
                "Here is your document",
                "Please find the attached file.",
                file
        );
        return "Mail sent with attachment!";
    }

    @GetMapping("/send-template")
    public String sendTemplateMail() throws MessagingException {
        emailService.sendEmailWithTemplate(
                "recipient@example.com",
                "Welcome to MyApp",
                "email-templates",
                Map.of("name", "Hieu", "verificationLink", "http://localhost:8080/verify?token=abc123")
        );
        return "Template email sent!";
    }
}
