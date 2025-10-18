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

package cc.sovellus.vrcaa.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.clickableIf
import cc.sovellus.vrcaa.api.vrchat.http.models.Badge as UserBadge
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.Languages
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

// Standard card modifier with unified sizing
private fun Modifier.standardCardModifier() = this
    .fillMaxWidth()
    .widthIn(Dp.Unspecified, 520.dp)
    .padding(horizontal = 16.dp, vertical = 8.dp)

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileCard(
    thumbnailUrl: String,
    iconUrl: String,
    displayName: String,
    statusDescription: String,
    trustRankColor: Color,
    trustRankName: String,
    statusColor: Color,
    tags: List<String>,
    badges: List<UserBadge>,
    pronouns: String,
    ageVerificationStatus: String,
    disablePeek: Boolean = true,
    onPeek: (url: String) -> Unit,
    onBadgeClick: (UserBadge) -> Unit = { },
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.standardCardModifier(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Banner Section with background image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                GlideImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickableIf(
                            enabled = !disablePeek,
                            onClick = {
                                onPeek(thumbnailUrl)
                            }),
                    contentScale = ContentScale.Crop,
                    loading = placeholder(R.drawable.image_placeholder),
                    failure = placeholder(R.drawable.image_placeholder)
                )
                
                // Gradient overlay for better readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )
                
                // Languages in top-left corner
                if (tags.isNotEmpty()) {
                    Languages(
                        languages = tags,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    )
                }
                
                // Badges overlay in top-right corner
                val visibleBadges = badges.filter { !it.hidden }
                if (visibleBadges.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        reverseLayout = true
                    ) {
                        items(visibleBadges) { badge ->
                            Card(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickableIf(
                                        enabled = true,
                                        onClick = { onBadgeClick(badge) }
                                    ),
                                shape = RoundedCornerShape(16.dp),
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
                                    alpha = if (badge.showcased) { 1.0f } else { 0.85f },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
                
                // Troll warning banner
                if (tags.contains("system_probable_troll")) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "此用戶可能是惡作劇用戶",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
                
                // Age verification badge
                if (ageVerificationStatus == "18+") {
                    Badge(
                        containerColor = Color(0xFF606FE4),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            text = "18+",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Profile Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .offset(y = (-50).dp)
                        .clickableIf(
                            enabled = !disablePeek,
                            onClick = {
                                onPeek(iconUrl)
                            }),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    GlideImage(
                        model = iconUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(46.dp)),
                        contentScale = ContentScale.Crop,
                        loading = placeholder(R.drawable.image_placeholder),
                        failure = placeholder(R.drawable.image_placeholder)
                    )
                }
                
                // User Info (adjusted for overlapping avatar)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-30).dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // User Name
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Trust Rank with styled background
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = trustRankColor.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = trustRankName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = trustRankColor,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                    
                    // Pronouns
                    if (pronouns.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = pronouns,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Status
                    if (statusDescription.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .background(
                                    color = statusColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Badge(
                                containerColor = statusColor, 
                                modifier = Modifier.size(8.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = statusDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BiographyCard(
    bio: String,
    modifier: Modifier = Modifier,
    maxCollapsedLines: Int = 4
) {
    if (bio.isNotEmpty()) {
        var isExpanded by remember { mutableStateOf(false) }
        var showReadMoreButton by remember { mutableStateOf(false) }
        
        Card(
            modifier = modifier.standardCardModifier(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.profile_label_biography),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Expand/Collapse Arrow (only show if text is long enough)
                    if (showReadMoreButton) {
                        IconButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) 
                                    Icons.Default.KeyboardArrowUp 
                                else 
                                    Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) 
                                    "Show less" 
                                else 
                                    "Show more",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Biography content
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    maxLines = if (isExpanded) Int.MAX_VALUE else maxCollapsedLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 12.dp),
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.didOverflowHeight) {
                            showReadMoreButton = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AccountStatusCard(
    dateJoined: String,
    ageVerificationStatus: String,
    platform: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.standardCardModifier(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.profile_label_account_info),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Account Info Items
            AccountInfoItem(
                icon = Icons.Default.CalendarToday,
                label = stringResource(R.string.profile_label_date_joined),
                value = dateJoined,
                iconTint = MaterialTheme.colorScheme.secondary
            )

            if (ageVerificationStatus == "18+") {
                AccountInfoItem(
                    icon = Icons.Default.Verified,
                    label = stringResource(R.string.profile_label_age_verification),
                    value = "18+ Verified",
                    iconTint = Color(0xFF606FE4)
                )
            }

            if (!platform.isNullOrEmpty() && platform != "unknown") {
                AccountInfoItem(
                    icon = Icons.Default.Devices,
                    label = "Platform",
                    value = platform.replaceFirstChar { it.uppercase() },
                    iconTint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun AccountInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = iconTint.copy(alpha = 0.1f)
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(20.dp)
                    .padding(10.dp)
            )
        }
        
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun InstanceCard(
    profile: LimitedUser, 
    instance: Instance, 
    disabled: Boolean, 
    callback: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .standardCardModifier()
            .height(160.dp)
            .clickableIf(
                enabled = !disabled,
                onClick = {
                    callback()
                }),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        val result = LocationHelper.parseLocationInfo(profile.location)

        SubHeader(title = stringResource(id = R.string.profile_label_current_location))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(12.dp)
        ) {
            GlideImage(
                model = instance.world.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(160.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(10)),
                contentScale = ContentScale.Crop,
                loading = placeholder(R.drawable.image_placeholder),
                failure = placeholder(R.drawable.image_placeholder)
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = instance.world.name,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "#${result.instanceId}:${result.instanceType}",
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.padding(4.dp))

                Text(
                    text = "${instance.nUsers}/${instance.capacity}",
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LastActivityCard(
    lastActivity: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.standardCardModifier(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        SubHeader(
            title = stringResource(R.string.profile_label_last_activity),
            icon = Icons.Default.AccessTime
        )
        Description(text = lastActivity)
    }
}
