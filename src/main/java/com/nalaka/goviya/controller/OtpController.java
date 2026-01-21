package com.nalaka.goviya.controller;

import com.nalaka.goviya.model.dto.ApiResponse;
import com.nalaka.goviya.model.dto.SendOtpRequest;
import com.nalaka.goviya.model.dto.VerifyOtpRequest;
import com.nalaka.goviya.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin
@Validated
@Slf4j
public class OtpController {

    @Autowired
    private OtpService otpService;

    /**
     * Send OTP to phone number
     * POST /api/otp/send
     * Body: { "phoneNumber": "0771234567", "purpose": "signup" }
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        try {
            String purpose = request.getPurpose() != null ? request.getPurpose() : "verification";
            Map<String, Object> result = otpService.sendOtp(request.getPhoneNumber(), purpose);

            boolean success = (boolean) result.get("success");
            String message = (String) result.get("message");
            Object data = result.get("data");

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(success, message, (Map<String, Object>) data);

            return success 
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            log.error("Error sending OTP: {}", e.getMessage(), e);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                    false, 
                    "An error occurred while sending OTP. Please try again."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Verify OTP
     * POST /api/otp/verify
     * Body: { "phoneNumber": "0771234567", "otp": "123456", "purpose": "signup" }
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        try {
            String purpose = request.getPurpose() != null ? request.getPurpose() : "verification";
            Map<String, Object> result = otpService.verifyOtp(
                    request.getPhoneNumber(), 
                    request.getOtp(), 
                    purpose
            );

            boolean success = (boolean) result.get("success");
            String message = (String) result.get("message");
            Object data = result.get("data");

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(success, message, (Map<String, Object>) data);

            return success 
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            log.error("Error verifying OTP: {}", e.getMessage(), e);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                    false, 
                    "An error occurred while verifying OTP. Please try again."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
