package com.hieunguyen.podcastai.service;

import java.io.File;
import java.util.Map;

import jakarta.mail.MessagingException;

public interface EmailService  {

    void sendEmail(String to, String subject, String body);

    void sendEmailWithAttachment(String to, String subject, String body, File attachmentPath) throws MessagingException;

    void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateData) throws MessagingException;

    void sendEmailWithAttachmentAndTemplate(String to, String subject, String attachmentPath, String templateName, Map<String, Object> templateData);

}
