package com.nalaka.goviya.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    // Email/password are optional for mobile-based signup flows using OTP
    @Email(message = "Invalid email format")
    private String email;

    private String password;
    
    @NotBlank(message = "Province is required")
    private String province;
    
    @NotBlank(message = "District is required")
    private String district;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+94|0)?[7][0-8][0-9]{7}$", 
             message = "Invalid Sri Lankan mobile number format")
    private String phone;
    
    private String optionalPhone;
    
    private String[] harvestTypes;
    
    private double harvestArea;
    
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
    private String otp;
}
