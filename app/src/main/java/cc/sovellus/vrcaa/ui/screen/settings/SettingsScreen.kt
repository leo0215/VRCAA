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

package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ImagesearchRoller
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.core.net.toUri
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.richPresenceWarningAcknowledged
import cc.sovellus.vrcaa.ui.components.dialog.DisclaimerDialog
import cc.sovellus.vrcaa.ui.components.dialog.LogoutDialog
import cc.sovellus.vrcaa.ui.components.settings.SectionHeader
import cc.sovellus.vrcaa.ui.components.settings.SettingsGroup
import cc.sovellus.vrcaa.ui.components.settings.SettingsItem
import cc.sovellus.vrcaa.ui.screen.about.AboutScreen
import cc.sovellus.vrcaa.ui.screen.advanced.AdvancedScreen
import cc.sovellus.vrcaa.ui.screen.database.DatabaseScreen
import cc.sovellus.vrcaa.ui.screen.presence.RichPresenceScreen
import cc.sovellus.vrcaa.ui.screen.theme.ThemeScreen

class SettingsScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { SettingsScreenModel() }

        val dialogState = remember { mutableStateOf(false) }
        val logoutState = remember { mutableStateOf(false) }

        // TODO: string to translatable
        val title = buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append("Notice!")
            }
            append(" ")
            append("Are you sure?")
        }

        val description = buildAnnotatedString {
            append("This feature is not ")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append("condoned")
            }
            append(" nor supported by discord, it may bear ")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append("consequences")
            }
            append(", that may get your account ")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append("terminated")
            }
            append(", if you understand the ")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append("risks")
            }
            append(" press \"Continue\" to continue, or press \"Go Back\".")
        }

        if (dialogState.value) {
            DisclaimerDialog(
                onDismiss = { dialogState.value = false },
                onConfirmation = {
                    dialogState.value = false
                    model.preferences.richPresenceWarningAcknowledged = true
                    navigator.parent?.parent?.push(RichPresenceScreen())
                },
                title,
                description
            )
        }
        
        if (logoutState.value) {
            LogoutDialog(
                onDismiss = { logoutState.value = false }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                // Logo Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = if (App.isAppInDarkTheme()) { painterResource(R.drawable.logo_dark) } else { painterResource(R.drawable.logo_white) },
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // General Settings Section
            item {
                SectionHeader(stringResource(R.string.settings_section_general))
            }

            item {
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = stringResource(R.string.settings_item_about),
                            description = stringResource(R.string.settings_item_about_description),
                            icon = Icons.Outlined.Info,
                            onClick = {
                                navigator.parent?.parent?.push(AboutScreen())
                            }
                        ),
                        SettingsItem(
                            title = stringResource(R.string.settings_item_theming),
                            description = stringResource(R.string.settings_item_theming_description),
                            icon = Icons.Outlined.ImagesearchRoller,
                            onClick = {
                                navigator.parent?.parent?.push(ThemeScreen())
                            }
                        )
                    )
                )
            }

            // Advanced Settings Section
            item {
                Spacer(modifier = Modifier.height(2.dp))
                SectionHeader(stringResource(R.string.settings_section_advanced))
            }

            item {
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = stringResource(R.string.settings_item_rich_presence),
                            description = stringResource(R.string.settings_item_rich_presence_description),
                            icon = Icons.Outlined.Image,
                            onClick = {
                                if (model.preferences.richPresenceWarningAcknowledged)
                                    navigator.parent?.parent?.push(RichPresenceScreen())
                                else
                                    dialogState.value = true
                            }
                        ),
                        SettingsItem(
                            title = stringResource(R.string.settings_item_database_settings),
                            description = stringResource(R.string.settings_item_database_settings_description),
                            icon = Icons.Outlined.Storage,
                            onClick = {
                                navigator.parent?.parent?.push(DatabaseScreen())
                            }
                        ),
                        SettingsItem(
                            title = stringResource(R.string.settings_item_advanced_settings),
                            description = stringResource(R.string.settings_item_advanced_settings_description),
                            icon = Icons.Outlined.DeveloperMode,
                            onClick = {
                                navigator.parent?.parent?.push(AdvancedScreen())
                            }
                        )
                    )
                )
            }

            // Community Section
            item {
                Spacer(modifier = Modifier.height(2.dp))
                SectionHeader(stringResource(R.string.settings_section_community))
            }

            item {
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = stringResource(R.string.about_page_translate_title),
                            description = null,
                            icon = Icons.Filled.Translate,
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    BuildConfig.CROWDIN_URL.toUri()
                                )
                                context.startActivity(intent)
                            }
                        ),
                        SettingsItem(
                            title = stringResource(R.string.settings_kofi_donation_button),
                            description = null,
                            icon = Icons.Filled.Coffee,
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    BuildConfig.KOFI_URL.toUri()
                                )
                                context.startActivity(intent)
                            }
                        )
                    )
                )
            }

            // Account Section
            item {
                Spacer(modifier = Modifier.height(2.dp))
                SectionHeader(stringResource(R.string.settings_section_account))
            }

            item {
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = stringResource(R.string.settings_item_logout),
                            description = null,
                            icon = Icons.AutoMirrored.Outlined.Logout,
                            onClick = {
                                logoutState.value = true
                            },
                            isDestructive = true
                        )
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}