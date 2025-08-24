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

package cc.sovellus.vrcaa.extension

import android.content.SharedPreferences
import androidx.core.content.edit
import cc.sovellus.vrcaa.helper.NotificationHelper
import cc.sovellus.vrcaa.manager.SecurityManager
import com.google.gson.Gson

// extend SharedPreferences
internal var SharedPreferences.userCredentials: Pair<String, String>
    get() {
        val encryptedCredentials = getString("userCredentials_encrypted", "")
        return if (encryptedCredentials?.isNotEmpty() == true) {
            // Try to decrypt new format
            SecurityManager.decryptCredentials(encryptedCredentials) ?: run {
                // Fallback to legacy format for migration
                val username = getString("userCredentials_username", "") ?: ""
                val password = getString("userCredentials_password", "") ?: ""
                if (username.isNotEmpty() || password.isNotEmpty()) {
                    // Migrate legacy data
                    val credentials = Pair(username, password)
                    userCredentials = credentials // This will encrypt and store
                    // Clear legacy data
                    edit {
                        remove("userCredentials_username")
                        remove("userCredentials_password")
                    }
                    credentials
                } else {
                    Pair("", "")
                }
            }
        } else {
            // Fallback to legacy format for migration
            val username = getString("userCredentials_username", "") ?: ""
            val password = getString("userCredentials_password", "") ?: ""
            if (username.isNotEmpty() || password.isNotEmpty()) {
                // Migrate legacy data
                val credentials = Pair(username, password)
                userCredentials = credentials // This will encrypt and store
                // Clear legacy data
                edit {
                    remove("userCredentials_username")
                    remove("userCredentials_password")
                }
                credentials
            } else {
                Pair("", "")
            }
        }
    }
    set(it) = edit {
        val encryptedCredentials = SecurityManager.encryptCredentials(it.first, it.second)
        if (encryptedCredentials != null) {
            putString("userCredentials_encrypted", encryptedCredentials)
            // Remove legacy keys if they exist
            remove("userCredentials_username")
            remove("userCredentials_password")
        } else {
            // Fallback to plain text if encryption fails (should not happen in normal cases)
            putString("userCredentials_username", it.first)
            putString("userCredentials_password", it.second)
        }
    }

internal var SharedPreferences.authToken: String
    get() {
        val encryptedToken = getString("cookies_encrypted", "")
        return if (encryptedToken?.isNotEmpty() == true) {
            // Try to decrypt new format
            SecurityManager.decryptToken(encryptedToken) ?: run {
                // Fallback to legacy format for migration
                val legacyToken = getString("cookies", "") ?: ""
                if (legacyToken.isNotEmpty()) {
                    // Migrate legacy data
                    authToken = legacyToken // This will encrypt and store
                    // Clear legacy data
                    edit { remove("cookies") }
                    legacyToken
                } else {
                    ""
                }
            }
        } else {
            // Fallback to legacy format for migration
            val legacyToken = getString("cookies", "") ?: ""
            if (legacyToken.isNotEmpty()) {
                // Migrate legacy data
                authToken = legacyToken // This will encrypt and store
                // Clear legacy data
                edit { remove("cookies") }
                legacyToken
            } else {
                ""
            }
        }
    }
    set(it) = edit {
        if (it.isNotEmpty()) {
            val encryptedToken = SecurityManager.encryptToken(it)
            if (encryptedToken != null) {
                putString("cookies_encrypted", encryptedToken)
                // Remove legacy key if it exists
                remove("cookies")
            } else {
                // Fallback to plain text if encryption fails
                putString("cookies", it)
            }
        } else {
            // Clear both encrypted and legacy keys when setting empty token
            remove("cookies_encrypted")
            remove("cookies")
        }
    }

internal var SharedPreferences.twoFactorToken: String
    get() {
        val encryptedToken = getString("TwoFactorAuth_encrypted", "")
        return if (encryptedToken?.isNotEmpty() == true) {
            // Try to decrypt new format
            SecurityManager.decryptToken(encryptedToken) ?: run {
                // Fallback to legacy format for migration
                val legacyToken = getString("TwoFactorAuth", "") ?: ""
                if (legacyToken.isNotEmpty()) {
                    // Migrate legacy data
                    twoFactorToken = legacyToken // This will encrypt and store
                    // Clear legacy data
                    edit { remove("TwoFactorAuth") }
                    legacyToken
                } else {
                    ""
                }
            }
        } else {
            // Fallback to legacy format for migration
            val legacyToken = getString("TwoFactorAuth", "") ?: ""
            if (legacyToken.isNotEmpty()) {
                // Migrate legacy data
                twoFactorToken = legacyToken // This will encrypt and store
                // Clear legacy data
                edit { remove("TwoFactorAuth") }
                legacyToken
            } else {
                ""
            }
        }
    }
    set(it) = edit {
        if (it.isNotEmpty()) {
            val encryptedToken = SecurityManager.encryptToken(it)
            if (encryptedToken != null) {
                putString("TwoFactorAuth_encrypted", encryptedToken)
                // Remove legacy key if it exists
                remove("TwoFactorAuth")
            } else {
                // Fallback to plain text if encryption fails
                putString("TwoFactorAuth", it)
            }
        } else {
            // Clear both encrypted and legacy keys when setting empty token
            remove("TwoFactorAuth_encrypted")
            remove("TwoFactorAuth")
        }
    }

internal var SharedPreferences.notificationWhitelist: NotificationHelper.NotificationPermissions
    get() {
        val result = getString("notificationWhitelist", "")
        if (result?.isNotEmpty() == true) {
            return Gson().fromJson(result, NotificationHelper.NotificationPermissions::class.java)
        }
        return NotificationHelper.NotificationPermissions()
    }
    set(it) = edit { putString("notificationWhitelist", Gson().toJson(it)) }

internal var SharedPreferences.discordToken: String
    get() {
        val encryptedToken = getString("discordToken_encrypted", "")
        return if (encryptedToken?.isNotEmpty() == true) {
            // Try to decrypt new format
            SecurityManager.decryptToken(encryptedToken) ?: run {
                // Fallback to legacy format for migration
                val legacyToken = getString("discordToken", "") ?: ""
                if (legacyToken.isNotEmpty()) {
                    // Migrate legacy data
                    discordToken = legacyToken // This will encrypt and store
                    // Clear legacy data
                    edit { remove("discordToken") }
                    legacyToken
                } else {
                    ""
                }
            }
        } else {
            // Fallback to legacy format for migration
            val legacyToken = getString("discordToken", "") ?: ""
            if (legacyToken.isNotEmpty()) {
                // Migrate legacy data
                discordToken = legacyToken // This will encrypt and store
                // Clear legacy data
                edit { remove("discordToken") }
                legacyToken
            } else {
                ""
            }
        }
    }
    set(it) = edit {
        if (it.isNotEmpty()) {
            val encryptedToken = SecurityManager.encryptToken(it)
            if (encryptedToken != null) {
                putString("discordToken_encrypted", encryptedToken)
                // Remove legacy key if it exists
                remove("discordToken")
            } else {
                // Fallback to plain text if encryption fails
                putString("discordToken", it)
            }
        } else {
            // Clear both encrypted and legacy keys when setting empty token
            remove("discordToken_encrypted")
            remove("discordToken")
        }
    }

internal var SharedPreferences.richPresenceEnabled: Boolean
    get() = getBoolean("richPresenceEnabled", false)
    set(it) = edit { putBoolean("richPresenceEnabled", it) }

internal var SharedPreferences.searchFeaturedWorlds: Boolean
    get() = getBoolean("searchFeaturedWorlds", false)
    set(it) = edit { putBoolean("searchFeaturedWorlds", it) }

internal var SharedPreferences.sortWorlds: String
    get() = getString("sortWorlds", "relevance")!!
    set(it) = edit { putString("sortWorlds", it) }

internal var SharedPreferences.worldsAmount: Int
    get() = getInt("worldsAmount", 50)
    set(it) = edit { putInt("worldsAmount", it) }

internal var SharedPreferences.usersAmount: Int
    get() = getInt("usersAmount", 50)
    set(it) = edit { putInt("usersAmount", it) }

internal var SharedPreferences.groupsAmount: Int
    get() = getInt("groupsAmount", 50)
    set(it) = edit { putInt("groupsAmount", it) }

internal var SharedPreferences.avatarsAmount: Int
    get() = getInt("groupsAmount", 50)
    set(it) = edit { putInt("groupsAmount", it) }

internal var SharedPreferences.richPresenceWarningAcknowledged: Boolean
    get() = getBoolean("richPresenceWarningAcknowledged", false)
    set(it) = edit { putBoolean("richPresenceWarningAcknowledged", it) }

internal var SharedPreferences.richPresenceWebhookUrl: String
    get() = getString("richPresenceWebhookUrl", "")!!
    set(it) = edit { putString("richPresenceWebhookUrl", it) }

internal var SharedPreferences.avatarProvider: String
    get() = getString("avatarProviderPreference", "avtrdb")!!
    set(it) = edit { putString("avatarProviderPreference", it) }

internal var SharedPreferences.networkLogging: Boolean
    get() = getBoolean("isDeveloperModeEnabled", false)
    set(it) = edit { putBoolean("isDeveloperModeEnabled", it) }

internal var SharedPreferences.minimalistMode: Boolean
    get() = getBoolean("isMinimalistModeEnabled", false)
    set(it) = edit { putBoolean("isMinimalistModeEnabled", it) }

internal var SharedPreferences.currentThemeOption: Int
    get() = getInt("currentThemeOption", 2)
    set(it) = edit { putInt("currentThemeOption", it) }

internal var SharedPreferences.columnCountOption: Int
    get() = getInt("columnCountOption", 0)
    set(value) = edit { putInt("columnCountOption", value) }

internal var SharedPreferences.fixedColumnSize: Int
    get() = getInt("fixedColumnSize", 2)
    set(it) = edit { putInt("fixedColumnSize", it) }

// Color overrides for theming. Store as Android ARGB Int. Use -1 to represent "not set".
internal var SharedPreferences.primaryColorOverride: Int
    get() = getInt("primaryColorOverride", -1)
    set(value) = edit { putInt("primaryColorOverride", value) }

internal var SharedPreferences.secondaryColorOverride: Int
    get() = getInt("secondaryColorOverride", -1)
    set(value) = edit { putInt("secondaryColorOverride", value) }
