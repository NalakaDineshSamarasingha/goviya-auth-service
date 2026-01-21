# GOVIYA API - Complete Postman Testing Guide

## Base URL
`http://localhost:8080`

---

## 📋 Table of Contents
1. [Database Testing](#1-test-database-connection)
2. [User Authentication](#user-authentication)
   - [Signup](#2-user-signup)
   - [Login](#3-user-login)
   - [Forgot Password](#4-forgot-password)
   - [Reset Password](#5-reset-password)
3. [OTP Services](#otp-services)
   - [Send Phone OTP](#6-send-phone-otp)
   - [Verify Phone OTP](#7-verify-phone-otp)
4. [Complete Workflows](#complete-workflows)

---

## 1. Test Database Connection
**Method:** `GET`  
**URL:** `http://localhost:8080/test-db`  
**Body:** None

**Expected Response:**
```
MongoDB connection SUCCESS. User count = 0
```

---

## User Authentication

### 2. User Signup
**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/signup`  
**Headers:** `Content-Type: application/json`

**Minimal Request (Required fields only):**
```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "Test@123",
  "role": "farmer"
}
```

**Full Request (With optional fields):**
```json
{
  "fullName": "John Farmer",
  "email": "farmer@example.com",
  "password": "Farmer123",
  "role": "farmer",
  "phone": "0771234567",
  "province": "Western",
  "district": "Colombo",
  "city": "Colombo",
  "harvestTypes": ["Rice", "Vegetables"],
  "harvestArea": 2.5
}
```

**Valid Roles:** `farmer`, `buyer`, `admin`

**Success Response:**
```json
{
  "success": true,
  "message": "Account created successfully",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "fullName": "John Doe",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "role": "farmer",
    "emailVerified": false,
    "phone": "0771234567",
    "province": "Western",
    "district": "Colombo",
    "city": "Colombo",
    "harvestTypes": ["Rice", "Vegetables"],
    "harvestArea": 2.5
  }
}
```

---

### 3. User Login
**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/login`  
**Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "Test@123"
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "0771234567",
    "province": "Western",
    "district": "Colombo",
    "city": "Colombo",
    "harvestTypes": ["Rice", "Vegetables"],
    "harvestArea": 2.5
  }
}
```

---

### 4. Forgot Password
**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/forgot-password`  
**Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "email": "john.doe@example.com"
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "OTP sent to your email successfully",
  "data": {
    "success": true,
    "message": "OTP sent successfully to your email",
    "data": {
      "email": "john.doe@example.com",
      "purpose": "password-reset",
      "expiresIn": "5 minutes"
    }
  }
}
```

**Note:** Check your email inbox for the 6-digit OTP code.

---

### 5. Reset Password
**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/reset-password`  
**Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "otp": "123456",
  "newPassword": "NewPassword123"
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": {
    "success": true,
    "message": "Password reset successfully"
  }
}
```

---

## OTP Services

### 6. Send Phone OTP
**Method:** `POST`  
**URL:** `http://localhost:8080/api/otp/send`  
**Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "phoneNumber": "0771234567",
  "purpose": "signup"
}
```

**Purpose Options:** `signup`, `login`, `verification`

**Success Response:**
```json
{
  "success": true,
  "message": "OTP sent successfully to your mobile number",
  "data": {
    "phoneNumber": "+94771234567",
    "purpose": "signup",
    "expiresIn": "5 minutes"
  }
}
```

---

### 7. Verify Phone OTP
**Method:** `POST`  
**URL:** `http://localhost:8080/api/otp/verify`  
**Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "phoneNumber": "0771234567",
  "otp": "123456",
  "purpose": "signup"
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "OTP verified successfully",
  "data": {
    "phoneNumber": "+94771234567",
    "verified": true,
    "purpose": "signup"
  }
}
```

---

## Complete Workflows

### Workflow 1: New User Signup → Login
1. **Signup:** POST `/api/auth/signup`
   ```json
   {
     "fullName": "Jane Smith",
     "email": "jane.smith@example.com",
     "password": "Jane@123",
     "role": "buyer"
   }
   ```

2. **Login:** POST `/api/auth/login`
   ```json
   {
     "email": "jane.smith@example.com",
     "password": "Jane@123"
   }
   ```

---

### Workflow 2: Forgot Password → Reset → Login
1. **Forgot Password:** POST `/api/auth/forgot-password`
   ```json
   {
     "email": "john.doe@example.com"
   }
   ```

2. **Check Email** for OTP (6-digit code)

3. **Reset Password:** POST `/api/auth/reset-password`
   ```json
   {
     "email": "john.doe@example.com",
     "otp": "123456",
     "newPassword": "NewPass@456"
   }
   ```

4. **Login with New Password:** POST `/api/auth/login`
   ```json
   {
     "email": "john.doe@example.com",
     "password": "NewPass@456"
   }
   ```

---

### Workflow 3: Full Farmer Signup with All Details
```json
{
  "fullName": "Saman Perera",
  "email": "saman.perera@gmail.com",
  "password": "Saman@789",
  "role": "farmer",
  "phone": "0771234567",
  "province": "Central",
  "district": "Kandy",
  "city": "Peradeniya",
  "harvestTypes": ["Tea", "Vegetables", "Fruits"],
  "harvestArea": 5.25
}
```

---

## Test Data Examples

### Different User Roles

**Farmer:**
```json
{
  "fullName": "Kumara Silva",
  "email": "kumara.farmer@test.com",
  "password": "Farmer@123",
  "role": "farmer",
  "harvestTypes": ["Rice", "Corn"],
  "harvestArea": 3.0
}
```

**Buyer:**
```json
{
  "fullName": "Nimal Fernando",
  "email": "nimal.buyer@test.com",
  "password": "Buyer@123",
  "role": "buyer",
  "city": "Colombo"
}
```

**Admin:**
```json
{
  "fullName": "Admin User",
  "email": "admin@goviya.com",
  "password": "Admin@123",
  "role": "admin"
}
```

---

## Error Responses

### Validation Errors
```json
{
  "success": false,
  "message": "Email is required"
}
```

### Authentication Errors
```json
{
  "success": false,
  "message": "Invalid email or password"
}
```

### OTP Errors
```json
{
  "success": false,
  "message": "Invalid or expired OTP. Please request a new OTP."
}
```

---

## Tips for Testing

1. **Test DB Connection first** to ensure MongoDB is running
2. **Use valid email addresses** for forgot password feature to receive OTP
3. **Check server logs** to see OTP codes during development (logged for testing)
4. **OTPs expire in 5 minutes** - request new one if expired
5. **Each OTP is single-use** - cannot reuse the same OTP
6. **Passwords are case-sensitive**
7. **Email must be unique** - cannot create multiple accounts with same email

---

## Environment Setup Required

### For Email (Forgot Password):
- Gmail account with 2FA enabled
- App password generated
- Environment variables set:
  ```
  EMAIL_USERNAME=your-email@gmail.com
  EMAIL_PASSWORD=your-16-char-app-password
  ```

### For SMS (Phone OTP):
- Twilio account
- Environment variables set:
  ```
  TWILIO_ACCOUNT_SID=your-account-sid
  TWILIO_AUTH_TOKEN=your-auth-token
  TWILIO_PHONE_NUMBER=+1234567890
  ```

See [EMAIL_SETUP.md](EMAIL_SETUP.md) for detailed email configuration.
