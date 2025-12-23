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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.StrokeHelper
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.request.RequestOptions

/**
 * 聯絡人卡片組件（類似圖片中的設計）
 * 白色圓角卡片，左側圓形頭像，右側姓名
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ContactCard(
    friend: Friend,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSingleItem: Boolean = false
) {
    val displayName = friend.displayName
    val firstChar = if (displayName.isNotEmpty()) displayName.first() else '?'
    val isChinese = StrokeHelper.isChinese(firstChar)
    val avatarText = if (isChinese) firstChar.toString() else firstChar.uppercaseChar().toString()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 圓形頭像
        Box(
            modifier = Modifier.size(40.dp)
        ) {
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
                // 如果沒有圖片，顯示文字頭像
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 16.sp
                    )
                }
            }
            
            // 狀態指示器（右下角）
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
                        shape = CircleShape
                    )
                    .padding(2.dp)
                    .background(
                        color = statusColor,
                        shape = CircleShape
                    )
            )
        }
        
        // 姓名
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 朋友分組組件，類似 SettingsGroup
 * 顯示一個字母標題和該字母下的所有朋友卡片
 */
@Composable
fun FriendsGroup(
    letter: String,
    friends: List<Friend>,
    onFriendClick: (Friend) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSingleItem = friends.size == 1
    val containerCornerRadius = 14.dp
    val itemCornerRadius = if (isSingleItem) 28.dp else 4.dp
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 字母標題（大號粉色字母）
        Text(
            text = letter,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            fontSize = 16.sp
        )
        
        if (isSingleItem) {
            // 單個項目：使用 28.dp 圓角
            val friend = friends[0]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(itemCornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            ) {
                ContactCard(
                    friend = friend,
                    onClick = { onFriendClick(friend) },
                    isSingleItem = true
                )
            }
        } else {
            // 多個項目：容器 14.dp 圓角，每個項目 4.dp 圓角
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(containerCornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(0.dp)
                ) {
                    friends.forEachIndexed { index, friend ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(itemCornerRadius),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        ) {
                            ContactCard(
                                friend = friend,
                                onClick = { onFriendClick(friend) },
                                isSingleItem = false
                            )
                        }
                    }
                }
            }
        }
    }
}

