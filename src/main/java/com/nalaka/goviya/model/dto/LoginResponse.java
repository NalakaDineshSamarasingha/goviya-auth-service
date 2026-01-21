package com.nalaka.goviya.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String province;
    private String district;
    private String city;
    private String[] harvestTypes;
    private double harvestArea;
}
