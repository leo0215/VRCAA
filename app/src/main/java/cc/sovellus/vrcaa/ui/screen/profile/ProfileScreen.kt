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

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.ui.unit.sp
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
import cc.sovellus.vrcaa.api.vrchat.http.models.Badge as UserBadge
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.extension.clickableIf
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.ui.components.card.ProfileCard
import cc.sovellus.vrcaa.ui.components.controls.QuickActionsRow
import cc.sovellus.vrcaa.ui.components.input.ComboInput
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.components.settings.SettingsGroup
import cc.sovellus.vrcaa.ui.components.settings.SettingsItem
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

    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalGlideComposeApi::class)
    @Composable
    private fun RenderProfile(model: ProfileScreenModel, profile: User) {
        val navigator = LocalNavigator.currentOrThrow
        var badgeDialogTitle by remember { mutableStateOf<String?>(null) }
        var badgeDialogText by remember { mutableStateOf<String?>(null) }
        
        val thumbnailUrl = profile.profilePicOverride.ifEmpty { profile.currentAvatarImageUrl }
        val iconUrl = profile.userIcon.ifEmpty { profile.profilePicOverride.ifEmpty { profile.currentAvatarImageUrl } }
        
        Box(modifier = Modifier.fillMaxSize()) {
            if (badgeDialogText != null) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { badgeDialogText = null; badgeDialogTitle = null },
                    title = { Text(text = badgeDialogTitle ?: stringResource(R.string.profile_badge_title)) },
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
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Banner image section with overlay buttons
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {
                            GlideImage(
                                model = thumbnailUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                loading = placeholder(R.drawable.image_placeholder),
                                failure = placeholder(R.drawable.image_placeholder)
                            )
                        }
                    }
                    
                    // Profile picture overlapping banner
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-60).dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GlideImage(
                                model = iconUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                loading = placeholder(R.drawable.image_placeholder),
                                failure = placeholder(R.drawable.image_placeholder)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Display name
                            Text(
                                text = "${profile.displayName}",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Trust Rank Chip and Platform Icon
                            val trustRank = TrustHelper.getTrustRankFromTags(profile.tags)
                            val trustRankColor = trustRank.toColor()
                            val platform = profile.presence.platform.ifEmpty { profile.lastPlatform }
                            
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
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
                                
                                // Platform Icon
                                if (platform.isNotEmpty()) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            when (platform.lowercase()) {
                                                "standalonewindows" -> {
                                                    Icon(
                                                        imageVector = Icons.Default.Computer,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                "android" -> {
                                                    Icon(
                                                        imageVector = Icons.Default.Android,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                "ios" -> {
                                                    Icon(
                                                        imageVector = Icons.Default.PhoneIphone,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                "web" -> {
                                                    Icon(
                                                        imageVector = Icons.Default.Web,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                else -> {
                                                    Icon(
                                                        imageVector = Icons.Default.Devices,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                            }
                        }
                    }
                }
            }
            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Pronouns and age
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
                            
                            val inventoryOptions = stringArrayResource(R.array.inventory_selection_options)
                            // inventory_selection_options order: Gallery(0), User Icons(1), Worlds(2), Avatars(3), Groups(4), Emojis(5), Stickers(6), Prints(7), Items(8)
                            val quickActionItems = remember(profile, inventoryOptions) {
                                buildList {
                                    add(Triple(Icons.Default.Person, inventoryOptions[3]) { // Avatars
                                        navigator.push(AvatarsScreen())
                                    })
                                    add(Triple(Icons.Default.Public, inventoryOptions[2]) { // Worlds
                                        navigator.push(WorldsScreen(profile.displayName, profile.id, `private` = false))
                                    })
                                    add(Triple(Icons.Default.Collections, inventoryOptions[0]) { // Gallery
                                        navigator.push(GalleryScreen())
                                    })
                                    add(Triple(Icons.Default.Group, inventoryOptions[4]) { // Groups
                                        navigator.push(UserGroupsScreen(profile.displayName, profile.id))
                                    })
                                    add(Triple(Icons.Default.Face, inventoryOptions[1]) { // User Icons
                                        navigator.push(IconGalleryScreen())
                                    })
                                    add(Triple(Icons.Default.EmojiEmotions, inventoryOptions[5]) { // Emojis
                                        navigator.push(EmojisScreen())
                                    })
                                    add(Triple(Icons.AutoMirrored.Filled.Label, inventoryOptions[6]) { // Stickers
                                        navigator.push(StickersScreen())
                                    })
                                    add(Triple(Icons.Default.Print, inventoryOptions[7]) { // Prints
                                        navigator.push(PrintsScreen(profile.id))
                                    })
                                    add(Triple(Icons.Default.Inventory, inventoryOptions[8]) { // Items
                                        navigator.push(ItemsScreen())
                                    })
                                }
                            }
                            
                            val buttonsPerPage = 4
                            val pageCount = (quickActionItems.size + buttonsPerPage - 1) / buttonsPerPage
                            val pagerState = rememberPagerState(pageCount = { pageCount })
                            
                            Column {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxWidth(),
                                    pageSpacing = 16.dp,
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) { page ->
        Row(
            modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        repeat(buttonsPerPage) { index ->
                                            val itemIndex = page * buttonsPerPage + index
                                            if (itemIndex < quickActionItems.size) {
                                                QuickActionButton(
                                                    icon = quickActionItems[itemIndex].first,
                                                    label = quickActionItems[itemIndex].second,
                                                    onClick = quickActionItems[itemIndex].third
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.width(64.dp))
                                            }
                                        }
                                    }
                                }
                                
                                // Page indicator dots
                                if (pageCount > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        repeat(pageCount) { iteration ->
                                            val color = if (pagerState.currentPage == iteration) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                            }
                                            Box(
                    modifier = Modifier
                                                    .padding(horizontal = 4.dp)
                                                    .size(8.dp)
                                                    .background(color, CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                            

                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Badges section
                            val visibleBadges = profile.badges.filter { !it.hidden }
                            if (visibleBadges.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        items(visibleBadges) { badge ->
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
                                                        MaterialTheme.colorScheme.surface
                                                    else 
                                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                                )
                                            ) {
                                                GlideImage(
                                                    model = badge.badgeImageUrl, 
                                                    contentDescription = badge.badgeName, 
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop,
                                                    loading = placeholder(R.drawable.image_placeholder),
                                                    failure = placeholder(R.drawable.image_placeholder),
                                                    alpha = if (badge.showcased) { 1.0f } else { 0.85f }
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            Column(
                        modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                val settingsItems = buildList {
                                    // Add header as first item
                                    add(
                                        SettingsItem(
                                            title = stringResource(R.string.profile_label_account_info),
                                            description = null,
                                            icon = null,
                                            onClick = { },
                                            isHeader = true
                                        )
                                    )
                                    if (profile.hasBirthday) {
                                        // Format dateJoined as birthday display (using dateJoined as placeholder since actual birthday is not available in API)
                                        val notAvailableText = stringResource(R.string.profile_not_available)
                                        val birthdayText = try {
                                            if (profile.dateJoined.isNotEmpty() && profile.dateJoined.length >= 10) {
                                                val date = profile.dateJoined.substring(0, 10)

                                                val parts = date.split("-")
                                                if (parts.size == 3) {  
                                                    "${parts[0]}/${parts[1].toInt()}/${parts[2].toInt()}"
                                                } else {
                                                    date
                                                }
                                            } else {
                                                notAvailableText
                                            }
                                        } catch (e: Exception) {
                                            notAvailableText
                                        }
                                        
                                        add(
                                            SettingsItem(
                                                title = birthdayText,
                                                description = stringResource(R.string.profile_label_date_joined),
                                                icon = Icons.Default.Cake,
                                                onClick = { }
                                            )
                                        )
                                    }
                                    // Add bio if available
                                    if (profile.bio.isNotEmpty()) {
                                        add(
                                            SettingsItem(
                                                title = profile.bio,
                                                description = null,
                                                icon = Icons.Default.Folder,
                                                onClick = { }
                                            )
                                        )
                                    }
                                }
                                
                                SettingsGroup(items = settingsItems)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Social Links section
                            if (profile.bioLinks.isNotEmpty() && profile.bioLinks.any { it.isNotEmpty() }) {
                                val context = LocalContext.current
                                Column(
                        modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    val socialLinksItems = buildList {
                                        add(
                                            SettingsItem(
                                                title = stringResource(R.string.profile_edit_dialog_title_bio_links),
                                                description = null,
                                                icon = null,
                                                onClick = { },
                                                isHeader = true
                                            )
                                        )
                                        profile.bioLinks.forEach { link ->
                                            if (link.isNotEmpty()) {
                                                add(
                                                    SettingsItem(
                                                        title = link,
                                                        description = null,
                                                        icon = Icons.Default.Link,
                                                        onClick = {
                                                            try {
                                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
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
                                                )
                                            }
                                        }
                                    }
                                    
                                    SettingsGroup(items = socialLinksItems)
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                            
                            // Known Languages section
                            val languageTags = profile.tags.filter { it.contains("language_") }
                            if (languageTags.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    val languagesItems = buildList {
                                        add(
                                            SettingsItem(
                                                title = stringResource(R.string.profile_label_known_languages),
                                                description = null,
                                                icon = null,
                                                onClick = { },
                                                isHeader = true
                                            )
                                        )
                                        languageTags.forEach { tag ->
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
                                                "ron" -> stringResource(R.string.language_romanian)
                                                "vie" -> stringResource(R.string.language_vietnamese)
                                                "ukr" -> stringResource(R.string.language_ukrainian)
                                                "ase" -> stringResource(R.string.language_american_sign_language)
                                                "bfi" -> stringResource(R.string.language_british_sign_language)
                                                "dse" -> stringResource(R.string.language_german_sign_language)
                                                "fsl" -> stringResource(R.string.language_french_sign_language)
                                                "jsl" -> stringResource(R.string.language_japanese_sign_language)
                                                "kvk" -> stringResource(R.string.language_korean_sign_language)
                                                else -> languageCode.uppercase()
                                            }
                                            add(
                                                SettingsItem(
                                                    title = languageName,
                                                    description = null,
                                                    icon = Icons.Default.Translate,
                                                    onClick = { }
                                                )
                                            )
                                        }
                                    }
                                    
                                    SettingsGroup(items = languagesItems)
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
            
        }
    }

    @Composable
    private fun QuickActionButton(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        onClick: () -> Unit,
        isHighlighted: Boolean = false
    ) {
        val backgroundColor = MaterialTheme.colorScheme.primary
        
        val iconColor = MaterialTheme.colorScheme.onPrimary
        
        val textColor = MaterialTheme.colorScheme.onSurfaceVariant
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(84.dp)
                    .height(56.dp)
                    .background(backgroundColor, RoundedCornerShape(1000.dp))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = label,
                fontSize = 11.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
        }
    }

    @Composable
    private fun ContactInfoCard(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        text: String,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFE91E63), // Pink color
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = Color(0xFFE91E63), // Pink color
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
    
}