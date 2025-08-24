/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa

import cc.sovellus.vrcaa.manager.SecurityManager
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SecurityManager
 * Note: These tests will only work on a real Android device or emulator with Android Keystore
 */
class SecurityManagerTest {

    @Test
    fun testCredentialsEncryptionDecryption() {
        val username = "testuser"
        val password = "testpassword123"
        
        // Test encryption
        val encrypted = SecurityManager.encryptCredentials(username, password)
        assertNotNull("Encryption should not return null", encrypted)
        assertNotEquals("Encrypted data should not equal original", "$username:$password", encrypted)
        
        // Test decryption
        val decrypted = SecurityManager.decryptCredentials(encrypted!!)
        assertNotNull("Decryption should not return null", decrypted)
        assertEquals("Username should match", username, decrypted!!.first)
        assertEquals("Password should match", password, decrypted.second)
    }
    
    @Test
    fun testTokenEncryptionDecryption() {
        val token = "auth_token_12345abcdef"
        
        // Test encryption
        val encrypted = SecurityManager.encryptToken(token)
        assertNotNull("Encryption should not return null", encrypted)
        assertNotEquals("Encrypted data should not equal original", token, encrypted)
        
        // Test decryption
        val decrypted = SecurityManager.decryptToken(encrypted!!)
        assertNotNull("Decryption should not return null", decrypted)
        assertEquals("Token should match", token, decrypted)
    }
    
    @Test
    fun testIsDataEncrypted() {
        val plainText = "plaintext"
        val base64Text = "dGVzdGRhdGE="
        
        assertFalse("Plain text should not be detected as encrypted", SecurityManager.isDataEncrypted(plainText))
        assertTrue("Base64 text should be detected as potentially encrypted", SecurityManager.isDataEncrypted(base64Text))
    }
    
    @Test
    fun testEmptyStringHandling() {
        val emptyEncrypted = SecurityManager.encryptToken("")
        // Empty strings might be handled differently, so we just ensure no crash
        assertNotNull("Empty string encryption should not crash", emptyEncrypted)
    }
}