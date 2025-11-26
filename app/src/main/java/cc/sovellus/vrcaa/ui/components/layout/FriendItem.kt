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

package cc.sovellus.vrcaa.ui.components.layout

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.request.RequestOptions

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FriendItem(
    friend: Friend,
    callback: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(friend.displayName)
        },
        overlineContent = {
            Text(friend.statusDescription.ifEmpty {
                if (friend.platform.isEmpty()) { StatusHelper.Status.Offline.toString() } else { StatusHelper.getStatusFromString(friend.status).toString() }
            }, maxLines = 1)
        },
        supportingContent = {
            Text(text =
                (if (friend.platform == "web") {
                    "Active on website."
                }
                else if (friend.platform.isEmpty()) {
                    "offline"
                }
                else {
                    LocationHelper.getReadableLocation(friend.location)
                }),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Column {
                Badge(
                    containerColor = if (friend.platform.isEmpty()) { StatusHelper.Status.Offline.toColor() } else { StatusHelper.getStatusFromString(friend.status).toColor() },
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    GlideImage(
                        model = friend.userIcon.ifEmpty { friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl } },
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(50)),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        loading = placeholder(R.drawable.image_placeholder),
                        failure = placeholder(R.drawable.image_placeholder),
                        requestBuilderTransform = { requestBuilder ->
                            requestBuilder
                                .apply(
                                    RequestOptions()
                                        .override(256, 256) // 4x for high DPI screens
                                        .dontAnimate()
                                        .encodeFormat(Bitmap.CompressFormat.PNG)
                                        .encodeQuality(100)
                                )
                        }
                    )
                }
            }
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.size(20.dp)
            ) {
                when (friend.platform) {
                    "standalonewindows" -> {
                        Icon(Icons.Default.Computer, null, modifier = Modifier.size(20.dp).align(Alignment.CenterHorizontally))
                    }
                    "android" -> {
                        Icon(Icons.Default.Android, null, modifier = Modifier.size(20.dp).align(Alignment.CenterHorizontally))
                    }
                    "ios" -> {
                        Icon(Icons.Default.PhoneIphone, null, modifier = Modifier.size(20.dp).align(Alignment.CenterHorizontally))
                    }
                    "web" -> {
                        Icon(Icons.Default.Web, null, modifier = Modifier.size(20.dp).align(Alignment.CenterHorizontally))
                    }
                }
            }
        },
        modifier = Modifier.clickable(
            onClick = {
                callback()
            }
        )
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FriendItemMaterial3(
    friend: Friend,
    showLetter: Boolean = false,
    letter: String = "",
    callback: () -> Unit
) {
    val isOnline = friend.platform.isNotEmpty()
    val isPrivate = friend.location == "private" || friend.location == "traveling" || friend.location == "offline" ||
            (friend.location.isNotEmpty() && friend.location.contains("wrld_")).let { hasWorld ->
                if (hasWorld) {
                    val locationInfo = LocationHelper.parseLocationInfo(friend.location)
                    locationInfo.privateId.isNotEmpty() || locationInfo.hiddenId.isNotEmpty()
                } else {
                    false
                }
            }
    val showLocation = isOnline && !isPrivate && friend.platform != "web"
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (showLocation || friend.platform == "web") 80.dp else 72.dp)
            .clickable(onClick = callback)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Letter indicator (only shown when showLetter is true)
        if (showLetter && letter.isNotEmpty()) {
            Text(
                text = letter,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(width = 16.dp, height = 32.dp)
            )
        } else {
            // Spacer to maintain alignment when letter is hidden
            Box(modifier = Modifier.size(width = 16.dp, height = 32.dp))
        }
        
        // Avatar with status indicator
        Box {
            GlideImage(
                model = friend.userIcon.ifEmpty { friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl } },
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                loading = placeholder(R.drawable.image_placeholder),
                failure = placeholder(R.drawable.image_placeholder),
                requestBuilderTransform = { requestBuilder ->
                    requestBuilder
                        .apply(
                            RequestOptions()
                                .override(224, 224) // 4x for high DPI screens
                                .dontAnimate()
                                .encodeFormat(Bitmap.CompressFormat.PNG)
                                .encodeQuality(100)
                        )
                }
            )
            // Status indicator at bottom right
            if (isOnline) {
                val statusColor = StatusHelper.getStatusFromString(friend.status).toColor()
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        )
                        .padding(3.dp)
                        .background(
                            color = statusColor,
                            shape = CircleShape
                        )
                )
            }
        }
        
        // Name and status/location
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = friend.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showLocation) {
                // Location
                Text(
                    text = LocationHelper.getReadableLocation(friend.location),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else if (friend.platform == "web") {
                Text(
                    text = "Active on website",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}