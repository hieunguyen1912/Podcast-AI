package com.hieunguyen.podcastai.service.impl;

import java.io.File;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.hieunguyen.podcastai.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImple implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImple(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String body, File attachmentPath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        FileSystemResource fileResource = new FileSystemResource(attachmentPath);
        helper.addAttachment(attachmentPath.getName(), fileResource);
        mailSender.send(message);
    }

    @Override
    public void sendEmailWithTemplate(String to, String subject, String templateName,
            Map<String, Object> templateData) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);

        // Prepare the context for template variables
        Context context = new Context();
        context.setVariable("name", templateData.get("name"));
        context.setVariable("verificationLink", templateData.get("verificationLink"));

        // Process Thymeleaf template
        String htmlContent = templateEngine.process(templateName, context);

        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(message);
    }

    @Override
    public void sendEmailWithAttachmentAndTemplate(String to, String subject, String attachmentPath,
            String templateName, Map<String, Object> templateData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEmailWithAttachmentAndTemplate'");
    }
}
