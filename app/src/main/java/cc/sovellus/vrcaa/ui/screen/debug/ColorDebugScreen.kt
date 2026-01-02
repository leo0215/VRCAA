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

package cc.sovellus.vrcaa.ui.screen.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import java.util.Locale

class ColorDebugScreen : Screen {
    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val colorScheme = MaterialTheme.colorScheme

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    title = { Text("Material Colors Debug") }
                )
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Current Theme Colors",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    val colorGroups = listOf(
                        "Primary" to listOf(
                            "primary" to colorScheme.primary,
                            "onPrimary" to colorScheme.onPrimary,
                            "primaryContainer" to colorScheme.primaryContainer,
                            "onPrimaryContainer" to colorScheme.onPrimaryContainer
                        ),
                        "Secondary" to listOf(
                            "secondary" to colorScheme.secondary,
                            "onSecondary" to colorScheme.onSecondary,
                            "secondaryContainer" to colorScheme.secondaryContainer,
                            "onSecondaryContainer" to colorScheme.onSecondaryContainer
                        ),
                        "Tertiary" to listOf(
                            "tertiary" to colorScheme.tertiary,
                            "onTertiary" to colorScheme.onTertiary,
                            "tertiaryContainer" to colorScheme.tertiaryContainer,
                            "onTertiaryContainer" to colorScheme.onTertiaryContainer
                        ),
                        "Error" to listOf(
                            "error" to colorScheme.error,
                            "onError" to colorScheme.onError,
                            "errorContainer" to colorScheme.errorContainer,
                            "onErrorContainer" to colorScheme.onErrorContainer
                        ),
                        "Background & Surface" to listOf(
                            "background" to colorScheme.background,
                            "onBackground" to colorScheme.onBackground,
                            "surface" to colorScheme.surface,
                            "onSurface" to colorScheme.onSurface,
                            "surfaceVariant" to colorScheme.surfaceVariant,
                            "onSurfaceVariant" to colorScheme.onSurfaceVariant
                        ),
                        "Surface Containers" to listOf(
                            "surfaceContainerLowest" to colorScheme.surfaceContainerLowest,
                            "surfaceContainerLow" to colorScheme.surfaceContainerLow,
                            "surfaceContainer" to colorScheme.surfaceContainer,
                            "surfaceContainerHigh" to colorScheme.surfaceContainerHigh,
                            "surfaceContainerHighest" to colorScheme.surfaceContainerHighest
                        ),
                        "Outline" to listOf(
                            "outline" to colorScheme.outline,
                            "outlineVariant" to colorScheme.outlineVariant
                        ),
                        "Inverse" to listOf(
                            "inverseSurface" to colorScheme.inverseSurface,
                            "inverseOnSurface" to colorScheme.inverseOnSurface,
                            "inversePrimary" to colorScheme.inversePrimary
                        )
                    )

                    items(colorGroups) { (groupName, colors) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = groupName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                
                                colors.forEach { (name, color) ->
                                    ColorItem(
                                        name = name,
                                        color = color
                                    )
                                    if (colors.indexOf(name to color) < colors.size - 1) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    private fun ColorItem(
        name: String,
        color: Color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, RoundedCornerShape(8.dp))
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name.replaceFirstChar { 
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) 
                            else it.toString() 
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = colorToHex(color),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    private fun colorToHex(color: Color): String {
        val argb = color.toArgb()
        val alpha = (argb shr 24) and 0xFF
        val red = (argb shr 16) and 0xFF
        val green = (argb shr 8) and 0xFF
        val blue = argb and 0xFF
        return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
    }
}

