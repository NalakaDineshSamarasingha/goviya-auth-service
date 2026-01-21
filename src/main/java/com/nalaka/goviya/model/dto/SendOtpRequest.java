package com.nalaka.goviya.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpRequest {
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+94|0)?[7][0-8][0-9]{7}$", 
             message = "Invalid Sri Lankan mobile number format. Use 0xxxxxxxxx or +94xxxxxxxxx")
    private String phoneNumber;
    
    private String purpose = "verification"; // Default to "verification"
}
