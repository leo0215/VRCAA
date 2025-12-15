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

package cc.sovellus.vrcaa.ui.screen.friends

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.layout.FriendItem
import cc.sovellus.vrcaa.ui.components.layout.FriendItemMaterial3
import cc.sovellus.vrcaa.ui.components.controls.SelectionChipsRow
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreenModel.FriendsState
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreenModel.GroupedFriend
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen

class FriendsScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { FriendsScreenModel() }

        BackHandler(
            enabled = model.currentIndex.intValue != 0,
            onBack = {
                model.currentIndex.intValue = 0
            }
        )

        val state by model.state.collectAsStateWithLifecycle()

        when (state) {
            is FriendsState.Loading -> LoadingIndicatorScreen().Content()
            is FriendsState.Result -> ShowScreen(model)
            else -> {}
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    fun ShowScreen(model: FriendsScreenModel)
    {
        var isRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val pullToRefreshState = rememberPullToRefreshState()
        val searchQuery by model.searchQuery.collectAsStateWithLifecycle()

        val options = stringArrayResource(R.array.friend_selection_options)
        val icons = listOf(Icons.Filled.Star, Icons.Filled.Person, Icons.Filled.Web, Icons.Filled.PersonOff)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelectionChipsRow(
                options = options.toList(),
                selectedIndex = model.currentIndex.intValue,
                onSelect = { model.currentIndex.intValue = it },
                icons = icons,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(32.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { model.updateSearchQuery(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                singleLine = true
                            )
                        }
                        
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { model.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    scope.launch {
                        isRefreshing = true
                        model.refreshCache()
                        isRefreshing = false
                    }
                },
                state = pullToRefreshState,
                modifier = Modifier.weight(1f)
            ) {
                when (model.currentIndex.intValue) {
                    0 -> ShowFriendsFavorite(model)
                    1 -> ShowFriends(model)
                    2 -> ShowFriendsOnWebsite(model)
                    3 -> ShowFriendsOffline(model)
                }
            }
        }
    }

    @Composable
    fun ShowFriendsFavorite(model: FriendsScreenModel) {
        val groupedFriends = model.groupedFavoriteFriends.collectAsStateWithLifecycle()

        if (groupedFriends.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            val navigator = LocalNavigator.currentOrThrow
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                state = rememberLazyListState()
            ) {
                items(
                    items = groupedFriends.value,
                    key = { it.friend.id }
                ) { groupedFriend ->
                    FriendItemMaterial3(
                        friend = groupedFriend.friend,
                        showLetter = groupedFriend.letter.isNotEmpty(),
                        letter = groupedFriend.letter,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(groupedFriend.friend.id)) }
                    )
                }
            }
        }
    }

    @Composable
    fun ShowFriendsOnWebsite(model: FriendsScreenModel) {
        val groupedFriends = model.groupedFriendsOnWebsite.collectAsStateWithLifecycle()

        if (groupedFriends.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            val navigator = LocalNavigator.currentOrThrow
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                state = rememberLazyListState()
            ) {
                items(
                    items = groupedFriends.value,
                    key = { it.friend.id }
                ) { groupedFriend ->
                    FriendItemMaterial3(
                        friend = groupedFriend.friend,
                        showLetter = groupedFriend.letter.isNotEmpty(),
                        letter = groupedFriend.letter,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(groupedFriend.friend.id)) }
                    )
                }
            }
        }
    }

    @Composable
    fun ShowFriends(model: FriendsScreenModel) {
        val groupedFriends = model.groupedFriends.collectAsStateWithLifecycle()

        if (groupedFriends.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            val navigator = LocalNavigator.currentOrThrow
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                state = rememberLazyListState()
            ) {
                items(
                    items = groupedFriends.value,
                    key = { it.friend.id }
                ) { groupedFriend ->
                    FriendItemMaterial3(
                        friend = groupedFriend.friend,
                        showLetter = groupedFriend.letter.isNotEmpty(),
                        letter = groupedFriend.letter,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(groupedFriend.friend.id)) }
                    )
                }
            }
        }
    }

    @Composable
    fun ShowFriendsOffline(model: FriendsScreenModel) {
        val groupedFriends = model.groupedOfflineFriends.collectAsStateWithLifecycle()

        if (groupedFriends.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            val navigator = LocalNavigator.currentOrThrow
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                state = rememberLazyListState()
            ) {
                items(
                    items = groupedFriends.value,
                    key = { it.friend.id }
                ) { groupedFriend ->
                    FriendItemMaterial3(
                        friend = groupedFriend.friend,
                        showLetter = groupedFriend.letter.isNotEmpty(),
                        letter = groupedFriend.letter,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(groupedFriend.friend.id)) }
                    )
                }
            }
        }
    }
}