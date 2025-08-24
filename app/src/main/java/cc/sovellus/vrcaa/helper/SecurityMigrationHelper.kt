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

package cc.sovellus.vrcaa.helper

import android.content.SharedPreferences
import android.util.Log
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.discordToken
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.extension.userCredentials

object SecurityMigrationHelper {
    
    private const val MIGRATION_KEY = "security_migration_completed"
    private const val MIGRATION_VERSION = 1
    
    /**
     * Performs one-time migration of existing plain text credentials to encrypted format
     */
    fun performSecurityMigration(preferences: SharedPreferences) {
        val migrationCompleted = preferences.getInt(MIGRATION_KEY, 0)
        
        if (migrationCompleted >= MIGRATION_VERSION) {
            // Migration already completed
            return
        }
        
        Log.i("SecurityMigration", "Starting security migration...")
        
        try {
            // Trigger migration by accessing the properties
            // The getters will automatically detect legacy data and migrate it
            
            // Migrate user credentials
            val credentials = preferences.userCredentials
            if (credentials.first.isNotEmpty() || credentials.second.isNotEmpty()) {
                Log.i("SecurityMigration", "Migrated user credentials")
            }
            
            // Migrate auth token
            val authToken = preferences.authToken
            if (authToken.isNotEmpty()) {
                Log.i("SecurityMigration", "Migrated auth token")
            }
            
            // Migrate 2FA token
            val twoFactorToken = preferences.twoFactorToken
            if (twoFactorToken.isNotEmpty()) {
                Log.i("SecurityMigration", "Migrated 2FA token")
            }
            
            // Migrate Discord token
            val discordToken = preferences.discordToken
            if (discordToken.isNotEmpty()) {
                Log.i("SecurityMigration", "Migrated Discord token")
            }
            
            // Mark migration as completed
            preferences.edit()
                .putInt(MIGRATION_KEY, MIGRATION_VERSION)
                .apply()
            
            Log.i("SecurityMigration", "Security migration completed successfully")
            
        } catch (e: Exception) {
            Log.e("SecurityMigration", "Security migration failed", e)
            // Don't mark as completed if migration failed
        }
    }
    
    /**
     * Checks if security migration has been completed
     */
    fun isMigrationCompleted(preferences: SharedPreferences): Boolean {
        return preferences.getInt(MIGRATION_KEY, 0) >= MIGRATION_VERSION
    }
    
    /**
     * Forces a re-migration (use with caution)
     */
    fun resetMigrationStatus(preferences: SharedPreferences) {
        preferences.edit()
            .remove(MIGRATION_KEY)
            .apply()
        Log.w("SecurityMigration", "Migration status reset - migration will run on next app start")
    }
}