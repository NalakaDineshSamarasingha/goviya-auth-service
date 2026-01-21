package com.nalaka.goviya.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+94|0)?[7][0-8][0-9]{7}$", 
             message = "Invalid Sri Lankan mobile number format")
    private String phoneNumber;
    
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
    private String otp;
    
    private String purpose = "verification"; // Default to "verification"
}
