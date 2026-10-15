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

package cc.sovellus.vrcaa.ui.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.FontHelper

/**
 * @param customFontName name of the imported custom font, or null when none is imported.
 * @param onPickCustomFont opens the system file picker to import (or replace) the custom font.
 * @param onRemoveCustomFont deletes the imported custom font.
 */
@Composable
fun FontSelectionDialog(
    onDismiss: () -> Unit,
    onFontSelected: (Int) -> Unit,
    currentFontIndex: Int,
    customFontName: String? = null,
    onPickCustomFont: () -> Unit = {},
    onRemoveCustomFont: () -> Unit = {}
) {
    // (stored `fontFamily` value, label). Values are explicit so hiding an option
    // never shifts the value saved for the options after it.
    val fontOptions = listOf(
        FontHelper.FONT_SYSTEM_DEFAULT to stringResource(R.string.font_option_system_default),
        1 to stringResource(R.string.font_option_google_sans),
        2 to stringResource(R.string.font_option_google_sans_flex),
//        3 to stringResource(R.string.font_option_google_sans_rounded),
    )
    val showCustomFont = FontHelper.isSupported()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.font_selection_dialog_title))
        },
        text = {
            Column {
                fontOptions.forEachIndexed { index, (value, fontName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onFontSelected(value)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fontName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (currentFontIndex == value) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (index < fontOptions.size - 1 || showCustomFont) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp
                        )
                    }
                }

                if (showCustomFont) {
                    CustomFontRow(
                        customFontName = customFontName,
                        isSelected = currentFontIndex == FontHelper.FONT_CUSTOM && customFontName != null,
                        onSelect = {
                            onFontSelected(FontHelper.FONT_CUSTOM)
                            onDismiss()
                        },
                        onPickCustomFont = onPickCustomFont,
                        onRemoveCustomFont = onRemoveCustomFont
                    )
                }
            }
        },
        confirmButton = {}
    )
}

/**
 * Before a font is imported, tapping the row opens the file picker. Afterwards it shows the
 * font's name, tapping selects it, and the trailing menu replaces or removes the file.
 */
@Composable
private fun CustomFontRow(
    customFontName: String?,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPickCustomFont: () -> Unit,
    onRemoveCustomFont: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (customFontName != null) onSelect() else onPickCustomFont()
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.font_option_custom),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = customFontName ?: stringResource(R.string.font_option_custom_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        if (customFontName != null) {
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.font_custom_options)
                    )
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.font_custom_replace)) },
                        onClick = {
                            isMenuExpanded = false
                            onPickCustomFont()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.font_custom_remove)) },
                        onClick = {
                            isMenuExpanded = false
                            onRemoveCustomFont()
                        }
                    )
                }
            }
        }
    }
}
