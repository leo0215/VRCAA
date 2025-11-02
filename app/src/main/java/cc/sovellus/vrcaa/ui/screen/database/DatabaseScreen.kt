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

package cc.sovellus.vrcaa.ui.screen.database

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import java.time.LocalDateTime

class DatabaseScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { DatabaseScreenModel() }

        val backupLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/octet-stream")
        ) { uri ->
            uri?.let {
                model.backupDatabaseToUri(uri)
            }
        }

        val restoreLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let {
                model.restoreDatabaseFromUri(uri)
            }
        }

        Scaffold(
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

                    title = { Text(text = stringResource(R.string.database_page_title)) }
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        SectionHeader(stringResource(R.string.database_page_section_statistics))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.database_page_statistics_size),
                                    description = model.getDatabaseSizeReadable(),
                                    icon = Icons.Outlined.Storage,
                                    onClick = {}
                                ),
                                SettingsItem(
                                    title = stringResource(R.string.database_page_statistics_row_count),
                                    description = model.getDatabaseRowsReadable(),
                                    icon = Icons.Outlined.Storage,
                                    onClick = {}
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(stringResource(R.string.database_page_section_recovery))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.database_page_recovery_backup),
                                    description = stringResource(R.string.database_page_recovery_backup_description),
                                    icon = Icons.Outlined.Backup,
                                    onClick = {
                                        backupLauncher.launch("VRCAA-backup-${LocalDateTime.now()}.db")
                                    }
                                ),
                                SettingsItem(
                                    title = stringResource(R.string.database_page_recovery_restore),
                                    description = stringResource(R.string.database_page_recovery_restore_description),
                                    icon = Icons.Outlined.Restore,
                                    onClick = {
                                        restoreLauncher.launch(arrayOf("application/octet-stream"))
                                    }
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(stringResource(R.string.database_page_section_glide))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.database_page_glide_cache_size),
                                    description = model.getGlideCacheSizeReadable(),
                                    icon = Icons.Outlined.Storage,
                                    onClick = {}
                                ),
                                SettingsItem(
                                    title = stringResource(R.string.database_page_glide_clean_cache),
                                    description = stringResource(R.string.database_page_glide_clean_cache_description),
                                    icon = Icons.Outlined.CleaningServices,
                                    onClick = {
                                        model.cleanGlideCache()
                                    }
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