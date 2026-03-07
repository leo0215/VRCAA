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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.Alignment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
    val trailingContent: @Composable (() -> Unit)? = null,
    val isHeader: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionHeaderCard(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsGroup(items: List<SettingsItem>) {
    val isSingleItem = items.size == 1
    val containerRadius = 16.dp
    val itemRadius = 4.dp

    if (isSingleItem) {
        val item = items[0]
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(containerRadius),
            colors = CardDefaults.cardColors(
                containerColor = if (item.isHeader) {
                    MaterialTheme.colorScheme.surfaceDim
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
        ) {
            SettingsCard(
                title = item.title,
                description = item.description,
                icon = item.icon,
                onClick = item.onClick,
                isDestructive = item.isDestructive,
                trailingContent = item.trailingContent,
                isHeader = item.isHeader
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items.forEachIndexed { index, item ->
                val isFirst = index == 0
                val isLast = index == items.lastIndex
                val shape = when {
                    isFirst && isLast -> RoundedCornerShape(containerRadius)
                    isFirst -> RoundedCornerShape(topStart = containerRadius, topEnd = containerRadius, bottomStart = itemRadius, bottomEnd = itemRadius)
                    isLast -> RoundedCornerShape(topStart = itemRadius, topEnd = itemRadius, bottomStart = containerRadius, bottomEnd = containerRadius)
                    else -> RoundedCornerShape(itemRadius)
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = shape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isHeader) {
                            MaterialTheme.colorScheme.surfaceDim
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    SettingsCard(
                        title = item.title,
                        description = item.description,
                        icon = item.icon,
                        onClick = item.onClick,
                        isDestructive = item.isDestructive,
                        trailingContent = item.trailingContent,
                        isHeader = item.isHeader
                    )
                }
            }
        }
    }
}

/**
 * Expandable card group per M3 design: header with accordion toggle, content items with 2dp gap.
 * - Container: 16dp radius, surface
 * - Header: 52dp, accordion button trailing
 * - Content items: 52dp, padding 10dp 16dp, gap 12dp
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableSettingsGroup(
    headerTitle: String,
    items: List<SettingsItem>,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val containerRadius = 16.dp
    val itemRadius = 4.dp
    val contentItems = items.filter { !it.isHeader }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Header row with accordion
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                topStart = containerRadius,
                topEnd = containerRadius,
                bottomStart = if (contentItems.isEmpty() || !expanded) containerRadius else itemRadius,
                bottomEnd = if (contentItems.isEmpty() || !expanded) containerRadius else itemRadius
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(), 
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                contentItems.forEachIndexed { index, item ->
                    val isLast = index == contentItems.lastIndex
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(
                            topStart = itemRadius,
                            topEnd = itemRadius,
                            bottomStart = if (isLast) containerRadius else itemRadius,
                            bottomEnd = if (isLast) containerRadius else itemRadius
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        SettingsCard(
                            title = item.title,
                            description = item.description,
                            icon = item.icon,
                            onClick = item.onClick,
                            isDestructive = item.isDestructive,
                            trailingContent = item.trailingContent,
                            isHeader = false
                        )
                    }
                }
            }
        }
    }
}

/**
 * M3 List Item color roles:
 * - Container: Surface
 * - Label text: On surface
 * - Supporting text / Overline: On surface variant
 * - Leading icon: On surface variant
 * - Trailing text/icon: On surface variant
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCard(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null,
    isHeader: Boolean = false
) {
    val contentColor = when {
        isDestructive -> MaterialTheme.colorScheme.error
        isHeader -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }
    val secondaryColor = MaterialTheme.colorScheme.onSurfaceVariant
    val iconTint = if (isDestructive) MaterialTheme.colorScheme.error else secondaryColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .then(if (description == null) Modifier.height(52.dp) else Modifier.height(64.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = if (isHeader) Arrangement.Center else Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isHeader) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = iconTint
                )
            }
        }
        if (description == null) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isHeader) FontWeight.Medium else FontWeight.Normal,
                color = contentColor,
                modifier = if (isHeader) Modifier else Modifier.weight(1f),
                maxLines = Int.MAX_VALUE
            )
        } else {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isHeader) FontWeight.Medium else FontWeight.Normal,
                    color = contentColor
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryColor,
                    maxLines = 2
                )
            }
        }
        if (!isHeader) {
            CompositionLocalProvider(LocalContentColor provides secondaryColor) {
                trailingContent?.invoke()
            }
        }
    }
}

