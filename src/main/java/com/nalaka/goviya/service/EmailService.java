package com.nalaka.goviya.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Slf4j
public class EmailService {

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.password}")
    private String senderPassword;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private String mailPort;

    /**
     * Send OTP email for password reset
     */
    public boolean sendOtpEmail(String recipientEmail, String otp, String recipientName) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", mailHost);
            props.put("mail.smtp.port", mailPort);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "GOVIYA"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset OTP - GOVIYA");

            String emailBody = buildOtpEmailBody(otp, recipientName);
            message.setContent(emailBody, "text/html; charset=utf-8");

            Transport.send(message);
            log.info("OTP email sent successfully to: {}", recipientEmail);
            return true;

        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", recipientEmail, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Build HTML email body for OTP
     */
    private String buildOtpEmailBody(String otp, String name) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f9f9f9; padding: 30px; border-radius: 5px; margin-top: 20px; }
                    .otp-box { background-color: #fff; border: 2px dashed #4CAF50; padding: 20px; text-align: center; margin: 20px 0; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #4CAF50; letter-spacing: 5px; }
                    .warning { color: #d32f2f; font-size: 14px; margin-top: 20px; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>GOVIYA Password Reset</h1>
                    </div>
                    <div class="content">
                        <p>Hello %s,</p>
                        <p>You have requested to reset your password. Please use the OTP code below to proceed:</p>
                        <div class="otp-box">
                            <div class="otp-code">%s</div>
                        </div>
                        <p><strong>This OTP will expire in 5 minutes.</strong></p>
                        <div class="warning">
                            <p>⚠️ If you did not request this password reset, please ignore this email or contact support if you have concerns.</p>
                            <p>Never share this OTP with anyone. GOVIYA staff will never ask for your OTP.</p>
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2026 GOVIYA. All rights reserved.</p>
                        <p>This is an automated message, please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """, name, otp);
    }
}
