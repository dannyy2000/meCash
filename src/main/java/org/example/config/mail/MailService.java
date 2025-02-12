package org.example.config.mail;

import org.example.dto.EmailNotificationRequest;
import org.example.general.ApiResponse;

public interface MailService {
    ApiResponse<?> sendMail(EmailNotificationRequest emailNotificationRequest);
}
