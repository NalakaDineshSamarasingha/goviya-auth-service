# Email Configuration Setup for Forgot Password Feature

## Gmail Setup (Free Email Service)

### Step 1: Enable 2-Factor Authentication
1. Go to your Google Account: https://myaccount.google.com/
2. Navigate to **Security**
3. Enable **2-Step Verification**

### Step 2: Generate App Password
1. After enabling 2FA, go to: https://myaccount.google.com/apppasswords
2. Select **Mail** as the app
3. Select **Other** as the device and name it "GOVIYA App"
4. Click **Generate**
5. Copy the 16-character password (remove spaces)

### Step 3: Configure Application Properties

Update your `application.properties` or set environment variables:

```properties
# For Gmail
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
```

Or set as environment variables:
```bash
set EMAIL_USERNAME=your-email@gmail.com
set EMAIL_PASSWORD=your-16-char-app-password
```

### Alternative Free Email Services

#### Outlook/Hotmail
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

#### Yahoo Mail
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password
```

## Testing the Email Service

Use the following endpoints in Postman:

### 1. Forgot Password (Send OTP)
**URL:** `POST http://localhost:8080/api/auth/forgot-password`

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
    "email": "john.doe@example.com",
    "purpose": "password-reset",
    "expiresIn": "5 minutes"
  }
}
```

### 2. Reset Password (Verify OTP and Reset)
**URL:** `POST http://localhost:8080/api/auth/reset-password`

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

## Complete Flow

1. **User requests password reset:**
   - POST `/api/auth/forgot-password` with email
   - System sends 6-digit OTP to user's email
   - OTP expires in 5 minutes

2. **User receives email:**
   - Beautiful HTML email with OTP code
   - Warning about security

3. **User submits OTP with new password:**
   - POST `/api/auth/reset-password` with email, OTP, and new password
   - System verifies OTP
   - Password is updated and encrypted

4. **User can now login:**
   - POST `/api/auth/login` with email and new password

## Security Features

- OTP expires in 5 minutes
- OTP is single-use (marked as used after verification)
- Previous unused OTPs are deleted when new one is requested
- Passwords are encrypted using BCrypt
- HTML email with security warnings

## Troubleshooting

### Email not sending
- Check if 2FA is enabled on Gmail
- Verify app password is correct (16 characters, no spaces)
- Check spam/junk folder
- Verify EMAIL_USERNAME and EMAIL_PASSWORD are set correctly
- Check application logs for errors

### "Invalid credentials" error
- Make sure you're using an App Password, not your regular Gmail password
- Verify the email and app password are correct

### OTP expired
- OTP is valid for only 5 minutes
- Request a new OTP if expired
