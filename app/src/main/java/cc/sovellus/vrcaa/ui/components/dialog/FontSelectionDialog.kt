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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R

@Composable
fun FontSelectionDialog(
    onDismiss: () -> Unit,
    onFontSelected: (Int) -> Unit,
    currentFontIndex: Int
) {
    val fontOptions = listOf(
        stringResource(R.string.font_option_system_default),
        stringResource(R.string.font_option_google_sans),
        stringResource(R.string.font_option_google_sans_flex),
        stringResource(R.string.font_option_google_sans_rounded)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.font_selection_dialog_title))
        },
        text = {
            Column {
                fontOptions.forEachIndexed { index, fontName ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // #region agent log
                                try {
                                    val logFile = java.io.File("d:\\Doc\\workspace\\VRCAA\\.cursor\\debug.log")
                                    val logEntry = """{"sessionId":"debug-session","runId":"run1","hypothesisId":"C","location":"FontSelectionDialog.kt:63","message":"Font selected","data":{"selectedIndex":$index},"timestamp":${System.currentTimeMillis()}}""" + "\n"
                                    logFile.appendText(logEntry)
                                } catch (e: Exception) {}
                                // #endregion
                                onFontSelected(index)
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
                        if (currentFontIndex == index) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (index < fontOptions.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}

