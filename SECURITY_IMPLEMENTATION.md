# Security Implementation Guide

## Overview

This document describes the security improvements implemented to protect sensitive user data in the VRChat Android Assistant application.

## Critical Security Issues Fixed

### 🚨 Plain Text Password Storage
**Issue**: User credentials (username/password) were stored in SharedPreferences without encryption.
**Solution**: Implemented Android Keystore-based encryption for all sensitive data.

### 🚨 Unencrypted Authentication Tokens
**Issue**: Auth tokens, 2FA tokens, and Discord tokens were stored in plain text.
**Solution**: All tokens are now encrypted using AES-256-GCM encryption via Android Keystore.

## Implementation Details

### SecurityManager Class

The `SecurityManager` class provides secure encryption/decryption functionality:

- **Encryption Algorithm**: AES-256-GCM (Galois/Counter Mode)
- **Key Storage**: Android Keystore (hardware-backed when available)
- **Key Generation**: Randomized encryption with unique IVs per encryption
- **Authentication**: GCM provides built-in authentication

### Key Features

1. **Hardware Security**: Uses Android Keystore for key management
2. **Forward Compatibility**: Separate keys for credentials and tokens
3. **Migration Support**: Automatic migration from plain text to encrypted storage
4. **Fallback Handling**: Graceful degradation if encryption fails
5. **Data Integrity**: GCM mode provides authentication and prevents tampering

### Encrypted Data Storage

| Data Type | Legacy Key | New Encrypted Key | Key Alias |
|-----------|------------|-------------------|-----------|
| User Credentials | `userCredentials_username`, `userCredentials_password` | `userCredentials_encrypted` | `vrcaa_credentials_key` |
| Auth Token | `cookies` | `cookies_encrypted` | `vrcaa_tokens_key` |
| 2FA Token | `TwoFactorAuth` | `TwoFactorAuth_encrypted` | `vrcaa_tokens_key` |
| Discord Token | `discordToken` | `discordToken_encrypted` | `vrcaa_tokens_key` |

## Migration Process

### Automatic Migration

The app automatically migrates existing plain text data to encrypted format:

1. **Detection**: Check for encrypted data first, fallback to legacy format
2. **Migration**: Encrypt legacy data and store in new format
3. **Cleanup**: Remove legacy plain text keys after successful migration
4. **One-time**: Migration runs once per app installation

### Migration Flow

```kotlin
// Example migration flow for auth token
val encryptedToken = getString("cookies_encrypted", "")
if (encryptedToken.isNotEmpty()) {
    // Use encrypted data
    return SecurityManager.decryptToken(encryptedToken)
} else {
    // Migrate legacy data
    val legacyToken = getString("cookies", "")
    if (legacyToken.isNotEmpty()) {
        authToken = legacyToken // Triggers encryption and storage
        edit { remove("cookies") } // Clean up legacy data
    }
}
```

## Security Best Practices Implemented

### 1. Key Management
- Separate keys for different data types
- Hardware-backed key storage when available
- Keys cannot be extracted from the device

### 2. Encryption Standards
- AES-256-GCM for authenticated encryption
- Unique IV for each encryption operation
- 128-bit authentication tag

### 3. Data Protection
- No sensitive data in logs
- Secure key deletion capability
- Graceful error handling without data exposure

### 4. Migration Safety
- Non-destructive migration process
- Fallback to legacy format if decryption fails
- Comprehensive error handling

## Usage Examples

### Storing Credentials
```kotlin
// Automatically encrypted when set
preferences.userCredentials = Pair("username", "password")
```

### Retrieving Credentials
```kotlin
// Automatically decrypted when accessed
val (username, password) = preferences.userCredentials
```

### Storing Tokens
```kotlin
// All tokens are automatically encrypted
preferences.authToken = "auth_token_value"
preferences.twoFactorToken = "2fa_token_value"
preferences.discordToken = "discord_token_value"
```

## Testing

### Unit Tests
- Encryption/decryption round-trip tests
- Empty string handling
- Data format detection

### Integration Tests
- Migration testing with mock legacy data
- Error handling verification
- Performance testing

## Security Considerations

### Strengths
- Hardware-backed encryption when available
- Industry-standard encryption algorithms
- Automatic key rotation capability
- Tamper-evident storage

### Limitations
- Requires Android API 23+ for full security features
- Keys are tied to the app installation
- No cross-device synchronization of encrypted data

## Maintenance

### Key Rotation
```kotlin
// To rotate keys (will make existing data unrecoverable)
SecurityManager.deleteKeys()
// New keys will be generated on next encryption
```

### Troubleshooting
- Check Android Keystore availability
- Verify app has proper permissions
- Monitor logs for encryption/decryption errors

## Future Enhancements

1. **Biometric Authentication**: Require biometric unlock for sensitive operations
2. **Key Rotation**: Implement automatic key rotation policies
3. **Backup Encryption**: Encrypt app backups with user-provided keys
4. **Certificate Pinning**: Add certificate pinning for API communications

## Compliance

This implementation helps meet security requirements for:
- GDPR data protection requirements
- Mobile app security best practices
- Android security guidelines
- Industry-standard encryption practices