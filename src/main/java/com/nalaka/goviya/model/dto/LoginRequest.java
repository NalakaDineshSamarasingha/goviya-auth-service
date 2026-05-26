package com.nalaka.goviya.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String password;

    @Pattern(regexp = "^(\\+94|0)?[7][0-8][0-9]{7}$", 
             message = "Invalid Sri Lankan mobile number format")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
    private String otp;
}
