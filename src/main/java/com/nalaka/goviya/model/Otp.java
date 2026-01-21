package com.nalaka.goviya.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Otp {
    @Id
    private String id;
    private String phoneNumber;
    private String email;
    private String otp;
    private String purpose; // "signup", "login", "verification", "password-reset"
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private boolean isUsed;

    // Constructor for phone OTP
    public Otp(String phoneNumber, String otp, String purpose, LocalDateTime expiresAt) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
        this.isUsed = false;
    }
    
    // Constructor for email OTP
    public Otp(String email, String otp, String purpose, LocalDateTime expiresAt, boolean isEmail) {
        this.email = email;
        this.otp = otp;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
        this.isUsed = false;
    }
}
