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

package cc.sovellus.vrcaa.ui.screen.world

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.extension.clickableIf
import cc.sovellus.vrcaa.helper.TimeHelper
import cc.sovellus.vrcaa.manager.DatabaseManager
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.card.WorldCard
import cc.sovellus.vrcaa.ui.components.layout.NavigationBarBottomInset
import cc.sovellus.vrcaa.ui.components.dialog.FavoriteDialog
import cc.sovellus.vrcaa.ui.components.dialog.GenericDialog
import cc.sovellus.vrcaa.ui.components.layout.InstanceItem
import cc.sovellus.vrcaa.ui.components.misc.BadgesFromTags
import cc.sovellus.vrcaa.ui.components.settings.SectionHeader
import cc.sovellus.vrcaa.ui.components.settings.SettingsGroup
import cc.sovellus.vrcaa.ui.components.settings.SettingsItem
import cc.sovellus.vrcaa.ui.components.controls.connectedButtonGroupToggleColors
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import java.text.NumberFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone
import cc.sovellus.vrcaa.ui.theme.appBackground

class WorldScreen(
    private val worldId: String,
    private val peek: Boolean = false,
    @Transient
    private val onInvalidWorld: (() -> Unit)? = null
) : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {
        val model = rememberScreenModel { WorldScreenModel(worldId) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is WorldScreenModel.WorldInfoState.Loading -> LoadingIndicatorScreen().Content()
            is WorldScreenModel.WorldInfoState.Failure -> HandleFailure()
            is WorldScreenModel.WorldInfoState.Result -> MultiChoiceHandler(model, result.world, result.instances)
            else -> {}
        }
    }

    @Composable
    private fun HandleFailure() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        if (peek) {
            if (context is Activity) {
                Toast.makeText(
                    context,
                    stringResource(R.string.world_toast_not_found),
                    Toast.LENGTH_SHORT
                ).show()
                context.finish()
            }
        } else {
            navigator.pop()
            val once = remember(Unit) { mutableStateOf(false) }
            if (!once.value) {
                Toast.makeText(
                    context,
                    stringResource(R.string.world_toast_not_found),
                    Toast.LENGTH_SHORT
                ).show()
                navigator.pop()
                once.value = true
            }
        }

        onInvalidWorld?.invoke()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MultiChoiceHandler(
        model: WorldScreenModel,
        world: World?,
        instances: List<Pair<String, WorldScreenModel.InstanceWithFriends>>,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var isMenuExpanded by remember { mutableStateOf(false) }
        var favoriteDialogShown by remember { mutableStateOf(false) }
        val groupMetadata by model.groupMetadata.collectAsState()

        if (world == null) {
            Toast.makeText(
                context,
                stringResource(R.string.world_toast_not_found),
                Toast.LENGTH_SHORT
            ).show()
            navigator.pop()
        } else {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.appBackground,
                contentWindowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                ),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        navigationIcon = {
                            IconButton(onClick = {
                                if (peek) {
                                    if (context is Activity) {
                                        context.finish()
                                    }
                                } else {
                                    navigator.pop()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },

                        title = {
                            Text(
                                text = world.name,
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start
                            )
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
                                                navigator.push(
                                                    UserProfileScreen(world.authorId)
                                                )
                                                isMenuExpanded = false
                                            },
                                            text = { Text(stringResource(R.string.group_page_dropdown_view_author)) }
                                        )
                                        if (FavoriteManager.isFavorite("world", world.id)) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    model.removeFavorite(world)
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.favorite_label_remove)) }
                                            )
                                        } else {
                                            DropdownMenuItem(
                                                onClick = {
                                                    favoriteDialogShown = true
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.favorite_label_add)) }
                                            )
                                        }
                                        DropdownMenuItem(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText(null, world.id)
                                                clipboard.setPrimaryClip(clip)

                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.copied_toast).format(world.name),
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                isMenuExpanded = false
                                            },
                                            text = { Text(stringResource(R.string.copy_id_label)) }
                                        )
                                    }
                                }
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    if (favoriteDialogShown) {
                        FavoriteDialog(
                            type = IFavorites.FavoriteType.FAVORITE_WORLD,
                            id = world.id,
                            metadata = FavoriteManager.FavoriteMetadata(
                                world.id,
                                "",
                                world.name,
                                world.thumbnailImageUrl
                            ),
                            groupMetadata = groupMetadata,
                            maximumFavorites = FavoriteManager.getMaximumFavoritesForType(IFavorites.FavoriteType.FAVORITE_WORLD),
                            onDismiss = { favoriteDialogShown = false },
                            onConfirmation = { favoriteDialogShown = false }
                        )
                    }

                    val options = stringArrayResource(R.array.world_selection_options)
                    val icons = listOf(Icons.Filled.Cabin, Icons.Filled.LocationOn)

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(
                                top = paddingValues.calculateTopPadding(),
                                bottom = NavigationBarBottomInset
                            ),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                        ) {
                            val modifiers = List(options.size) { Modifier.weight(1f) }
                            options.forEachIndexed { index, label ->
                                val selected = index == model.currentTabIndex.intValue
                                ToggleButton(
                                    checked = selected,
                                    onCheckedChange = { model.currentTabIndex.intValue = index },
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
                                    Text(text = label, softWrap = true, maxLines = 1)
                                }
                            }
                        }

                        when (model.currentTabIndex.intValue) {
                            0 -> ShowInfo(world)
                            1 -> ShowInstances(instances, model)
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun ShowInfo(world: World) {
        val nf = NumberFormat.getInstance()
        val userTimeZone = TimeZone.getDefault().toZoneId()
        val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
            .withLocale(Locale.getDefault())

        val createdAtFormatted = ZonedDateTime.parse(world.createdAt).withZoneSameInstant(userTimeZone).format(dateFormatter)
        val updatedAtFormatted = ZonedDateTime.parse(world.updatedAt).withZoneSameInstant(userTimeZone).format(dateFormatter)

        val occupancyRate = world.visits.takeIf { it != 0 }?.let {
            String.format(Locale.ENGLISH, "%.1f", world.favorites.toFloat() / it.toFloat() * 100)
        } ?: "0.0"

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                WorldCard(world, onImageClick = {}, clickable = true)
            }

            if (!world.description.isNullOrEmpty()) {
                item {
                    SectionHeader(title = stringResource(R.string.world_label_description))
                    SettingsGroup(
                        items = listOf(
                            SettingsItem(
                                title = world.description,
                                onClick = {}
                            )
                        )
                    )
                }
            }

            item {
                SectionHeader(title = stringResource(R.string.world_section_statistics))
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = stringResource(R.string.world_title_occupants),
                            description = stringResource(
                                R.string.world_occupants_format,
                                nf.format(world.publicOccupants),
                                nf.format(world.privateOccupants),
                                nf.format(world.occupants)
                            ),
                            icon = Icons.Filled.Group,
                            onClick = {}
                        ),
                        SettingsItem(
                            title = stringResource(R.string.world_title_favorites),
                            description = stringResource(
                                R.string.world_favorites_format,
                                nf.format(world.favorites),
                                occupancyRate
                            ),
                            icon = Icons.Filled.Star,
                            onClick = {}
                        ),
                        SettingsItem(
                            title = stringResource(R.string.world_title_visits),
                            description = nf.format(world.visits),
                            icon = Icons.Filled.Visibility,
                            onClick = {}
                        ),
                        SettingsItem(
                            title = stringResource(R.string.world_title_capacity),
                            description = stringResource(
                                R.string.world_capacity_format,
                                world.recommendedCapacity,
                                world.capacity
                            ),
                            icon = Icons.Filled.Public,
                            onClick = {}
                        )
                    )
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.world_section_activity))
                SettingsGroup(
                    items = buildList {
                        if (world.heat > 0) {
                            add(
                                SettingsItem(
                                    title = stringResource(R.string.world_title_heat),
                                    description = world.heat.toString(),
                                    icon = Icons.Filled.LocalFireDepartment,
                                    onClick = {}
                                )
                            )
                        }
                        if (world.popularity > 0) {
                            add(
                                SettingsItem(
                                    title = stringResource(R.string.world_title_popularity),
                                    description = world.popularity.toString(),
                                    icon = Icons.Filled.Favorite,
                                    onClick = {}
                                )
                            )
                        }
                    }
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.world_section_dates))
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = stringResource(R.string.world_title_created_at),
                            description = createdAtFormatted,
                            icon = Icons.Filled.CalendarMonth,
                            onClick = {}
                        ),
                        SettingsItem(
                            title = stringResource(R.string.world_title_updated_at),
                            description = updatedAtFormatted,
                            icon = Icons.Filled.Update,
                            onClick = {}
                        ),
                        SettingsItem(
                            title = stringResource(R.string.world_title_publication_date),
                            description = updatedAtFormatted,
                            icon = Icons.Filled.Public,
                            onClick = {}
                        )
                    )
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.world_label_tags))
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = "",
                            onClick = {},
                            trailingContent = {
                                BadgesFromTags(
                                    tags = world.tags,
                                    tagPropertyName = "author_tag",
                                    localizationResourceInt = R.string.world_text_no_tags
                                )
                            }
                        )
                    )
                )
            }
        }
    }

    @Composable
    fun ShowInstances(
        instances: List<Pair<String, WorldScreenModel.InstanceWithFriends>>,
        model: WorldScreenModel
    ) {
        val dialogState = remember { mutableStateOf(false) }

        if (dialogState.value) {
            GenericDialog(
                onDismiss = { dialogState.value = false },
                onConfirmation = {
                    dialogState.value = false
                    model.selfInvite()
                },
                title = stringResource(R.string.world_instance_invite_dialog_title),
                description = stringResource(R.string.world_instance_invite_dialog_description)
            )
        }

        if (instances.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(R.string.world_instance_no_public_instances_message))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                items(
                    instances.distinctBy { it.second.instance?.world?.id }
                ) { (_, item) ->
                    val inst = item.instance ?: return@items
                    InstanceItem(
                        instance = inst,
                        creator = item.creator,
                        friends = item.friends.toList(),
                        onClick = {
                            dialogState.value = true
                            model.selectedInstanceId.value = inst.id
                        }
                    )
                }
            }
        }
    }
}

