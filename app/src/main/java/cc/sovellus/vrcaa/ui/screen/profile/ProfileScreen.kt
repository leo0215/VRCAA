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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import cc.sovellus.vrcaa.extension.anonymousMode
import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.ui.components.card.ProfileCard
import cc.sovellus.vrcaa.ui.components.controls.QuickActionsRow
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class ProfileScreen : Screen {

    override val key = uniqueScreenKey
    
    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalGlideComposeApi::class)
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
    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalGlideComposeApi::class)
    @Composable
    private fun RenderProfile(profile: User) {
        LazyColumn(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                profile.let {
                    ProfileCard(
                        thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarThumbnailImageUrl },
                        iconUrl = it.userIcon.ifEmpty { it.currentAvatarThumbnailImageUrl },
                        displayName = it.displayName,
                        statusDescription = it.statusDescription.ifEmpty {  StatusHelper.getStatusFromString(it.status).toString() },
                        trustRankColor = TrustHelper.getTrustRankFromTags(it.tags).toColor(),
                        statusColor = StatusHelper.getStatusFromString(it.status).toColor(),
                        tags = profile.tags,
                        badges = profile.badges,
                        pronouns = profile.pronouns,
                        ageVerificationStatus = profile.ageVerificationStatus
                    ) { }
                }
            }
            preferences.registerOnSharedPreferenceChangeListener(listener)
            onDispose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
        }
        
        Box(modifier = Modifier.fillMaxSize()) {
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
                            var badgeDialogText by remember { mutableStateOf<String?>(null) }
                            var badgeDialogTitle by remember { mutableStateOf<String?>(null) }

                            if (badgeDialogText != null) {
                                androidx.compose.material3.AlertDialog(
                                    onDismissRequest = { badgeDialogText = null },
                                    title = { Text(text = badgeDialogTitle ?: "Badge") },
                                    text = { Text(text = badgeDialogText!!) },
                                    confirmButton = {
                                        androidx.compose.material3.TextButton(onClick = { badgeDialogText = null }) {
                                            Text(text = stringResource(android.R.string.ok))
                                        }
                                    }
                                )
                            }

                            ProfileCard(
                                thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl },
                                iconUrl = it.userIcon.ifEmpty { it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl } },
                                displayName = if (anonymousModeEnabled) "You" else it.displayName,
                                statusDescription = it.statusDescription.ifEmpty {  StatusHelper.getStatusFromString(it.status).toString() },
                                trustRankColor = TrustHelper.getTrustRankFromTags(it.tags).toColor(),
                                statusColor = StatusHelper.getStatusFromString(it.status).toColor(),
                                tags = profile.tags,
                                badges = profile.badges,
                                pronouns = profile.pronouns,
                                ageVerificationStatus = profile.ageVerificationStatus,
                                onPeek = { },
                                onBadgeClick = { badge ->
                                    badgeDialogTitle = badge.badgeName
                                    badgeDialogText = badge.badgeDescription.ifEmpty { badge.badgeName }
                                }
                            )
                        }
                    }

                    item { QuickActionsRow(modifier = Modifier.fillMaxWidth()) }

                    item {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Card(
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

                            val myGroups by model.myGroups.collectAsState()
                            if (myGroups.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .defaultMinSize(minHeight = 60.dp)
                                        .widthIn(Dp.Unspecified, 520.dp),
                                ) {
                                    SubHeader(title = "Your groups")
                                    LazyRow(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(myGroups) { g ->
                                            Surface(
                                                shape = RoundedCornerShape(24.dp),
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHigh,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(24.dp))
                                                    .clickable { navigator.push(GroupScreen(g.groupId)) }
                                            ) {
                                                androidx.compose.foundation.layout.Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    com.bumptech.glide.integration.compose.GlideImage(
                                                        model = g.iconUrl,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        loading = com.bumptech.glide.integration.compose.placeholder(cc.sovellus.vrcaa.R.drawable.image_placeholder),
                                                        failure = com.bumptech.glide.integration.compose.placeholder(cc.sovellus.vrcaa.R.drawable.image_placeholder)
                                                    )
                                                    Text(
                                                        text = g.name,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}