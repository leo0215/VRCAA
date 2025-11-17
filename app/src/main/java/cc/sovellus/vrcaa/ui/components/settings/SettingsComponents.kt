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

package cc.sovellus.vrcaa.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun rememberThumbContent(
    isChecked: Boolean,
    checkedIcon: ImageVector = Icons.Filled.Check,
): (@Composable () -> Unit)? =
    remember(isChecked, checkedIcon) {
        if (isChecked) {
            {
                Icon(
                    imageVector = checkedIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        } else {
            null
        }
    }

data class SettingsItem(
    val title: String,
    val description: String? = null,
    val icon: ImageVector? = null,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false,
    val trailingContent: @Composable (() -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsGroup(items: List<SettingsItem>) {
    val isSingleItem = items.size == 1
    val containerCornerRadius = 28.dp // 外層容器圓角
    val itemCornerRadius = if (isSingleItem) 28.dp else 4.dp // 單個項目 28dp，多個項目 4dp
    
    if (isSingleItem) {
        // 單個項目使用一個 Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(itemCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        ) {
            SettingsCard(
                title = items[0].title,
                description = items[0].description,
                icon = items[0].icon,
                onClick = items[0].onClick,
                isDestructive = items[0].isDestructive,
                trailingContent = items[0].trailingContent,
                isSingleItem = true
            )
        }
    } else {
        // 多個項目：外層容器 + 內部項目，gap 2px
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
                items.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(itemCornerRadius),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    ) {
                        SettingsCard(
                            title = item.title,
                            description = item.description,
                            icon = item.icon,
                            onClick = item.onClick,
                            isDestructive = item.isDestructive,
                            trailingContent = item.trailingContent,
                            isSingleItem = false
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCard(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null,
    isSingleItem: Boolean = false
) {
    if (isSingleItem) {
        // 單個項目使用 Row 佈局，符合 CSS 樣式
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(onClick = onClick)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                color = if (isDestructive) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
            trailingContent?.invoke()
        }
    } else {
        // 多個項目使用 Row 佈局，符合 XML 樣式
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .clickable(onClick = onClick)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon - 32dp according to CSS
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            // Content - Column layout
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                // Title - 14px font size, 20px line height
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    fontWeight = FontWeight.Normal,
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                // Labels (Metadata + Supporting Text) - 12px font size, 16px line height
                description?.let {
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            
            // Trailing content
            trailingContent?.invoke()
        }
    }
}

