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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import cc.sovellus.vrcaa.ui.components.controls.SelectionChipsRow
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreenModel.FriendsState
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
    fun ShowScreen(model: FriendsScreenModel)
    {
        var isRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val pullToRefreshState = rememberPullToRefreshState()

        val options = stringArrayResource(R.array.friend_selection_options)
        val icons = listOf(Icons.Filled.Star, Icons.Filled.Person, Icons.Filled.Web, Icons.Filled.PersonOff)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
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

        val favoriteFriends = model.favoriteFriends.collectAsStateWithLifecycle()
        val favoriteFriendsInInstances = model.favoriteFriendsInInstances.collectAsStateWithLifecycle()
        val favoriteFriendsOffline = model.favoriteFriendsOffline.collectAsStateWithLifecycle()

        if (favoriteFriends.value.isEmpty() && favoriteFriendsInInstances.value.isEmpty() && favoriteFriendsOffline.value.isEmpty()) {
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
                    .fillMaxHeight()
                    .padding(1.dp),
                state = rememberLazyListState()
            ) {
                items(favoriteFriendsInInstances.value) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }

                if (favoriteFriendsInInstances.value.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            color = Color.Gray,
                            thickness = 0.5.dp
                        )
                    }

                }

                items(
                    favoriteFriends.value) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }

                if (favoriteFriendsOffline.value.isNotEmpty() && favoriteFriends.value.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            color = Color.Gray,
                            thickness = 0.5.dp
                        )
                    }
                }

                items(favoriteFriendsOffline.value) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }
            }
        }
    }

    @Composable
    fun ShowFriendsOnWebsite(model: FriendsScreenModel) {

        val friendsOnWebsite = model.friendsOnWebsite.collectAsStateWithLifecycle()

        if (friendsOnWebsite.value.isEmpty()) {
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
                    .fillMaxHeight()
                    .padding(1.dp),
                state = rememberLazyListState()
            ) {
                items(friendsOnWebsite.value) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }
            }
        }
    }

    @Composable
    fun ShowFriends(model: FriendsScreenModel) {

        val friendsOnline = model.friendsOnline.collectAsStateWithLifecycle()
        val friendsInInstances = model.friendsInInstances.collectAsStateWithLifecycle()

        if (friendsOnline.value.isEmpty() && friendsInInstances.value.isEmpty()) {
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
                    .fillMaxHeight()
                    .padding(1.dp),
                state = rememberLazyListState()
            ) {
                if (friendsInInstances.value.isNotEmpty())
                {
                    items(friendsInInstances.value) { friend ->
                        FriendItem(
                            friend = friend,
                            callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                    item {
                        HorizontalDivider(
                            color = Color.Gray,
                            thickness = 0.5.dp
                        )
                    }
                    items(friendsOnline.value) { friend ->
                        FriendItem(
                            friend = friend,
                            callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                } else {
                    items(friendsOnline.value) { friend ->
                        FriendItem(
                            friend = friend,
                            callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ShowFriendsOffline(model: FriendsScreenModel) {

        val offlineFriends = model.offlineFriends.collectAsStateWithLifecycle()

        if (offlineFriends.value.isEmpty()) {
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
                    .fillMaxHeight()
                    .padding(1.dp),
                state = rememberLazyListState()
            ) {
                items(offlineFriends.value) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }
            }
        }
    }
}