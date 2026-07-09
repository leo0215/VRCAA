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

package cc.sovellus.vrcaa.ui.screen.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites.FavoriteType
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.manager.FriendManager
import cc.sovellus.vrcaa.ui.components.controls.connectedButtonGroupToggleColors
import cc.sovellus.vrcaa.ui.components.dialog.FavoriteEditDialog
import cc.sovellus.vrcaa.ui.components.dialog.GenericDialog
import cc.sovellus.vrcaa.ui.components.layout.FavoriteVerticalSegmentRowItem
import cc.sovellus.vrcaa.ui.components.settings.ExpandableSettingsGroup
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.favorites.FavoritesScreenModel.FavoriteState
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen
import cc.sovellus.vrcaa.ui.theme.appBackground

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
class FavoritesScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { FavoritesScreenModel() }

        val state by model.state.collectAsState()

        when (state) {
            is FavoriteState.Loading -> LoadingIndicatorScreen().Content()
            is FavoriteState.Result -> ShowScreen(model)
            else -> {}
        }
    }

    @Composable
    fun ShowScreen(model: FavoritesScreenModel) {

        val groupMetadata = model.groupMetadata.collectAsState()

        if (model.editDialogShown.value) {
            FavoriteEditDialog(
                tag = model.currentSelectedGroup.value,
                isFriend = model.currentSelectedIsFriend.value,
                groupMetadata = groupMetadata.value,
                onDismiss = {
                    model.editDialogShown.value = false
                    model.currentSelectedIsFriend.value = false
                },
                onConfirmation = {
                    model.editDialogShown.value = false
                    model.currentSelectedIsFriend.value = false
                },
                onUpdateGroupMetadata = { tag, metadata, isFriend ->
                    if (isFriend) {
                        FavoriteManager.updateGroupMetadataOnlyName(tag, metadata)
                    } else {
                        FavoriteManager.updateGroupMetadata(tag, metadata)
                    }
                }
            )
        }

        if (model.deleteDialogShown.value) {
            GenericDialog(
                title = stringResource(R.string.favorite_remove_dialog_title),
                description = stringResource(R.string.favorite_remove_dialog_description),
                onDismiss = {
                    model.deleteDialogShown.value = false
                },
                onConfirmation = {
                    model.removeFavorite()
                    model.deleteDialogShown.value = false
                }
            )
        }

        val options = stringArrayResource(R.array.favorites_selection_options)
        val icons = listOf(Icons.Filled.Cabin, Icons.Filled.Person, Icons.Filled.Group)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.appBackground),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                val modifiers = List(options.size) { Modifier.weight(1f) }
                options.forEachIndexed { index, label ->
                    val selected = index == model.currentIndex.intValue
                    ToggleButton(
                        checked = selected,
                        onCheckedChange = { model.currentIndex.intValue = index },
                        modifier = modifiers[index],
                        shapes = when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                        colors = connectedButtonGroupToggleColors(),
                    ) {
                        Icon(
                            imageVector = if (selected) Icons.Filled.Check else icons[index],
                            contentDescription = null,
                        )
                        Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                        Text(text = label, softWrap = true, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            Spacer(modifier = Modifier.padding(4.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                item {
                    when (model.currentIndex.intValue) {
                        0 -> ShowWorlds(model, groupMetadata.value)
                        1 -> ShowAvatars(model, groupMetadata.value)
                        2 -> ShowFriends(model, groupMetadata.value)
                    }
                }
            }
        }
    }

    @Composable
    fun ShowWorlds(
        model: FavoritesScreenModel,
        groupMetadata: Map<String, FavoriteManager.FavoriteGroupMetadata>,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val worldList = model.worldList.collectAsStateWithLifecycle()

        worldList.value.forEach { item ->
            if (item.value.isNotEmpty()) {
                val metadata = groupMetadata[item.key]
                val title = "${metadata?.displayName ?: metadata?.name ?: item.key} (${metadata?.size ?: 0}/${FavoriteManager.getMaximumFavoritesForType(FavoriteType.FAVORITE_WORLD)})"

                ExpandableSettingsGroup(
                    headerTitle = title,
                    modifier = Modifier.padding(bottom = 12.dp),
                    stateKey = "${model.currentIndex.intValue}|${item.key}",
                    initiallyExpanded = false,
                    headerTrailing = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    model.currentSelectedGroup.value = item.key
                                    model.editDialogShown.value = true
                                },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                    ) {
                        val rows = item.value.distinct()
                        rows.forEachIndexed { index, world ->
                            FavoriteVerticalSegmentRowItem(
                                name = world.name,
                                url = world.thumbnailUrl,
                                index = index,
                                count = rows.size,
                                onClick = {
                                    if (world.name != "???") {
                                        navigator.parent?.parent?.push(WorldScreen(world.id) {
                                            model.deleteDialogShown.value = true
                                            model.currentSelectedType.value = FavoriteType.FAVORITE_WORLD
                                            model.currentSelectedId.value = world.id
                                        })
                                    } else {
                                        model.deleteDialogShown.value = true
                                        model.currentSelectedType.value = FavoriteType.FAVORITE_WORLD
                                        model.currentSelectedId.value = world.id
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowAvatars(
        model: FavoritesScreenModel,
        groupMetadata: Map<String, FavoriteManager.FavoriteGroupMetadata>,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val avatarList = model.avatarList.collectAsStateWithLifecycle()

        avatarList.value.forEach { item ->
            if (item.value.isNotEmpty()) {
                val metadata = groupMetadata[item.key]
                val title = "${metadata?.displayName ?: metadata?.name ?: item.key} (${metadata?.size ?: 0}/${FavoriteManager.getMaximumFavoritesForType(FavoriteType.FAVORITE_AVATAR)})"

                ExpandableSettingsGroup(
                    headerTitle = title,
                    modifier = Modifier.padding(bottom = 12.dp),
                    stateKey = "${model.currentIndex.intValue}|${item.key}",
                    initiallyExpanded = false,
                    headerTrailing = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    model.currentSelectedGroup.value = item.key
                                    model.editDialogShown.value = true
                                },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                    ) {
                        val avatars = item.value.distinct()
                        avatars.forEachIndexed { index, avatar ->
                            FavoriteVerticalSegmentRowItem(
                                name = avatar.name,
                                url = avatar.thumbnailUrl,
                                index = index,
                                count = avatars.size,
                                onClick = {
                                    if (avatar.name != "???") {
                                        navigator.parent?.parent?.push(AvatarScreen(avatar.id) {
                                            model.deleteDialogShown.value = true
                                            model.currentSelectedType.value = FavoriteType.FAVORITE_AVATAR
                                            model.currentSelectedId.value = avatar.id
                                        })
                                    } else {
                                        model.deleteDialogShown.value = true
                                        model.currentSelectedType.value = FavoriteType.FAVORITE_AVATAR
                                        model.currentSelectedId.value = avatar.id
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowFriends(
        model: FavoritesScreenModel,
        groupMetadata: Map<String, FavoriteManager.FavoriteGroupMetadata>,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val friendList = model.friendList.collectAsStateWithLifecycle()

        friendList.value.forEach { item ->
            if (item.value.isNotEmpty()) {
                val metadata = groupMetadata[item.key]
                val title = "${metadata?.displayName ?: metadata?.name ?: item.key} (${metadata?.size ?: 0}/${FavoriteManager.getMaximumFavoritesForType(FavoriteType.FAVORITE_FRIEND)})"

                ExpandableSettingsGroup(
                    headerTitle = title,
                    modifier = Modifier.padding(bottom = 12.dp),
                    stateKey = "${model.currentIndex.intValue}|${item.key}",
                    initiallyExpanded = false,
                    headerTrailing = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    model.currentSelectedIsFriend.value = true
                                    model.currentSelectedGroup.value = item.key
                                    model.editDialogShown.value = true
                                },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                    ) {
                        val friendRows = item.value.distinct().mapNotNull { fav ->
                            FriendManager.getFriend(fav.id)
                        }
                        friendRows.forEachIndexed { index, u ->
                            FavoriteVerticalSegmentRowItem(
                                name = u.displayName,
                                url = u.profilePicOverride.ifEmpty { u.currentAvatarImageUrl },
                                index = index,
                                count = friendRows.size,
                                onClick = {
                                    navigator.parent?.parent?.push(UserProfileScreen(u.id))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

