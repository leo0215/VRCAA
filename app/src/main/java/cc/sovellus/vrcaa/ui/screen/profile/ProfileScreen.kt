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

package cc.sovellus.vrcaa.ui.screen.profile

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
import android.content.SharedPreferences
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.App

import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.ui.components.card.ProfileCard
import cc.sovellus.vrcaa.ui.components.controls.QuickActionsRow
import cc.sovellus.vrcaa.ui.components.input.ComboInput
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.avatars.AvatarsScreen
import cc.sovellus.vrcaa.ui.screen.emojis.EmojisScreen
import cc.sovellus.vrcaa.ui.screen.gallery.GalleryScreen
import cc.sovellus.vrcaa.ui.screen.gallery.IconGalleryScreen
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import cc.sovellus.vrcaa.ui.screen.items.ItemsScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.prints.PrintsScreen
import cc.sovellus.vrcaa.ui.screen.stickers.StickersScreen
import cc.sovellus.vrcaa.ui.screen.worlds.WorldsScreen
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class ProfileScreen : Screen {

    override val key = uniqueScreenKey
    
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {
        val model = rememberScreenModel { ProfileScreenModel() }
        val state by model.state.collectAsState()

        when (val result = state) {
            is ProfileScreenModel.ProfileState.Loading -> LoadingIndicatorScreen().Content()
            is ProfileScreenModel.ProfileState.Result -> RenderProfile(model, result.profile)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun RenderProfile(model: ProfileScreenModel, profile: User) {
        val navigator = LocalNavigator.currentOrThrow
        var fabMenuExpanded by remember { mutableStateOf(false) }
        var badgeDialogTitle by remember { mutableStateOf<String?>(null) }
        var badgeDialogText by remember { mutableStateOf<String?>(null) }
        
        Box(modifier = Modifier.fillMaxSize()) {
            if (badgeDialogText != null) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { badgeDialogText = null; badgeDialogTitle = null },
                    title = { Text(text = badgeDialogTitle ?: "Badge") },
                    text = { Text(text = badgeDialogText!!) },
                    confirmButton = {
                        androidx.compose.material3.TextButton(onClick = { badgeDialogText = null; badgeDialogTitle = null }) {
                            Text(text = stringResource(android.R.string.ok))
                        }
                    }
                )
            }
            
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        profile.let {
                            ProfileCard(
                                thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl },
                                iconUrl = it.userIcon.ifEmpty { it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl } },
                                displayName = it.displayName,
                                statusDescription = it.statusDescription.ifEmpty {
                                    StatusHelper.getStatusFromString(
                                        it.status
                                    ).toString()
                                },
                                trustRankColor = TrustHelper.getTrustRankFromTags(it.tags).toColor(),
                                statusColor = StatusHelper.getStatusFromString(it.status).toColor(),
                                tags = profile.tags,
                                badges = profile.badges,
                                pronouns = profile.pronouns,
                                ageVerificationStatus = profile.ageVerificationStatus,
                                onPeek = { },
                                onBadgeClick = { badge ->
                                    badgeDialogTitle = badge.badgeName
                                    badgeDialogText = badge.badgeDescription
                                }
                            )
                        }
                    }

                    item {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start
                        ) {
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier.padding(top = 16.dp).defaultMinSize(minHeight = 80.dp).widthIn(Dp.Unspecified, 520.dp),
                            ) {
                                SubHeader(title = stringResource(R.string.profile_label_biography))
                                Description(text = profile.bio)

                                if (profile.lastActivity.isNotEmpty()) {
                                    val userTimeZone = TimeZone.getDefault().toZoneId()
                                    val formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
                                        .withLocale(Locale.getDefault())

                                    val lastActivity = ZonedDateTime.parse(profile.lastActivity).withZoneSameInstant(userTimeZone).format(formatter)

                                    SubHeader(title = stringResource(R.string.profile_label_last_activity))
                                    Description(text = lastActivity)
                                }

                                SubHeader(title = stringResource(R.string.profile_label_date_joined))
                                Description(text = profile.dateJoined)
                            }
                        }
                    }
                }
            }
            
            val items = listOf(
                Icons.Default.Collections to "Gallery",
                Icons.Default.Face to "User Icons",
                Icons.Default.Public to "Worlds",
                Icons.Default.Person to "Avatars",
                Icons.Default.Group to "Groups",
                Icons.Default.EmojiEmotions to "Emojis",
                Icons.AutoMirrored.Filled.Label to "Stickers",
                Icons.Default.Print to "Prints",
                Icons.Default.Inventory to "Items"
            )

            FloatingActionButtonMenu(
                modifier = Modifier.align(Alignment.BottomEnd),
                expanded = fabMenuExpanded,
                button = {
                    ToggleFloatingActionButton(
                        modifier = Modifier.semantics {
                            traversalIndex = -1f
                            stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                            contentDescription = "Toggle menu"
                        },
                        checked = fabMenuExpanded,
                        onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                    ) {
                        val imageVector by remember {
                            derivedStateOf {
                                if (fabMenuExpanded) Icons.Filled.Close else Icons.Filled.Edit
                            }
                        }
                        Icon(
                            painter = rememberVectorPainter(imageVector),
                            contentDescription = null,
                            modifier = Modifier.animateIcon({ if (fabMenuExpanded) 1f else 0f }),
                        )
                    }
                },
            ) {
                items.forEachIndexed { i, item ->
                    FloatingActionButtonMenuItem(
                        modifier = Modifier.semantics {
                            isTraversalGroup = true
                            if (i == items.size - 1) {
                                customActions = listOf(
                                    CustomAccessibilityAction(
                                        label = "Close menu",
                                        action = {
                                            fabMenuExpanded = false
                                            true
                                        },
                                    )
                                )
                            }
                        },
                        onClick = {
                            when (item.second) {
                                "Gallery" -> navigator.push(GalleryScreen())
                                "User Icons" -> navigator.push(IconGalleryScreen())
                                "Worlds" -> {
                                    val profile = CacheManager.getProfile()
                                    if (profile != null) {
                                        navigator.push(WorldsScreen(profile.displayName, profile.id, `private` = false))
                                    }
                                }
                                "Avatars" -> navigator.push(AvatarsScreen())
                                "Groups" -> {
                                    val profile = CacheManager.getProfile()
                                    if (profile != null) {
                                        navigator.push(UserGroupsScreen(profile.displayName, profile.id))
                                    }
                                }
                                "Emojis" -> navigator.push(EmojisScreen())
                                "Stickers" -> navigator.push(StickersScreen())
                                "Prints" -> {
                                    val profile = CacheManager.getProfile()
                                    if (profile != null) {
                                        navigator.push(PrintsScreen(profile.id))
                                    }
                                }
                                "Items" -> navigator.push(ItemsScreen())
                            }
                            fabMenuExpanded = false
                        },
                        icon = { Icon(item.first, contentDescription = null) },
                        text = { Text(text = item.second) },
                    )
                }
            }
        }
    }
}