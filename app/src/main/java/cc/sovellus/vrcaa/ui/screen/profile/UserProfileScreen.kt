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

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.card.InstanceCard
import cc.sovellus.vrcaa.ui.components.dialog.FavoriteDialog
import cc.sovellus.vrcaa.ui.components.dialog.ImagePreviewDialog
import cc.sovellus.vrcaa.ui.components.dialog.InputDialog
import cc.sovellus.vrcaa.ui.components.settings.ExpandableSettingsGroup
import cc.sovellus.vrcaa.ui.components.settings.SettingsGroup
import cc.sovellus.vrcaa.ui.components.settings.SettingsItem
import cc.sovellus.vrcaa.extension.clickableIf
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.favorites.UserFavoritesScreen
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.notification.NotificationScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen
import cc.sovellus.vrcaa.ui.screen.worlds.WorldsScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class UserProfileScreen(
    private val userId: String,
    private val peek: Boolean = false
) : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {
        val model = rememberScreenModel { UserProfileScreenModel(userId) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is UserProfileScreenModel.UserProfileState.Loading -> LoadingIndicatorScreen().Content()
            is UserProfileScreenModel.UserProfileState.Failure -> HandleFailure()
            is UserProfileScreenModel.UserProfileState.Result -> Profile(
                result.profile, result.instance, model
            )
            else -> {}
        }
    }

    @Composable
    private fun HandleFailure() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        Toast.makeText(
            context,
            stringResource(R.string.profile_user_not_found_message),
            Toast.LENGTH_SHORT
        ).show()

        if (peek) {
            if (context is Activity) {
                context.finish()
            }
        } else {
            navigator.pop()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalGlideComposeApi::class)
    @Composable
    fun Profile(
        profile: LimitedUser?,
        instance: Instance?,
        model: UserProfileScreenModel
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var favoriteDialogShown by remember { mutableStateOf(false) }
        var badgeDialogTitle by remember { mutableStateOf<String?>(null) }
        var badgeDialogText by remember { mutableStateOf<String?>(null) }
        var noteDialogShown by remember { mutableStateOf(false) }
        var peekUrl by remember { mutableStateOf("") }
        var peekProfilePicture by remember { mutableStateOf(false) }
        var selectedTab by remember { mutableIntStateOf(0) }

        if (noteDialogShown) {
            InputDialog(
                onDismiss = { noteDialogShown = false },
                onConfirmation = {
                    noteDialogShown = false
                    model.updateNote()
                },
                title = stringResource(R.string.profile_user_note_dialog_title),
                text = model.note
            )
        }

        if (profile == null) {
            Toast.makeText(
                context, stringResource(R.string.profile_user_not_found_message), Toast.LENGTH_SHORT
            ).show()
            if (peek && context is Activity) context.finish()
            else navigator.pop()
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                if (badgeDialogText != null) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { badgeDialogText = null; badgeDialogTitle = null },
                        title = { Text(text = badgeDialogTitle ?: "Badge") },
                        text = { Text(text = badgeDialogText!!) },
                        confirmButton = {
                            androidx.compose.material3.TextButton(
                                onClick = { badgeDialogText = null; badgeDialogTitle = null }
                            ) {
                                Text(text = stringResource(android.R.string.ok))
                            }
                        }
                    )
                }

                if (favoriteDialogShown) {
                    FavoriteDialog(
                        type = IFavorites.FavoriteType.FAVORITE_FRIEND,
                        id = profile.id,
                        metadata = FavoriteManager.FavoriteMetadata(
                            profile.id, "", profile.displayName, ""
                        ),
                        onDismiss = { favoriteDialogShown = false },
                        onConfirmation = { favoriteDialogShown = false }
                    )
                }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                IconButton(onClick = {
                                    if (peek && context is Activity) context.finish()
                                    else navigator.pop()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            },
                            title = {
                                Text(
                                    text = profile.displayName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        )
                    },
                    content = { padding ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = padding.calculateTopPadding(),
                                    bottom = padding.calculateBottomPadding()
                                ),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Banner
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp)
                                ) {
                                    GlideImage(
                                        model = profile.profilePicOverride.ifEmpty {
                                            profile.currentAvatarImageUrl
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        loading = placeholder(R.drawable.image_placeholder),
                                        failure = placeholder(R.drawable.image_placeholder)
                                    )
                                }
                            }

                            // Profile picture overlay
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(y = (-60).dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    GlideImage(
                                        model = profile.userIcon.ifEmpty {
                                            profile.profilePicOverride.ifEmpty {
                                                profile.currentAvatarImageUrl
                                            }
                                        },
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .clickableIf(
                                                enabled = true,
                                                onClick = {
                                                    peekUrl = profile.currentAvatarImageUrl
                                                    peekProfilePicture = true
                                                }
                                            ),
                                        contentScale = ContentScale.Crop,
                                        loading = placeholder(R.drawable.image_placeholder),
                                        failure = placeholder(R.drawable.image_placeholder)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = profile.displayName,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    val trustRank = TrustHelper.getTrustRankFromTags(profile.tags)
                                    val trustRankColor = trustRank.toColor()
                                    val platform = profile.platform.ifEmpty { profile.lastPlatform }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = trustRankColor.copy(alpha = 0.12f)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 6.dp
                                                ),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Shield,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = trustRankColor
                                                )
                                                Text(
                                                    text = trustRank.toString(),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = trustRankColor
                                                )
                                            }
                                        }
                                        if (platform.isNotEmpty()) {
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceBright.copy(
                                                        alpha = 0.6f
                                                    )
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        horizontal = 12.dp,
                                                        vertical = 6.dp
                                                    ),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    when (platform.lowercase()) {
                                                        "standalonewindows" -> Icon(
                                                            imageVector = Icons.Default.Computer,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(16.dp),
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "android" -> Icon(
                                                            imageVector = Icons.Default.Android,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(16.dp),
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "ios" -> Icon(
                                                            imageVector = Icons.Default.PhoneIphone,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(16.dp),
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "web" -> Icon(
                                                            imageVector = Icons.Default.Web,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(16.dp),
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        else -> Icon(
                                                            imageVector = Icons.Default.Devices,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(16.dp),
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    Text(
                                                        text = platform.replaceFirstChar { it.uppercase() },
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (profile.pronouns.isNotEmpty()) {
                                            Text(
                                                text = profile.pronouns,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (profile.pronouns.isNotEmpty() && profile.ageVerificationStatus == "18+") {
                                            Text(
                                                text = " • ",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (profile.ageVerificationStatus == "18+") {
                                            Text(
                                                text = "18+",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    TabRow(
                                        selectedTabIndex = selectedTab,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        contentColor = MaterialTheme.colorScheme.primary,
                                        divider = {}
                                    ) {
                                        Tab(
                                            selected = selectedTab == 0,
                                            onClick = { selectedTab = 0 },
                                            text = { Text(stringResource(R.string.tabs_label_profile)) }
                                        )
                                        Tab(
                                            selected = selectedTab == 1,
                                            onClick = { selectedTab = 1 },
                                            text = { Text(stringResource(R.string.tabs_label_options)) }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            when (selectedTab) {
                                0 -> {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        ) {
                                            // Badges
                                            val visibleBadges = profile.badges.filter { !it.hidden }
                                            if (visibleBadges.isNotEmpty()) {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    LazyRow(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                                                    ) {
                                                        items(
                                                            items = visibleBadges,
                                                            key = { it.badgeId }
                                                        ) { badge ->
                                                            Card(
                                                                modifier = Modifier
                                                                    .size(48.dp)
                                                                    .clickableIf(
                                                                        enabled = true,
                                                                        onClick = {
                                                                            badgeDialogTitle = badge.badgeName
                                                                            badgeDialogText = badge.badgeDescription
                                                                        }
                                                                    ),
                                                                shape = RoundedCornerShape(12.dp),
                                                                colors = CardDefaults.cardColors(
                                                                    containerColor = if (badge.showcased)
                                                                        MaterialTheme.colorScheme.surfaceContainer
                                                                    else
                                                                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f)
                                                                )
                                                            ) {
                                                                GlideImage(
                                                                    model = badge.badgeImageUrl,
                                                                    contentDescription = badge.badgeName,
                                                                    modifier = Modifier.fillMaxSize(),
                                                                    contentScale = ContentScale.Crop,
                                                                    loading = placeholder(R.drawable.image_placeholder),
                                                                    failure = placeholder(R.drawable.image_placeholder),
                                                                    alpha = if (badge.showcased) 1.0f else 0.85f
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(16.dp))
                                            }

                                            // Instance card
                                            if (instance != null) {
                                                InstanceCard(
                                                    profile = profile,
                                                    instance = instance,
                                                    disabled = false
                                                ) {
                                                    navigator.push(WorldScreen(instance.worldId))
                                                }
                                                Spacer(modifier = Modifier.height(24.dp))
                                            }

                                            // Account info
                                            val accountItems = buildList {
                                                if (profile.note.isNotEmpty()) {
                                                    add(
                                                        SettingsItem(
                                                            title = profile.note,
                                                            description = stringResource(R.string.profile_label_note),
                                                            icon = Icons.AutoMirrored.Filled.Notes,
                                                            onClick = { noteDialogShown = true }
                                                        )
                                                    )
                                                }
                                                if (profile.bio.isNotEmpty()) {
                                                    add(
                                                        SettingsItem(
                                                            title = profile.bio,
                                                            description = stringResource(R.string.profile_label_biography),
                                                            icon = Icons.Default.Folder,
                                                            onClick = { }
                                                        )
                                                    )
                                                }
                                                if (profile.lastActivity.isNotEmpty()) {
                                                    val userTimeZone = TimeZone.getDefault().toZoneId()
                                                    val formatter = DateTimeFormatter.ofLocalizedDateTime(
                                                        java.time.format.FormatStyle.SHORT
                                                    ).withLocale(Locale.getDefault())
                                                    val lastActivity = ZonedDateTime.parse(profile.lastActivity)
                                                        .withZoneSameInstant(userTimeZone)
                                                        .format(formatter)
                                                    add(
                                                        SettingsItem(
                                                            title = lastActivity,
                                                            description = stringResource(R.string.profile_label_last_activity),
                                                            icon = Icons.Default.History,
                                                            onClick = { }
                                                        )
                                                    )
                                                }
                                                add(
                                                    SettingsItem(
                                                        title = profile.dateJoined,
                                                        description = stringResource(R.string.profile_label_date_joined),
                                                        icon = Icons.Default.Cake,
                                                        onClick = { }
                                                    )
                                                )
                                            }
                                            if (accountItems.isNotEmpty()) {
                                                ExpandableSettingsGroup(
                                                    headerTitle = stringResource(R.string.profile_label_account_info),
                                                    items = accountItems,
                                                    initiallyExpanded = true
                                                )
                                            }

                                            // Social links
                                            if (profile.bioLinks.isNotEmpty() && profile.bioLinks.any { it.isNotEmpty() }) {
                                                Spacer(modifier = Modifier.height(24.dp))
                                                val socialLinksItems = profile.bioLinks
                                                    .filter { it.isNotEmpty() }
                                                    .map { link ->
                                                        SettingsItem(
                                                            title = link,
                                                            description = null,
                                                            icon = Icons.Default.Link,
                                                            onClick = {
                                                                try {
                                                                    val intent = Intent(
                                                                        Intent.ACTION_VIEW,
                                                                        Uri.parse(link)
                                                                    )
                                                                    context.startActivity(intent)
                                                                } catch (e: Exception) {
                                                                    Toast.makeText(
                                                                        context,
                                                                        context.getString(R.string.profile_failed_to_open_link),
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                            }
                                                        )
                                                    }
                                                ExpandableSettingsGroup(
                                                    headerTitle = stringResource(R.string.profile_edit_dialog_title_bio_links),
                                                    items = socialLinksItems,
                                                    initiallyExpanded = false
                                                )
                                            }

                                            // Known languages
                                            val languageTags = profile.tags.filter { it.contains("language_") }
                                            if (languageTags.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(24.dp))
                                                val languagesItems = languageTags.map { tag ->
                                                    val languageCode = tag.substring("language_".length)
                                                    val languageName = when (languageCode.lowercase()) {
                                                        "eng" -> stringResource(R.string.language_english)
                                                        "kor" -> stringResource(R.string.language_korean)
                                                        "rus" -> stringResource(R.string.language_russian)
                                                        "spa" -> stringResource(R.string.language_spanish)
                                                        "por" -> stringResource(R.string.language_portuguese)
                                                        "zho" -> stringResource(R.string.language_chinese)
                                                        "deu" -> stringResource(R.string.language_german)
                                                        "jpn" -> stringResource(R.string.language_japanese)
                                                        "fra" -> stringResource(R.string.language_french)
                                                        "swe" -> stringResource(R.string.language_swedish)
                                                        "nld" -> stringResource(R.string.language_dutch)
                                                        "pol" -> stringResource(R.string.language_polish)
                                                        "dan" -> stringResource(R.string.language_danish)
                                                        "nor" -> stringResource(R.string.language_norwegian)
                                                        "ita" -> stringResource(R.string.language_italian)
                                                        "tha" -> stringResource(R.string.language_thai)
                                                        "fin" -> stringResource(R.string.language_finnish)
                                                        "hun" -> stringResource(R.string.language_hungarian)
                                                        "ces" -> stringResource(R.string.language_czech)
                                                        "tur" -> stringResource(R.string.language_turkish)
                                                        "ara" -> stringResource(R.string.language_arabic)
                                                        else -> languageCode.uppercase()
                                                    }
                                                    SettingsItem(
                                                        title = languageName,
                                                        description = null,
                                                        icon = Icons.Default.Translate,
                                                        onClick = { }
                                                    )
                                                }
                                                ExpandableSettingsGroup(
                                                    headerTitle = stringResource(R.string.profile_label_known_languages),
                                                    items = languagesItems,
                                                    initiallyExpanded = false
                                                )
                                            }
                                        }
                                    }
                                }
                                1 -> {
                                    item {
                                        OptionsContent(
                                            profile = profile,
                                            instance = instance,
                                            model = model,
                                            navigator = navigator,
                                            context = context,
                                            onFavoriteDialog = { favoriteDialogShown = true },
                                            onNoteDialog = { noteDialogShown = true }
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                )

                if (peekProfilePicture) {
                    ImagePreviewDialog(
                        url = peekUrl,
                        name = "${profile.displayName}-${LocalDateTime.now()}",
                        onDismiss = { peekProfilePicture = false }
                    )
                }
            }
        }
    }

    @Composable
    private fun OptionsContent(
        profile: LimitedUser,
        instance: Instance?,
        model: UserProfileScreenModel,
        navigator: cafe.adriel.voyager.navigator.Navigator,
        context: Context,
        onFavoriteDialog: () -> Unit,
        onNoteDialog: () -> Unit
    ) {
        val options = mutableListOf<Pair<String, ImageVector>>()
        val actions = mutableListOf<() -> Unit>()

        model.status?.let { status ->
            when {
                status.incomingRequest -> {
                    options.add(stringResource(R.string.user_overlay_friend_accept) to Icons.Default.Person)
                    actions.add {
                        model.handleFriendStatus { _, result ->
                            Toast.makeText(context, if (result) context.getString(R.string.friend_toast_friend_request_accepted).format(profile.displayName) else context.getString(R.string.friend_toast_friend_request_accept_failed).format(profile.displayName), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                status.outgoingRequest -> {
                    options.add(stringResource(R.string.user_overlay_friend_cancel) to Icons.Default.Person)
                    actions.add {
                        model.handleFriendStatus { _, result ->
                            Toast.makeText(context, if (result) context.getString(R.string.friend_toast_friend_request_cancelled).format(profile.displayName) else context.getString(R.string.friend_toast_friend_request_cancel_failed).format(profile.displayName), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                status.isFriend -> {
                    options.add(stringResource(R.string.user_overlay_friend_remove) to Icons.Default.Person)
                    actions.add {
                        model.handleFriendStatus { _, result ->
                            Toast.makeText(context, if (result) context.getString(R.string.friend_toast_friend_removed).format(profile.displayName) else context.getString(R.string.friend_toast_friend_remove_failed).format(profile.displayName), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> {
                    options.add(stringResource(R.string.user_overlay_friend_add) to Icons.Default.Person)
                    actions.add {
                        model.handleFriendStatus { _, result ->
                            Toast.makeText(context, if (result) context.getString(R.string.friend_toast_friend_requested).format(profile.displayName) else context.getString(R.string.friend_toast_friend_request_failed).format(profile.displayName), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        if (profile.isFriend) {
            options.add(stringResource(R.string.profile_user_dropdown_manage_notifications) to Icons.Default.NotificationsActive)
            actions.add { navigator.push(NotificationScreen(profile.id, profile.displayName)) }

            if (FavoriteManager.isFavorite("friend", profile.id)) {
                options.add(stringResource(R.string.favorite_label_remove) to Icons.Default.Star)
                actions.add {
                    model.removeFavorite { result ->
                        Toast.makeText(context, if (result) context.getString(R.string.favorite_toast_favorite_removed).format(profile.displayName) else context.getString(R.string.favorite_toast_favorite_removed_failed).format(profile.displayName), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                options.add(stringResource(R.string.favorite_label_add) to Icons.Default.Star)
                actions.add(onFavoriteDialog)
            }
        }

        if (instance != null) {
            options.add(stringResource(R.string.user_overlay_invite) to Icons.Default.Navigation)
            actions.add { model.inviteToFriend(instance.location) }
        }

        options.add(stringResource(R.string.user_overlay_note) to Icons.AutoMirrored.Filled.Notes)
        actions.add(onNoteDialog)

        options.add(stringResource(R.string.user_overlay_find_avatar) to Icons.Default.Person)
        actions.add {
            model.findAvatar { avatarId ->
                if (profile.profilePicOverride.isNotEmpty()) {
                    Toast.makeText(context, context.getString(R.string.profile_user_avatar_unreachable), Toast.LENGTH_SHORT).show()
                    return@findAvatar Unit
                }
                if (avatarId == null) {
                    Toast.makeText(context, context.getString(R.string.profile_user_avatar_private), Toast.LENGTH_SHORT).show()
                } else {
                    navigator.push(AvatarScreen(avatarId))
                }
            }
        }

        options.add(stringResource(R.string.user_overlay_worlds) to Icons.Default.Cabin)
        actions.add { navigator.push(WorldsScreen(profile.displayName, profile.id, false)) }

        options.add(stringResource(R.string.user_overlay_groups) to Icons.Default.Group)
        actions.add { navigator.push(UserGroupsScreen(profile.displayName, profile.id)) }

        options.add(stringResource(R.string.user_overlay_favorites) to Icons.Default.Star)
        actions.add { navigator.push(UserFavoritesScreen(profile.id)) }

        options.add(stringResource(R.string.copy_id_label) to Icons.Default.ContentCopy)
        actions.add {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText(null, profile.id))
            Toast.makeText(context, context.getString(R.string.copied_toast).format(profile.displayName), Toast.LENGTH_SHORT).show()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val settingsItems = options.mapIndexed { index, (label, icon) ->
                SettingsItem(
                    title = label,
                    description = null,
                    icon = icon,
                    onClick = actions[index]
                )
            }
            SettingsGroup(items = settingsItems)
        }
    }
}
