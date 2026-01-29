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

package cc.sovellus.vrcaa.ui.screen.advanced

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.settings.SectionHeader
import cc.sovellus.vrcaa.ui.components.settings.SettingsGroup
import cc.sovellus.vrcaa.ui.components.settings.SettingsItem
import cc.sovellus.vrcaa.ui.components.settings.rememberThumbContent
import cc.sovellus.vrcaa.ui.screen.network.NetworkLogScreen

class AdvancedScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { AdvancedScreenModel() }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },

                    title = { Text(text = stringResource(R.string.advanced_page_title)) }
                )
            },
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = it.calculateBottomPadding(),
                            top = it.calculateTopPadding()
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        SectionHeader(stringResource(R.string.advanced_page_section_networking))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.advanced_page_network_logging),
                                    description = stringResource(R.string.advanced_page_network_logging_description),
                                    icon = Icons.Outlined.Visibility,
                                    onClick = { model.toggleLogging() },
                                    trailingContent = {
                                        Switch(
                                            checked = model.networkLoggingMode.value,
                                            onCheckedChange = { model.toggleLogging() },
                                            thumbContent = rememberThumbContent(isChecked = model.networkLoggingMode.value)
                                        )
                                    }
                                ),
                                SettingsItem(
                                    title = stringResource(R.string.advanced_page_view_network_logs),
                                    description = null,
                                    icon = Icons.Outlined.Settings,
                                    onClick = {
                                        navigator.push(NetworkLogScreen())
                                    }
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(stringResource(R.string.advanced_page_section_background_activities))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.advanced_page_battery_optimization),
                                    description = stringResource(R.string.advanced_page_battery_optimization_description),
                                    icon = Icons.Outlined.PowerSettingsNew,
                                    onClick = { model.disableBatteryOptimizations() }
                                ),
                                SettingsItem(
                                    title = stringResource(R.string.advanced_page_kill_service),
                                    description = stringResource(R.string.advanced_page_kill_service_description),
                                    icon = Icons.Outlined.PowerSettingsNew,
                                    onClick = { model.killBackgroundService() }
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(stringResource(R.string.advanced_page_section_database))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.advanced_page_database_delete),
                                    description = stringResource(R.string.advanced_page_database_delete_description),
                                    icon = Icons.Outlined.Delete,
                                    onClick = { model.deleteDatabase() },
                                    isDestructive = true
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
        )
    }
}