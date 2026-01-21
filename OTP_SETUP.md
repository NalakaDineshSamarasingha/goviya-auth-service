# OTP-Based User Registration

This backend now includes a complete OTP (One-Time Password) verification system for user registration using Twilio SMS.

## Features

- ✅ Send OTP to Sri Lankan mobile numbers
- ✅ Verify OTP before user registration
- ✅ Auto-expire OTPs after 5 minutes
- ✅ Prevent OTP reuse
- ✅ Support for multiple purposes (signup, login, verification)

## Setup Instructions

### 1. Configure Twilio Credentials

Set these environment variables or update [application.properties](src/main/resources/application.properties):

```properties
TWILIO_ACCOUNT_SID=your-twilio-account-sid
TWILIO_AUTH_TOKEN=your-twilio-auth-token
TWILIO_PHONE_NUMBER=+1234567890
```

### 2. Install Dependencies

Run Maven to install dependencies (including Twilio SDK):

```bash
./mvnw clean install
```

## API Endpoints

### 1. Send OTP

**Endpoint:** `POST /api/otp/send`

**Request Body:**
```json
{
  "phoneNumber": "0771234567",
  "purpose": "signup"
}
```

**Response (Success):**
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

### 2. Verify OTP

**Endpoint:** `POST /api/otp/verify`

**Request Body:**
```json
{
  "phoneNumber": "0771234567",
  "otp": "123456",
  "purpose": "signup"
}
```

**Response (Success):**
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

### 3. Register User with OTP

**Endpoint:** `POST /api/auth/register-with-otp`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "province": "Western",
  "district": "Colombo",
  "city": "Colombo",
  "phone": "0771234567",
  "optionalPhone": "0112345678",
  "harvestTypes": ["Rice", "Vegetables"],
  "harvestArea": 2.5,
  "otp": "123456"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "65f8a9b1c2d3e4f5g6h7i8j9",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phone": "0771234567",
    "province": "Western",
    "district": "Colombo",
    "city": "Colombo",
    "harvestTypes": ["Rice", "Vegetables"],
    "harvestArea": 2.5
  }
}
```

## User Registration Flow

1. **User initiates registration** → Frontend collects phone number
2. **Send OTP** → `POST /api/otp/send` with `purpose: "signup"`
3. **User receives SMS** → 6-digit OTP sent via Twilio
4. **User enters OTP** → Frontend collects OTP and other details
5. **Register with OTP** → `POST /api/auth/register-with-otp` with all details + OTP
6. **Backend verifies OTP** → Checks validity, expiry, and usage
7. **User created** → Account registered successfully

## Phone Number Format

Supports Sri Lankan mobile numbers in these formats:
- `0771234567` (local format)
- `+94771234567` (international format)

All numbers are automatically converted to international format (+94) for storage.

## OTP Properties

- **Length:** 6 digits
- **Expiry:** 5 minutes
- **Purpose:** signup, login, or verification
- **Reuse:** Cannot reuse same OTP (marked as used after verification)

## Security Notes

1. OTPs are single-use only
2. OTPs expire after 5 minutes
3. Old unused OTPs are deleted when new ones are generated
4. Phone numbers are validated for Sri Lankan format
5. OTPs are logged in development mode only (remove in production)

## Database Collections

### `otps` Collection
```javascript
{
  "_id": ObjectId,
  "phoneNumber": "+94771234567",
  "otp": "123456",
  "purpose": "signup",
  "expiresAt": ISODate,
  "createdAt": ISODate,
  "isUsed": false
}
```

### `users` Collection
```javascript
{
  "_id": ObjectId,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "hashedPassword",
  "phone": "0771234567",
  "province": "Western",
  "district": "Colombo",
  "city": "Colombo",
  "harvestTypes": ["Rice", "Vegetables"],
  "harvestArea": 2.5,
  "emailVerified": false
}
```

## Testing with Postman

### Step 1: Send OTP
```bash
POST http://localhost:8080/api/otp/send
Content-Type: application/json

{
  "phoneNumber": "0771234567",
  "purpose": "signup"
}
```

### Step 2: Register User
```bash
POST http://localhost:8080/api/auth/register-with-otp
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123",
  "province": "Western",
  "district": "Colombo",
  "city": "Colombo",
  "phone": "0771234567",
  "harvestTypes": ["Rice"],
  "harvestArea": 2.5,
  "otp": "123456"
}
```

## Error Handling

### Common Errors

**Invalid Phone Number:**
```json
{
  "success": false,
  "message": "Invalid Sri Lankan mobile number format. Use 0xxxxxxxxx or +94xxxxxxxxx"
}
```

**Invalid/Expired OTP:**
```json
{
  "success": false,
  "message": "Invalid or expired OTP. Please request a new OTP."
}
```

**User Already Exists:**
```json
{
  "success": false,
  "message": "User already exists with this email"
}
```

## Next Steps

1. ✅ Add password encryption (BCrypt)
2. ✅ Add JWT authentication
3. ✅ Add rate limiting for OTP requests
4. ✅ Add email verification
5. ✅ Add login with OTP option

## Files Created/Modified

### New Files:
- `model/Otp.java` - OTP entity
- `model/dto/SendOtpRequest.java` - Send OTP request DTO
- `model/dto/VerifyOtpRequest.java` - Verify OTP request DTO
- `model/dto/RegisterUserRequest.java` - Registration request DTO
- `model/dto/ApiResponse.java` - Standard API response wrapper
- `repository/OtpRepository.java` - OTP repository
- `service/OtpService.java` - OTP service layer
- `controller/OtpController.java` - OTP endpoints

### Modified Files:
- `pom.xml` - Added Twilio SDK dependency
- `application.properties` - Added Twilio configuration
- `service/UserService.java` - Added OTP-verified registration
- `controller/UserController.java` - Added OTP registration endpoint
