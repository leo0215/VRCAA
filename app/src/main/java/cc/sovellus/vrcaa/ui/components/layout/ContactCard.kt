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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.helper.StatusHelper
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.request.RequestOptions
import cc.sovellus.vrcaa.ui.theme.listCardBackground

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ContactAvatar(friend: Friend) {
    val displayName = friend.displayName
    val firstChar = if (displayName.isNotEmpty()) displayName.first() else '?'
    val avatarText = firstChar.uppercaseChar().toString()

    Box(modifier = Modifier.size(40.dp)) {
        val imageUrl = friend.userIcon.ifEmpty {
            friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl }
        }

        if (imageUrl.isNotEmpty()) {
            GlideImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                loading = placeholder(R.drawable.image_placeholder),
                failure = placeholder(R.drawable.image_placeholder),
                requestBuilderTransform = { requestBuilder ->
                    requestBuilder.apply(
                        RequestOptions()
                            .override(160, 160)
                            .dontAnimate()
                            .encodeFormat(Bitmap.CompressFormat.PNG)
                            .encodeQuality(100)
                    )
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = avatarText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 16.sp,
                )
            }
        }

        val isOnline = friend.platform.isNotEmpty()
        val statusColor = if (isOnline) {
            StatusHelper.getStatusFromString(friend.status).toColor()
        } else {
            StatusHelper.Status.Offline.toColor()
        }
        Box(
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.BottomEnd)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape,
                )
                .padding(2.dp)
                .background(
                    color = statusColor,
                    shape = CircleShape,
                ),
        )
    }
}

/**
 * 朋友分組組件，顯示一個字母標題和該字母下的所有朋友
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FriendsGroup(
    letter: String,
    friends: List<Friend>,
    onFriendClick: (Friend) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            fontSize = 16.sp,
        )

        Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
            friends.forEachIndexed { index, friend ->
                SegmentedListItem(
                    onClick = { onFriendClick(friend) },
                    modifier = Modifier.segmentedListMinHeight(SegmentedListLineLayout.OneLine),
                    shapes = segmentedListItemShapes(index = index, count = friends.size),
                    verticalAlignment = Alignment.CenterVertically,
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.listCardBackground,
                    ),
                    leadingContent = { ContactAvatar(friend) },
                    content = {
                        Text(
                            text = friend.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }
    }
}
