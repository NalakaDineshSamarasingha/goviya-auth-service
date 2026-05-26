package com.nalaka.goviya.service;

import com.nalaka.goviya.model.Otp;
import com.nalaka.goviya.repository.OtpRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class OtpService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Autowired
    private OtpRepository otpRepository;
    
    @Autowired
    private EmailService emailService;

    private final SecureRandom random = new SecureRandom();

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio initialized successfully");
    }

    /**
     * Generate a 6-digit OTP
     */
    private String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Validate Sri Lankan phone number format
     */
    private boolean isValidSriLankanPhone(String phone) {
        return phone != null && phone.matches("^(\\+94|0)?[7][0-8][0-9]{7}$");
    }

    /**
     * Format phone number to international format (+94xxxxxxxxx)
     */
    private String formatSriLankanPhone(String phone) {
        if (phone.startsWith("+94")) {
            return phone;
        } else if (phone.startsWith("0")) {
            return "+94" + phone.substring(1);
        } else {
            return "+94" + phone;
        }
    }

    /**
     * Send OTP to the given phone number
     */
    @Transactional
    public Map<String, Object> sendOtp(String phoneNumber, String purpose) {
        Map<String, Object> response = new HashMap<>();

        // Validate phone number
        if (!isValidSriLankanPhone(phoneNumber)) {
            response.put("success", false);
            response.put("message", "Invalid Sri Lankan mobile number format. Use 0xxxxxxxxx or +94xxxxxxxxx");
            return response;
        }

        String formattedPhone = formatSriLankanPhone(phoneNumber);
        String otp = generateOtp();

        // Set expiry time (5 minutes from now)
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        try {
            // Delete any existing unused OTPs for this phone number and purpose
            otpRepository.deleteByPhoneNumberAndPurposeAndIsUsed(formattedPhone, purpose, false);

            // Create new OTP record
            Otp otpRecord = new Otp(formattedPhone, otp, purpose, expiryTime);
            otpRepository.save(otpRecord);

            // Send SMS via Twilio
            String message = String.format(
                    "Your GOVIYA verification code is: %s. This code will expire in 5 minutes. Do not share this code with anyone.",
                    otp
            );

            Message twilioMessage = Message.creator(
                    new PhoneNumber(formattedPhone),
                    new PhoneNumber(twilioPhoneNumber),
                    message
            ).create();

            log.info("OTP sent successfully to {}. SID: {} (DEV ONLY - OTP: {})", 
                     formattedPhone, twilioMessage.getSid(), otp);

            Map<String, Object> data = new HashMap<>();
            data.put("phoneNumber", formattedPhone);
            data.put("purpose", purpose);
            data.put("expiresIn", "5 minutes");

            response.put("success", true);
            response.put("message", "OTP sent successfully to your mobile number");
            response.put("data", data);

        } catch (Exception e) {
            log.error("Failed to send OTP to {}: {}", formattedPhone, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to send SMS. Please check your phone number and try again.");
        }

        return response;
    }

    /**
     * Verify OTP for the given phone number
     */
    @Transactional
    public Map<String, Object> verifyOtp(String phoneNumber, String otp, String purpose) {
        Map<String, Object> response = new HashMap<>();
        String formattedPhone = formatSriLankanPhone(phoneNumber);

        // Find valid OTP in database
        Optional<Otp> otpRecordOpt = otpRepository.findByPhoneNumberAndOtpAndPurposeAndIsUsedAndExpiresAtAfter(
                formattedPhone, otp, purpose, false, LocalDateTime.now()
        );

        if (otpRecordOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Invalid or expired OTP. Please request a new OTP.");
            return response;
        }

        // Mark OTP as used
        Otp otpRecord = otpRecordOpt.get();
        otpRecord.setUsed(true);
        otpRepository.save(otpRecord);

        log.info("OTP verified successfully for {}", formattedPhone);

        Map<String, Object> data = new HashMap<>();
        data.put("phoneNumber", formattedPhone);
        data.put("verified", true);
        data.put("purpose", purpose);

        response.put("success", true);
        response.put("message", "OTP verified successfully");
        response.put("data", data);

        return response;
    }

    /**
     * Re-open a phone OTP so it can be used again.
     */
    @Transactional
    public void markPhoneOtpUnused(String phoneNumber, String otp, String purpose) {
        String formattedPhone = formatSriLankanPhone(phoneNumber);

        Optional<Otp> otpRecordOpt = otpRepository.findByPhoneNumberAndOtpAndPurposeAndExpiresAtAfter(
                formattedPhone, otp, purpose, LocalDateTime.now()
        );

        otpRecordOpt.ifPresent(otpRecord -> {
            otpRecord.setUsed(false);
            otpRepository.save(otpRecord);
            log.info("OTP reset to unused for {}", formattedPhone);
        });
    }

    /**
     * Check if phone number is verified (has a verified OTP for signup)
     */
    public boolean isPhoneVerified(String phoneNumber, String purpose) {
        String formattedPhone = formatSriLankanPhone(phoneNumber);
        Optional<Otp> verifiedOtp = otpRepository.findByPhoneNumberAndOtpAndPurposeAndIsUsedAndExpiresAtAfter(
                formattedPhone, "", purpose, true, LocalDateTime.now().minusMinutes(10)
        );
        return verifiedOtp.isPresent();
    }
    
    /**
     * Send OTP to email for password reset
     */
    @Transactional
    public Map<String, Object> sendEmailOtp(String email, String userName, String purpose) {
        Map<String, Object> response = new HashMap<>();

        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        try {
            // Delete any existing unused OTPs for this email and purpose
            otpRepository.deleteByEmailAndPurposeAndIsUsed(email, purpose, false);

            // Create new OTP record
            Otp otpRecord = new Otp(email, otp, purpose, expiryTime, true);
            otpRepository.save(otpRecord);

            // Send email
            boolean emailSent = emailService.sendOtpEmail(email, otp, userName);

            if (!emailSent) {
                response.put("success", false);
                response.put("message", "Failed to send email. Please try again.");
                return response;
            }

            log.info("OTP sent successfully to email: {} (DEV ONLY - OTP: {})", email, otp);

            Map<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("purpose", purpose);
            data.put("expiresIn", "5 minutes");

            response.put("success", true);
            response.put("message", "OTP sent successfully to your email");
            response.put("data", data);

        } catch (Exception e) {
            log.error("Failed to send OTP to email {}: {}", email, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to send email. Please try again.");
        }

        return response;
    }

    /**
     * Verify email OTP
     */
    @Transactional
    public Map<String, Object> verifyEmailOtp(String email, String otp, String purpose) {
        Map<String, Object> response = new HashMap<>();

        // Find valid OTP in database
        Optional<Otp> otpRecordOpt = otpRepository.findByEmailAndOtpAndPurposeAndIsUsedAndExpiresAtAfter(
                email, otp, purpose, false, LocalDateTime.now()
        );

        if (otpRecordOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Invalid or expired OTP. Please request a new OTP.");
            return response;
        }

        // Mark OTP as used
        Otp otpRecord = otpRecordOpt.get();
        otpRecord.setUsed(true);
        otpRepository.save(otpRecord);

        log.info("Email OTP verified successfully for {}", email);

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("verified", true);
        data.put("purpose", purpose);

        response.put("success", true);
        response.put("message", "OTP verified successfully");
        response.put("data", data);

        return response;
    }
}
