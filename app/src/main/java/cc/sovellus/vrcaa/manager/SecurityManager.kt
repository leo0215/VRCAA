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

package cc.sovellus.vrcaa.manager

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import cc.sovellus.vrcaa.base.BaseManager
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecurityManager : BaseManager<Any>() {
    
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS_CREDENTIALS = "vrcaa_credentials_key"
    private const val KEY_ALIAS_TOKENS = "vrcaa_tokens_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16
    
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
            load(null)
        }
    }
    
    /**
     * Encrypts sensitive data using Android Keystore
     */
    fun encryptData(data: String, keyAlias: String): String? {
        return try {
            val secretKey = getOrCreateSecretKey(keyAlias)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val iv = cipher.iv
            val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            // Combine IV and encrypted data
            val combined = iv + encryptedData
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("SecurityManager", "Encryption failed", e)
            null
        }
    }
    
    /**
     * Decrypts sensitive data using Android Keystore
     */
    fun decryptData(encryptedData: String, keyAlias: String): String? {
        return try {
            val secretKey = getOrCreateSecretKey(keyAlias)
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)
            
            // Extract IV and encrypted data
            val iv = combined.sliceArray(0..GCM_IV_LENGTH - 1)
            val cipherText = combined.sliceArray(GCM_IV_LENGTH until combined.size)
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            val decryptedData = cipher.doFinal(cipherText)
            String(decryptedData, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e("SecurityManager", "Decryption failed", e)
            null
        }
    }
    
    /**
     * Encrypts user credentials (username/password)
     */
    fun encryptCredentials(username: String, password: String): String? {
        val credentials = "$username:$password"
        return encryptData(credentials, KEY_ALIAS_CREDENTIALS)
    }
    
    /**
     * Decrypts user credentials
     */
    fun decryptCredentials(encryptedCredentials: String): Pair<String, String>? {
        val decrypted = decryptData(encryptedCredentials, KEY_ALIAS_CREDENTIALS)
        return decrypted?.let {
            val parts = it.split(":", limit = 2)
            if (parts.size == 2) {
                Pair(parts[0], parts[1])
            } else {
                null
            }
        }
    }
    
    /**
     * Encrypts authentication tokens
     */
    fun encryptToken(token: String): String? {
        return encryptData(token, KEY_ALIAS_TOKENS)
    }
    
    /**
     * Decrypts authentication tokens
     */
    fun decryptToken(encryptedToken: String): String? {
        return decryptData(encryptedToken, KEY_ALIAS_TOKENS)
    }
    
    /**
     * Gets or creates a secret key for encryption
     */
    private fun getOrCreateSecretKey(keyAlias: String): SecretKey {
        return if (keyStore.containsAlias(keyAlias)) {
            keyStore.getKey(keyAlias, null) as SecretKey
        } else {
            createSecretKey(keyAlias)
        }
    }
    
    /**
     * Creates a new secret key in Android Keystore
     */
    private fun createSecretKey(keyAlias: String): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false) // Set to true if you want biometric/PIN protection
            .setRandomizedEncryptionRequired(true)
            .build()
        
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }
    
    /**
     * Checks if data appears to be encrypted (Base64 encoded)
     */
    fun isDataEncrypted(data: String): Boolean {
        return try {
            Base64.decode(data, Base64.DEFAULT)
            data.matches(Regex("^[A-Za-z0-9+/]*={0,2}$")) && data.length > 20
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Deletes encryption keys (use with caution - will make encrypted data unrecoverable)
     */
    fun deleteKeys() {
        try {
            if (keyStore.containsAlias(KEY_ALIAS_CREDENTIALS)) {
                keyStore.deleteEntry(KEY_ALIAS_CREDENTIALS)
            }
            if (keyStore.containsAlias(KEY_ALIAS_TOKENS)) {
                keyStore.deleteEntry(KEY_ALIAS_TOKENS)
            }
        } catch (e: Exception) {
            Log.e("SecurityManager", "Failed to delete keys", e)
        }
    }
}