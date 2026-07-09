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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.ui.components.layout.FavoriteHorizontalRow
import cc.sovellus.vrcaa.ui.components.layout.NavigationBarBottomInset
import cc.sovellus.vrcaa.ui.components.layout.RowItem
import cc.sovellus.vrcaa.ui.screen.avatar.UserAvatarScreen
import cc.sovellus.vrcaa.ui.components.controls.connectedButtonGroupToggleColors
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen
import cc.sovellus.vrcaa.ui.theme.appBackground

class UserFavoritesScreen(
    private val userId: String
) : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {
        val model = rememberScreenModel { UserFavoritesScreenModel(userId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is UserFavoritesScreenModel.UserFavoriteState.Loading -> LoadingIndicatorScreen().Content()
            is UserFavoritesScreenModel.UserFavoriteState.Result -> ShowScreen(model, result.worlds, result.avatars)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowScreen(
        model: UserFavoritesScreenModel,
        worlds: Map<String, List<World>>,
        avatars: Map<String, List<Avatar>>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            containerColor = MaterialTheme.colorScheme.appBackground,
            contentWindowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Top
            ),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    title = {
                        Text(text = stringResource(R.string.favorite_page_title))
                    }
                )
            }
        ) { innerPadding ->
            val options = stringArrayResource(R.array.user_favorites_selection_options)
            val icons = listOf(Icons.Filled.Cabin, Icons.Filled.Person)

            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding(), bottom = NavigationBarBottomInset)
                    .fillMaxSize(),
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
                            0 -> ShowWorlds(worlds)
                            1 -> ShowAvatars(avatars)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowWorlds(
        list: Map<String, List<World>>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        if (list.isNotEmpty()) {
            list.forEach { item ->
                if (item.value.isNotEmpty()) {
                    FavoriteHorizontalRow(
                        title = item.key,
                        allowEdit = false,
                        onEdit = {}
                    ) {
                        items(item.value) {
                            RowItem(name = it.name, url = it.thumbnailImageUrl) {
                                if (it.name != "???") {
                                    navigator.push(WorldScreen(it.id))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        }
    }

    @Composable
    fun ShowAvatars(
        list: Map<String, List<Avatar>>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        if (list.isNotEmpty()) {
            list.forEach { item ->
                if (item.value.isNotEmpty()) {
                    FavoriteHorizontalRow(
                        title = item.key,
                        allowEdit = false,
                        onEdit = {}
                    ) {
                        items(item.value) {
                            RowItem(name = it.name, url = it.thumbnailImageUrl) {
                                if (it.name != "???") {
                                    navigator.push(UserAvatarScreen(it))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        }
    }
}

