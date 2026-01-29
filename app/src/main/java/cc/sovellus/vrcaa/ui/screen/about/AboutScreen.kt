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

package cc.sovellus.vrcaa.ui.screen.about

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.misc.Logo
import cc.sovellus.vrcaa.ui.components.settings.SectionHeader
import cc.sovellus.vrcaa.ui.components.settings.SettingsGroup
import cc.sovellus.vrcaa.ui.components.settings.SettingsItem
import cc.sovellus.vrcaa.ui.screen.licenses.LicensesScreen

class AboutScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

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
                    title = { Text(text = stringResource(R.string.about_page_title)) }
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
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Logo(size = 128.dp)
                        }
                    }

                    item {
                        SectionHeader(stringResource(R.string.about_page_section_version_info))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.about_page_version),
                                    description = "${BuildConfig.VERSION_NAME} ${BuildConfig.FLAVOR} (${BuildConfig.GIT_BRANCH}, ${BuildConfig.GIT_HASH})",
                                    icon = null,
                                    onClick = {}
                                ),
                                SettingsItem(
                                    title = stringResource(R.string.about_page_model),
                                    description = Build.MODEL,
                                    icon = null,
                                    onClick = {}
                                ),
                                SettingsItem(
                                    title = stringResource(R.string.about_page_vendor),
                                    description = Build.MANUFACTURER,
                                    icon = null,
                                    onClick = {}
                                ),
                                SettingsItem(
                                    title = stringResource(R.string.about_page_system_version),
                                    description = "Android ${Build.VERSION.RELEASE}",
                                    icon = null,
                                    onClick = {}
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(stringResource(R.string.about_page_section_other))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.about_page_open_source_licenses_title),
                                    description = null,
                                    icon = Icons.Outlined.Code,
                                    onClick = {
                                        navigator.push(LicensesScreen())
                                    }
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        )
    }
}