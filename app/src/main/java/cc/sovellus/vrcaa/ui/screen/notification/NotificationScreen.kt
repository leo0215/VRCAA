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

package cc.sovellus.vrcaa.ui.screen.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.NotificationHelper
import cc.sovellus.vrcaa.ui.components.settings.SettingsGroup
import cc.sovellus.vrcaa.ui.components.settings.SettingsItem
import cc.sovellus.vrcaa.ui.components.settings.rememberThumbContent

class NotificationScreen(
    private val friendId: String, private val friendName: String
) : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val model = rememberScreenModel { NotificationScreenModel(friendId) }

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val notificationsOn = model.isNotificationsEnabled.value

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            topBar = {
                TopAppBar(
                    title = { Text(text = "${friendName}${stringResource(R.string.notification_title_player)}") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                )
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.notification_label_enable),
                                    onClick = {
                                        model.toggleNotifications(!model.isNotificationsEnabled.value)
                                    },
                                    trailingContent = {
                                        Switch(
                                            checked = notificationsOn,
                                            onCheckedChange = { model.toggleNotifications(it) },
                                            thumbContent = rememberThumbContent(
                                                isChecked = notificationsOn
                                            )
                                        )
                                    }
                                )
                            )
                        )
                    }
                    if (notificationsOn) {
                        item {
                            SettingsGroup(
                                items = listOf(
                                    SettingsItem(
                                        title = stringResource(R.string.notification_label_intent_offline),
                                        onClick = {
                                            model.toggleIntent(
                                                !model.isOfflineIntentEnabled.value,
                                                NotificationHelper.Intents.FRIEND_FLAG_OFFLINE
                                            )
                                        },
                                        trailingContent = {
                                            Switch(
                                                checked = model.isOfflineIntentEnabled.value,
                                                onCheckedChange = { v ->
                                                    model.toggleIntent(
                                                        v,
                                                        NotificationHelper.Intents.FRIEND_FLAG_OFFLINE
                                                    )
                                                },
                                                thumbContent = rememberThumbContent(
                                                    isChecked = model.isOfflineIntentEnabled.value
                                                )
                                            )
                                        }
                                    ),
                                    SettingsItem(
                                        title = stringResource(R.string.notification_label_intent_online),
                                        onClick = {
                                            model.toggleIntent(
                                                !model.isOnlineIntentEnabled.value,
                                                NotificationHelper.Intents.FRIEND_FLAG_ONLINE
                                            )
                                        },
                                        trailingContent = {
                                            Switch(
                                                checked = model.isOnlineIntentEnabled.value,
                                                onCheckedChange = { v ->
                                                    model.toggleIntent(
                                                        v,
                                                        NotificationHelper.Intents.FRIEND_FLAG_ONLINE
                                                    )
                                                },
                                                thumbContent = rememberThumbContent(
                                                    isChecked = model.isOnlineIntentEnabled.value
                                                )
                                            )
                                        }
                                    ),
                                    SettingsItem(
                                        title = stringResource(R.string.notification_label_intent_location),
                                        onClick = {
                                            model.toggleIntent(
                                                !model.isLocationIntentEnabled.value,
                                                NotificationHelper.Intents.FRIEND_FLAG_LOCATION
                                            )
                                        },
                                        trailingContent = {
                                            Switch(
                                                checked = model.isLocationIntentEnabled.value,
                                                onCheckedChange = { v ->
                                                    model.toggleIntent(
                                                        v,
                                                        NotificationHelper.Intents.FRIEND_FLAG_LOCATION
                                                    )
                                                },
                                                thumbContent = rememberThumbContent(
                                                    isChecked = model.isLocationIntentEnabled.value
                                                )
                                            )
                                        }
                                    ),
                                    SettingsItem(
                                        title = stringResource(R.string.notification_label_intent_status),
                                        onClick = {
                                            model.toggleIntent(
                                                !model.isStatusIntentEnabled.value,
                                                NotificationHelper.Intents.FRIEND_FLAG_STATUS
                                            )
                                        },
                                        trailingContent = {
                                            Switch(
                                                checked = model.isStatusIntentEnabled.value,
                                                onCheckedChange = { v ->
                                                    model.toggleIntent(
                                                        v,
                                                        NotificationHelper.Intents.FRIEND_FLAG_STATUS
                                                    )
                                                },
                                                thumbContent = rememberThumbContent(
                                                    isChecked = model.isStatusIntentEnabled.value
                                                )
                                            )
                                        }
                                    )
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}

