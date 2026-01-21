package com.nalaka.goviya.repository;

import com.nalaka.goviya.model.Otp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends MongoRepository<Otp, String> {
    
    List<Otp> findByPhoneNumberAndPurposeAndIsUsed(String phoneNumber, String purpose, boolean isUsed);
    
    Optional<Otp> findByPhoneNumberAndOtpAndPurposeAndIsUsedAndExpiresAtAfter(
            String phoneNumber, String otp, String purpose, boolean isUsed, LocalDateTime currentTime);
    
    void deleteByPhoneNumberAndPurposeAndIsUsed(String phoneNumber, String purpose, boolean isUsed);
    
    // Email OTP methods
    List<Otp> findByEmailAndPurposeAndIsUsed(String email, String purpose, boolean isUsed);
    
    Optional<Otp> findByEmailAndOtpAndPurposeAndIsUsedAndExpiresAtAfter(
            String email, String otp, String purpose, boolean isUsed, LocalDateTime currentTime);
    
    void deleteByEmailAndPurposeAndIsUsed(String email, String purpose, boolean isUsed);
}
