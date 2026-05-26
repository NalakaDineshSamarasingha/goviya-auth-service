package com.nalaka.goviya.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailSignupRequest {
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    // Email/password are optional for mobile-based signup flows
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Role is required")
    private String role;
    
    // Optional fields
    private String phone;
    private String province;
    private String district;
    private String city;
    private String[] harvestTypes;
    private Double harvestArea;
}
