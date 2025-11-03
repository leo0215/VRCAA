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

package cc.sovellus.vrcaa.ui.screen.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cabin
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.os.bundleOf
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R

import cc.sovellus.vrcaa.activity.MainActivity
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.card.QuickMenuCard
import cc.sovellus.vrcaa.ui.components.dialog.NoInternetDialog
import cc.sovellus.vrcaa.ui.components.input.ComboInput
import cc.sovellus.vrcaa.ui.screen.feed.FeedSearchScreen
import cc.sovellus.vrcaa.ui.screen.notifications.NotificationsScreen
import cc.sovellus.vrcaa.ui.screen.search.SearchResultScreen
import cc.sovellus.vrcaa.ui.screen.economy.EconomyScreen
import cc.sovellus.vrcaa.ui.tabs.FavoritesTab
import cc.sovellus.vrcaa.ui.tabs.FeedTab
import cc.sovellus.vrcaa.ui.tabs.FriendsTab
import cc.sovellus.vrcaa.ui.tabs.HomeTab
import cc.sovellus.vrcaa.ui.tabs.ProfileTab
import cc.sovellus.vrcaa.ui.tabs.SettingsTab
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.launch
import android.content.SharedPreferences
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect

class NavigationScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
    @Composable
    override fun Content() {

        val navigator: Navigator = LocalNavigator.currentOrThrow
        val context: Context = LocalContext.current
        val model = navigator.rememberNavigatorScreenModel { NavigationScreenModel() }

        if (model.hasNoInternet.value) {
            NoInternetDialog(onClick = {
                model.hasNoInternet.value = false

                val bundle = bundleOf()
                bundle.putBoolean("RESTART_SESSION", true)

                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtras(bundle)
                context.startActivity(intent)


                if (context is Activity) {
                    context.finish()
                }
            })
        }

        val tabs = remember {
            arrayListOf(
                HomeTab,
                FriendsTab,
                FavoritesTab,
                FeedTab,
                ProfileTab,
                SettingsTab
            )
        }

        TabNavigator(tabs[0], tabDisposable = {
            TabDisposable(
                navigator = it, tabs = tabs
            )
        }) { tabNavigator ->
            val settingsSheetState = rememberModalBottomSheetState()
            val profileSheetState = rememberModalBottomSheetState()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

            var showSettingsSheet by remember { mutableStateOf(false) }
            var isMenuExpanded by remember { mutableStateOf(false) }
            var showProfileSheet by remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()

            // 預載入 drawer 中的 banner 圖片
            val profile = CacheManager.getProfile()
            LaunchedEffect(profile?.id) {
                profile?.let {
                    val thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl }
                    // 預載入 banner 圖片，確保在 drawer 打開前已載入
                    com.bumptech.glide.Glide.with(context)
                        .load(thumbnailUrl)
                        .preload()
                }
            }



            var pressBackCounter by remember { mutableIntStateOf(0) }

            BackHandler(enabled = drawerState.isOpen) {
                scope.launch {
                    drawerState.close()
                }
            }

            BackHandler(enabled = !drawerState.isOpen, onBack = {
                if (tabNavigator.current != HomeTab) {
                    tabNavigator.current = HomeTab
                } else {
                    pressBackCounter++
                }

                if (tabNavigator.current == HomeTab) {
                    if (pressBackCounter == 1) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.misc_exit_toast_label),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (pressBackCounter == 2) {
                        if (context is Activity) context.finish()
                    }
                }
            })

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 用戶資料卡片
                            CacheManager.getProfile()?.let {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(topEnd = 16.dp))
                                ) {
                                    QuickMenuCard(
                                        thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl },
                                        iconUrl = it.userIcon.ifEmpty { it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl } },
                                        displayName = it.displayName,
                                        statusDescription = it.statusDescription.ifEmpty { StatusHelper.getStatusFromString(it.status).toString() },
                                        trustRankColor = TrustHelper.getTrustRankFromTags(it.tags).toColor(),
                                        statusColor = StatusHelper.getStatusFromString(it.status).toColor(),
                                        tags = it.tags,
                                        badges = it.badges
                                    )

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    drawerState.close()
                                                }
                                                showProfileSheet = true
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            // 導航項目
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                tabs.forEach { tab ->
                                    val isSelected = tabNavigator.current.key == tab.key
                                    NavigationDrawerItem(
                                        icon = {
                                            Icon(
                                                painter = tab.options.icon!!,
                                                contentDescription = tab.options.title
                                            )
                                        },
                                        label = { Text(tab.options.title) },
                                        selected = isSelected,
                                        onClick = {
                                            pressBackCounter = 0
                                            tabNavigator.current = tab
                                            scope.launch {
                                                drawerState.close()
                                            }
                                        }
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            // 訂閱與餘額
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.AccountBalanceWallet,
                                            contentDescription = "訂閱與餘額"
                                        )
                                    },
                                    label = { Text("訂閱與餘額") },
                                    selected = false,
                                    onClick = {
                                        navigator.push(EconomyScreen())
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // 版本號
                            Text(
                                text = "VRCAA Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                    when (tabNavigator.current.options.index) {
                        HomeTab.options.index -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                SearchBar(
                                    modifier = Modifier.widthIn(max = 1080.dp),
                                    inputField = {
                                        InputField(
                                            query = model.searchText.value,
                                            onQueryChange = { model.searchText.value = it; },
                                            onSearch = {
                                                model.searchModeActivated.value = false
                                                navigator.push(SearchResultScreen(model.searchText.value))
                                                model.addSearchHistory()
                                            },
                                            expanded = model.searchModeActivated.value,
                                            onExpandedChange = {
                                                model.searchModeActivated.value = true
                                            },
                                            enabled = true,
                                            placeholder = {
                                                if (!App.isMinimalistModeEnabled()) {
                                                    Text(
                                                        text = stringResource(R.string.main_search_placeholder),
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            },
                                            leadingIcon = {
                                                if (model.searchModeActivated.value) {
                                                    IconButton(onClick = {
                                                        model.searchModeActivated.value = false
                                                    }) {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                            contentDescription = null
                                                        )
                                                    }
                                                } else {
                                                    IconButton(onClick = {
                                                        scope.launch {
                                                            drawerState.open()
                                                        }
                                                    }) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Menu,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            },
                                            trailingIcon = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    if (model.searchModeActivated.value) {
                                                        IconButton(onClick = { model.clearSearchText() }) {
                                                            Icon(
                                                                imageVector = Icons.Filled.Close,
                                                                contentDescription = null
                                                            )
                                                        }
                                                    } else {
                                                        IconButton(onClick = { showSettingsSheet = true }) {
                                                            Icon(
                                                                imageVector = Icons.Filled.MoreVert,
                                                                contentDescription = null
                                                            )
                                                        }
                                                    }
                                                    if (!model.searchModeActivated.value) {
                                                        val profile = CacheManager.getProfile()
                                                        if (model.cacheBuilt.value && profile != null) {
                                                            val avatarUrl = profile.userIcon.ifEmpty {
                                                                profile.profilePicOverride.ifEmpty { profile.currentAvatarImageUrl }
                                                            }
                                                            GlideImage(
                                                                model = avatarUrl,
                                                                contentDescription = null,
                                                                modifier = Modifier
                                                                    .size(48.dp)
                                                                    .clip(CircleShape)
                                                                    .clickable { tabNavigator.current = ProfileTab },
                                                                loading = placeholder(R.drawable.image_placeholder),
                                                                failure = placeholder(R.drawable.image_placeholder)
                                                            )
                                                        } else if (profile != null) {
                                                            GlideImage(
                                                                model = profile.userIcon,
                                                                contentDescription = null,
                                                                modifier = Modifier
                                                                    .size(48.dp)
                                                                    .clip(CircleShape)
                                                                    .clickable { tabNavigator.current = ProfileTab },
                                                                loading = placeholder(R.drawable.image_placeholder),
                                                                failure = placeholder(R.drawable.image_placeholder)
                                                            )
                                                        }
                                                    }
                                                }
                                            })
                                    },
                                    expanded = model.searchModeActivated.value,
                                    onExpandedChange = { },
                                    shape = SearchBarDefaults.inputFieldShape,
                                    colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                                    tonalElevation = if (model.searchModeActivated.value) {
                                        0.dp
                                    } else {
                                        8.dp
                                    },
                                    windowInsets = SearchBarDefaults.windowInsets.exclude(
                                        WindowInsets(left = 4.dp, right = 4.dp)
                                    ),
                                    content = {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            if (model.searchModeActivated.value) {
                                                items(model.searchHistory.size) {
                                                    val item = model.searchHistory.reversed()[it]
                                                    ListItem(leadingContent = {
                                                        Icon(
                                                            imageVector = Icons.Filled.History,
                                                            contentDescription = null
                                                        )
                                                    }, headlineContent = {
                                                        Text(text = item)
                                                    }, modifier = Modifier.clickable(onClick = {
                                                        model.searchModeActivated.value = false
                                                        navigator.push(
                                                            SearchResultScreen(
                                                                item
                                                            )
                                                        )
                                                    }))
                                                }
                                            }
                                        }
                                    },
                                )
                            }
                        }

                        FriendsTab.options.index -> {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(id = R.string.tabs_label_friends),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }

                        ProfileTab.options.index -> {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(id = R.string.tabs_label_profile),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }

                        ProfileTab.options.index -> {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(id = R.string.tabs_label_profile),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        if (CacheManager.isBuilt()) {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }

                        FavoritesTab.options.index -> {
                            TopAppBar(
                                title = { Text(stringResource(R.string.tabs_label_favorites)) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = null
                                        )
                                    }
                                },
                                actions = {
                                IconButton(onClick = { isMenuExpanded = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = null
                                    )
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        DropdownMenu(
                                            expanded = isMenuExpanded,
                                            onDismissRequest = { isMenuExpanded = false },
                                            offset = DpOffset(0.dp, 0.dp)
                                        ) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    scope.launch {
                                                        FavoriteManager.refresh()
                                                    }

                                                        Toast.makeText(
                                                            context,
                                                            context.getString(R.string.favorite_toast_refreshed_favorites),
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        isMenuExpanded = false
                                                    },
                                                    text = { Text(stringResource(R.string.favorite_tab_refresh_favorites)) })
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        FeedTab.options.index -> {
                            TopAppBar(
                                actions = {
                                    IconButton(
                                        modifier = Modifier.size(64.dp, 64.dp),
                                        onClick = {
                                            if (CacheManager.isBuilt()) {
                                                navigator.push(FeedSearchScreen())
                                            }
                                        }) {
                                        Icon(Icons.Filled.Search, contentDescription = null)
                                    }
                                    IconButton(
                                        modifier = Modifier.size(64.dp, 64.dp),
                                        onClick = {
                                            if (CacheManager.isBuilt()) {
                                                navigator.push(NotificationsScreen())
                                            }
                                        }) {
                                        BadgedBox(
                                            badge = {
                                                if (model.notificationsCount.intValue > 0) {
                                                    Badge {
                                                        Text("${model.notificationsCount.intValue}")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Filled.Notifications, contentDescription = null)
                                        }
                                    }
                                },
                                title = {
                                    Text(
                                        text = stringResource(id = R.string.tabs_label_feed),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }

                        SettingsTab.options.index -> {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(id = R.string.tabs_label_settings),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }
                    }
                }, content = { padding ->

                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        CurrentTab()
                    }

                    if (showProfileSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showProfileSheet = false
                            }, sheetState = profileSheetState
                        ) {
                            LazyColumn {
                                item {
                                    ListItem(leadingContent = {
                                        OutlinedButton(onClick = {
                                            showProfileSheet = false
                                        }) {
                                            Text(stringResource(R.string.profile_edit_dialog_button_cancel))
                                        }
                                    }, trailingContent = {
                                        Button(onClick = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.profile_edit_dialog_toast_updated),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            scope.launch {
                                                CacheManager.getProfile()?.let {
                                                    api.user.updateProfileByUserId(
                                                        it.id,
                                                        model.status.value,
                                                        model.description.value,
                                                        model.bio.value,
                                                        model.bioLinks,
                                                        model.pronouns.value,
                                                        if (model.ageVerified.value) {
                                                            model.verifiedStatus.value
                                                        } else {
                                                            null
                                                        }
                                                    )?.let { user ->
                                                        CacheManager.updateProfile(user)
                                                    }
                                                }
                                                profileSheetState.hide()
                                            }.invokeOnCompletion {
                                                if (!settingsSheetState.isVisible) {
                                                    showProfileSheet = false
                                                }
                                            }
                                        }) {
                                            Text(stringResource(R.string.profile_edit_dialog_button_apply))
                                        }
                                    }, headlineContent = { })
                                }
                                item {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = stringResource(R.string.profile_edit_dialog_title_status),
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        },
                                        supportingContent = {
                                            ComboInput(
                                                options = listOf(
                                                    "join me",
                                                    "active",
                                                    "ask me",
                                                    "busy"
                                                ),
                                                readableOptions = mapOf(
                                                    "join me" to "Join Me",
                                                    "active" to "Active",
                                                    "ask me" to "Ask Me",
                                                    "busy" to "Busy"
                                                ),
                                                selection = model.status
                                            )
                                        }
                                    )
                                }

                                item {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = stringResource(R.string.profile_edit_dialog_title_status_description),
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        },
                                        supportingContent = {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                value = model.description.value,
                                                onValueChange = {
                                                    model.description.value = it
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                                            )
                                        }
                                    )
                                }

                                item {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = stringResource(R.string.profile_edit_dialog_title_pronouns),
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        },
                                        supportingContent = {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                value = model.pronouns.value,
                                                onValueChange = {
                                                    if (it.length <= 32)
                                                        model.pronouns.value = it
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                                            )
                                        }
                                    )
                                }

                                item {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = stringResource(R.string.profile_edit_dialog_title_bio),
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        },
                                        supportingContent = {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                value = model.bio.value,
                                                onValueChange = {
                                                    model.bio.value = it
                                                },
                                                minLines = 8,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                            )
                                        }
                                    )
                                }

                                if (model.ageVerified.value) {
                                    item {
                                        ListItem(
                                            headlineContent = {
                                                Text(
                                                    text = stringResource(R.string.profile_edit_dialog_title_age_verification_visibility),
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                            },
                                            supportingContent = {
                                                ComboInput(
                                                    options = listOf("hidden", "18+"),
                                                    readableOptions = mapOf(
                                                        "hidden" to "Hidden",
                                                        "18+" to "18+ Verified"
                                                    ),
                                                    selection = model.verifiedStatus
                                                )
                                            }
                                        )
                                    }
                                }

                                item {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = stringResource(R.string.profile_edit_dialog_title_bio_links),
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        },
                                        supportingContent = {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                value = model.bioLinks[0],
                                                onValueChange = {
                                                    model.bioLinks[0] = it
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                            )
                                        }
                                    )

                                    ListItem(
                                        headlineContent = { },
                                        supportingContent = {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                value = model.bioLinks[1],
                                                onValueChange = {
                                                    model.bioLinks[1] = it
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                            )
                                        }
                                    )

                                    ListItem(
                                        headlineContent = { },
                                        supportingContent = {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                value = model.bioLinks[2],
                                                onValueChange = {
                                                    model.bioLinks[2] = it
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (showSettingsSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showSettingsSheet = false
                            }, sheetState = settingsSheetState
                        ) {
                            LazyColumn {
                                item {
                                    ListItem(leadingContent = {
                                        OutlinedButton(onClick = {
                                            model.resetSettings()
                                        }) {
                                            Text(stringResource(R.string.search_filter_button_reset))
                                        }
                                    }, trailingContent = {
                                        Button(onClick = {
                                            scope.launch {
                                                model.applySettings()
                                                settingsSheetState.hide()
                                            }.invokeOnCompletion {
                                                if (!settingsSheetState.isVisible) {
                                                    showSettingsSheet = false
                                                }
                                            }
                                        }) {
                                            Text(stringResource(R.string.search_filter_button_apply))
                                        }
                                    }, headlineContent = { })
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_worlds)) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Outlined.Cabin,
                                                contentDescription = null
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.padding(2.dp))
                                }

                                item {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = stringResource(R.string.search_filter_category_worlds_sort_by),
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        },
                                        supportingContent = {
                                            val options = listOf(
                                                "popularity",
                                                "heat",
                                                "trust",
                                                "shuffle",
                                                "random",
                                                "favorites",
                                                "publicationDate",
                                                "labsPublicationDate",
                                                "created",
                                                "updated",
                                                "order",
                                                "relevance",
                                                "name"
                                            )
                                            ComboInput(
                                                options = options, selection = model.sortWorlds
                                            )
                                        }
                                    )
                                }
                                item {
                                    var worldCount by remember { mutableStateOf(model.worldsAmount.intValue.toString()) }
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_worlds_count)) },
                                        trailingContent = {
                                            OutlinedTextField(
                                                value = worldCount,
                                                onValueChange = {
                                                    worldCount = it
                                                    if (it.isNotEmpty()) model.worldsAmount.intValue =
                                                        it.toIntOrNull()
                                                            ?: model.worldsAmount.intValue
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                    )
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_users)) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Outlined.People,
                                                contentDescription = null
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.padding(2.dp))
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_users_count)) },
                                        trailingContent = {
                                            OutlinedTextField(
                                                value = model.usersAmount.intValue.toString(),
                                                onValueChange = {
                                                    if (it.isNotEmpty()) model.usersAmount.intValue =
                                                        it.toIntOrNull()
                                                            ?: model.usersAmount.intValue
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                    )
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_avatars)) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Outlined.Person,
                                                contentDescription = null
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.padding(2.dp))
                                }
                                item {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = stringResource(R.string.search_filter_category_avatars_provider),
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        },
                                        supportingContent = {
                                            val options = listOf("avtrdb", "justhparty")
                                            val optionsReadable = mapOf(
                                                "avtrdb" to "avtrDB",
                                                "justhparty" to "Just-H Party"
                                            )
                                            ComboInput(
                                                options = options,
                                                selection = model.avatarProvider,
                                                readableOptions = optionsReadable
                                            )
                                        }
                                    )
                                }
                                item {
                                    var avatarCount by remember { mutableStateOf(model.avatarsAmount.intValue.toString()) }
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_label_count)) },
                                        trailingContent = {
                                            OutlinedTextField(
                                                value = avatarCount,
                                                onValueChange = {
                                                    avatarCount = it
                                                    if (it.isNotEmpty()) model.avatarsAmount.intValue =
                                                        it.toIntOrNull()
                                                            ?: model.avatarsAmount.intValue
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        })
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_groups)) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Outlined.Groups,
                                                contentDescription = null
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.padding(2.dp))
                                }
                                item {
                                    var groupCount by remember { mutableStateOf(model.groupsAmount.intValue.toString()) }
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_groups_count)) },
                                        trailingContent = {
                                            OutlinedTextField(
                                                value = groupCount,
                                                onValueChange = {
                                                    groupCount = it
                                                    if (it.isNotEmpty()) model.groupsAmount.intValue =
                                                        it.toIntOrNull()
                                                            ?: model.groupsAmount.intValue
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                    bottomBar = {
                    // 側邊選單，無需底部導航列
                })
            }
        }
    }
}
