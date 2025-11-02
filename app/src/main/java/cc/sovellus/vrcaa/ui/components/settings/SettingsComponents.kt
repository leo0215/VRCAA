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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsGroup(items: List<SettingsItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column {
            items.forEachIndexed { index, item ->
                SettingsCard(
                    title = item.title,
                    description = item.description,
                    icon = item.icon,
                    onClick = item.onClick,
                    isDestructive = item.isDestructive,
                    trailingContent = item.trailingContent
                )
                
                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp
                    )
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
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                color = if (isDestructive) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        supportingContent = description?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = icon?.let {
            {
                androidx.compose.material3.Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        },
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

