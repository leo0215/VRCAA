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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.ui.components.layout.SegmentedContainerCornerRadius
import cc.sovellus.vrcaa.ui.components.layout.SegmentedListLineLayout
import cc.sovellus.vrcaa.ui.components.layout.segmentedListItemShapes
import cc.sovellus.vrcaa.ui.components.layout.segmentedListMinHeight
import cc.sovellus.vrcaa.ui.theme.listCardBackground

@Composable
fun rememberThumbContent(
    isChecked: Boolean,
    checkedIcon: ImageVector = Icons.Filled.Check,
    uncheckedIcon: ImageVector = Icons.Filled.Close,
): @Composable () -> Unit =
    remember(isChecked, checkedIcon, uncheckedIcon) {
        {
            Icon(
                imageVector = if (isChecked) checkedIcon else uncheckedIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (isChecked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    contentColorFor(MaterialTheme.colorScheme.outline)
                },
            )
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
            .segmentedListMinHeight(SegmentedListLineLayout.OneLine),
        shape = RoundedCornerShape(SegmentedContainerCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.listCardBackground
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

private fun SettingsItem.lineLayout(): SegmentedListLineLayout =
    if (description.isNullOrBlank()) {
        SegmentedListLineLayout.OneLine
    } else {
        SegmentedListLineLayout.ThreeLine
    }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsSegmentedItem(item: SettingsItem, index: Int, count: Int) {
    val contentColor = when {
        item.isDestructive -> MaterialTheme.colorScheme.error
        item.isHeader -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }
    val secondaryColor = MaterialTheme.colorScheme.onSurfaceVariant
    val iconTint = if (item.isDestructive) MaterialTheme.colorScheme.error else secondaryColor

    SegmentedListItem(
        onClick = item.onClick,
        modifier = Modifier.segmentedListMinHeight(item.lineLayout()),
        shapes = segmentedListItemShapes(index = index, count = count),
        verticalAlignment = Alignment.CenterVertically,
        colors = ListItemDefaults.segmentedColors(
            containerColor = if (item.isHeader) {
                MaterialTheme.colorScheme.surfaceDim
            } else {
                MaterialTheme.colorScheme.listCardBackground
            },
            contentColor = contentColor,
            leadingContentColor = iconTint,
        ),
        leadingContent = item.icon?.takeIf { !item.isHeader }?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = iconTint,
                )
            }
        },
        supportingContent = item.description?.let { desc ->
            {
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryColor,
                )
            }
        },
        trailingContent = item.trailingContent.takeIf { !item.isHeader },
        content = {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (item.isHeader) FontWeight.Medium else FontWeight.Normal,
                color = contentColor,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsGroup(items: List<SettingsItem>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
    ) {
        items.forEachIndexed { index, item ->
            SettingsSegmentedItem(item, index, items.size)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpandableSettingsHeader(
    headerTitle: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    index: Int,
    count: Int,
    headerTrailing: (@Composable () -> Unit)? = null,
) {
    SegmentedListItem(
        onClick = onToggle,
        modifier = Modifier.segmentedListMinHeight(SegmentedListLineLayout.OneLine),
        shapes = segmentedListItemShapes(index = index, count = count),
        verticalAlignment = Alignment.CenterVertically,
        colors = ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.listCardBackground,
        ),
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                headerTrailing?.invoke()
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 40.dp)
                        .background(
                            MaterialTheme.colorScheme.listCardBackground,
                            RoundedCornerShape(20.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        content = {
            Text(
                text = headerTitle,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
    )
}

/**
 * Expandable segmented list group: header with accordion toggle, content items with [ListItemDefaults.SegmentedGap].
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandableSettingsGroup(
    headerTitle: String,
    items: List<SettingsItem>,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true,
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val contentItems = items.filter { !it.isHeader }
    val totalCount = if (expanded && contentItems.isNotEmpty()) 1 + contentItems.size else 1

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
    ) {
        ExpandableSettingsHeader(
            headerTitle = headerTitle,
            expanded = expanded,
            onToggle = { expanded = !expanded },
            index = 0,
            count = totalCount,
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            ) {
                contentItems.forEachIndexed { index, item ->
                    SettingsSegmentedItem(item, index + 1, totalCount)
                }
            }
        }
    }
}

/**
 * Same header / accordion visuals as [ExpandableSettingsGroup] for list of [SettingsItem],
 * but content is an arbitrary composable. Expanded content is not wrapped in an extra card: use
 * [cc.sovellus.vrcaa.ui.components.layout.FavoriteVerticalSegmentRowItem] (or similar) with
 * [ListItemDefaults.SegmentedGap] for stacked segments.
 *
 * @param stateKey When non-null, expansion state is remembered under this key (e.g. tab + group id).
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandableSettingsGroup(
    headerTitle: String,
    modifier: Modifier = Modifier,
    stateKey: Any? = null,
    initiallyExpanded: Boolean = true,
    headerTrailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var expanded by remember(stateKey) { mutableStateOf(initiallyExpanded) }
    val headerCount = if (expanded) 2 else 1

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
    ) {
        ExpandableSettingsHeader(
            headerTitle = headerTitle,
            expanded = expanded,
            onToggle = { expanded = !expanded },
            index = 0,
            count = headerCount,
            headerTrailing = headerTrailing,
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            ) {
                content()
            }
        }
    }
}
