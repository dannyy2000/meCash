package org.example.config.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.example.data.model.enums.Status;
import org.example.dto.EmailNotificationRequest;
import org.example.general.ApiResponse;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.example.config.app.Message.EMAIL_FAILED;
import static org.example.config.app.Message.EMAIL_SENT;

@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService{

    private final MailConfiguration mailConfiguration;
    private final JavaMailSender javaMailSender;
    @Override
    public ApiResponse<?> sendMail(EmailNotificationRequest emailNotificationRequest) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);

        try{
            mimeMessageHelper.setFrom(mailConfiguration.getMailUsername());
            mimeMessageHelper.setTo(emailNotificationRequest.getRecipients());
            mimeMessageHelper.setSubject(emailNotificationRequest.getSubject());
            mimeMessageHelper.setText(emailNotificationRequest.getText());
            javaMailSender.send(message);
            Map<String,Object> responseData = buildResponseData(emailNotificationRequest);
            return buildSuccessResponse(responseData);

        } catch (MessagingException e) {
            return buildErrorResponse();
        }
    }

    private ApiResponse<?> buildErrorResponse() {
        return ApiResponse.builder()
                .status(Status.BAD_REQUEST)
                .message(EMAIL_FAILED)
                .build();

    }

    private ApiResponse<?> buildSuccessResponse(Map<String, Object> responseData) {
        return ApiResponse.builder()
                .status(Status.SUCCESS)
                .message(EMAIL_SENT)
                .data(responseData)
                .build();
    }

    private Map<String, Object> buildResponseData(EmailNotificationRequest emailNotificationRequest) {
        Map<String,Object> responseData = new HashMap<>();
        responseData.put("email",emailNotificationRequest.getRecipients());
        responseData.put("subject",emailNotificationRequest.getSubject());
        responseData.put("text",emailNotificationRequest.getText());
        return responseData;
    }

}

