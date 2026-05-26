# GOVIYA Auth Service - Frontend Implementation Guide

This document describes how the frontend should integrate with the GOVIYA authentication API.

## Base URL

`http://localhost:8080`

## Content Type

All request bodies should use:

```http
Content-Type: application/json
```

## Authentication Overview

The service supports two login paths:

1. Email and password login
2. Mobile login using OTP verification

After a successful login, the API returns a JWT token. The frontend should store this token securely and send it with protected requests using the `Authorization` header.

```http
Authorization: Bearer <token>
```

## Data Contracts

### Standard API Response

Most endpoints return this shape:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {}
}
```

### Login Response

```json
{
  "token": "jwt-token",
  "id": "user-id",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "role": "farmer",
  "phone": "0771234567",
  "province": "Western",
  "district": "Colombo",
  "city": "Colombo",
  "harvestTypes": ["Rice", "Vegetables"],
  "harvestArea": 2.5
}
```

## 1. Signup With Email

### Endpoint

`POST /api/auth/signup`

### Purpose

Create a new account using email/password.

### Request Body

```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "Test@123",
  "role": "farmer",
  "phone": "0771234567",
  "province": "Western",
  "district": "Colombo",
  "city": "Colombo",
  "harvestTypes": ["Rice", "Vegetables"],
  "harvestArea": 2.5
}
```

### Required Fields

- `fullName`
- `email`
- `password`
- `role`

### Optional Fields

- `phone`
- `province`
- `district`
- `city`
- `harvestTypes`
- `harvestArea`

### Valid Roles

- `farmer`
- `buyer`
- `admin`

### Success Response

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
    "phone": "0771234567",
    "province": "Western",
    "district": "Colombo",
    "city": "Colombo",
    "harvestTypes": ["Rice", "Vegetables"],
    "harvestArea": 2.5
  }
}
```

## 2. Login

### Endpoint

`POST /api/auth/login`

### Purpose

Authenticate the user and return a JWT token.

The frontend can use either of these modes:

1. Email login with password
2. Phone login with OTP

### A. Email Login

#### Request Body

```json
{
  "email": "john.doe@example.com",
  "password": "Test@123"
}
```

#### Flow

1. User enters email and password.
2. Frontend sends the login request.
3. On success, store the returned token.

### B. Phone OTP Login

#### Step 1: Request OTP

Use the OTP send endpoint with purpose `login`.

##### Endpoint

`POST /api/otp/send`

##### Request Body

```json
{
  "phoneNumber": "0771234567",
  "purpose": "login"
}
```

##### Success Response

```json
{
  "success": true,
  "message": "OTP sent successfully to your mobile number",
  "data": {
    "phoneNumber": "+94771234567",
    "purpose": "login",
    "expiresIn": "5 minutes"
  }
}
```

#### Step 2: Verify OTP During Login

##### Request Body

```json
{
  "phoneNumber": "0771234567",
  "otp": "123456"
}
```

##### Login Success Response

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "jwt-token",
    "id": "user-id",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "role": "farmer",
    "phone": "0771234567",
    "province": "Western",
    "district": "Colombo",
    "city": "Colombo",
    "harvestTypes": ["Rice", "Vegetables"],
    "harvestArea": 2.5
  }
}
```

### Frontend Rules for Login

- If the user is logging in with email, send `email` and `password`.
- If the user is logging in with mobile number, first call `/api/otp/send` with purpose `login`.
- After OTP entry, send `phoneNumber` and `otp` to `/api/auth/login`.
- The frontend should not send both email/password and phone/OTP in the same request.

## 3. OTP Send

### Endpoint

`POST /api/otp/send`

### Purpose

Send a 6-digit OTP to a Sri Lankan mobile number.

### Request Body

```json
{
  "phoneNumber": "0771234567",
  "purpose": "signup"
}
```

### Purpose Values

- `signup` for registration verification
- `login` for mobile login
- `verification` for general verification use cases

### Success Response

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

## 4. OTP Verify

### Endpoint

`POST /api/otp/verify`

### Purpose

Verify an OTP without logging in.

### Request Body

```json
{
  "phoneNumber": "0771234567",
  "otp": "123456",
  "purpose": "signup"
}
```

### Success Response

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

## 5. Forgot Password

### Endpoint

`POST /api/auth/forgot-password`

### Request Body

```json
{
  "email": "john.doe@example.com"
}
```

### Flow

1. User enters email.
2. API sends OTP to the email.
3. Frontend redirects to reset password screen.

## 6. Reset Password

### Endpoint

`POST /api/auth/reset-password`

### Request Body

```json
{
  "email": "john.doe@example.com",
  "otp": "123456",
  "newPassword": "NewPassword123"
}
```

### Success Response

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

## 7. Suggested Frontend Flows

### New User Registration Flow

1. Collect signup details.
2. Send OTP to the mobile number with purpose `signup`.
3. User enters OTP.
4. Call `/api/auth/register-with-otp` with the full registration payload and OTP.

### Email Login Flow

1. Show email/password form.
2. Call `/api/auth/login` with `email` and `password`.
3. Store the JWT token after success.

### Mobile Login Flow

1. Show mobile number field.
2. Call `/api/otp/send` with purpose `login`.
3. Show OTP input.
4. Call `/api/auth/login` with `phoneNumber` and `otp`.
5. Store the JWT token after success.

## 8. Frontend Token Handling

After login success:

1. Save the JWT token in secure client storage.
2. Attach it to protected requests using the `Authorization` header.
3. Redirect the user to the dashboard or protected area.
4. If token validation fails, clear the token and redirect to login.

## 9. Validation Notes

- Sri Lankan mobile numbers should use `0771234567` or `+94771234567` format.
- OTP values must be 6 digits.
- OTPs expire after 5 minutes.
- Login by mobile number requires a previously registered account with that phone number.
- The login API accepts either email/password or phone/OTP, not both at once.

## 10. Recommended UI States

The frontend should handle these states:

- Loading while API requests are in progress
- Success message after OTP send
- OTP expired or invalid message
- Invalid credentials message
- User not found message for mobile login
- Session expired and logout flow

## 11. Example Request Headers for Protected APIs

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

## 12. Important Implementation Notes

- Do not require a password for mobile login.
- Do not fetch extra profile data before login; the login response already contains the user details needed for the UI.
- Use the returned `role` to route the user after authentication.
- Keep the token handling centralized in the frontend auth service or store.
