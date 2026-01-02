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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.SchemeTonalSpot
import com.materialkolor.scheme.SchemeVibrant
import com.materialkolor.scheme.SchemeFruitSalad
import com.materialkolor.scheme.SchemeExpressive

// Color list using HCT color space (same as Seal)
private val ColorPalette = ((4..10) + (1..3)).map { it * 35.0 }.map { Color(Hct.from(it, 40.0, 40.0).toInt()) }

// Color scheme types (4 styles per color, matching Seal)
// Order: TonalSpot (0), Expressive (1), FruitSalad (2), Vibrant (3)
private enum class ColorSchemeType {
    TONAL_SPOT,      // 0
    EXPRESSIVE,      // 1 (replaces Spritz in Seal)
    FRUIT_SALAD,     // 2
    VIBRANT          // 3
}

// Data class to represent color + scheme combination
private data class ColorSchemeOption(
    val seedColor: Color,
    val schemeType: ColorSchemeType,
    val schemeIndex: Int // Index for this scheme type (0-3)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ColorPicker(
    selectedColor: Color?,
    selectedSchemeIndex: Int = 0, // Index of selected scheme (0-3)
    onColorSelected: (Color, Int) -> Unit, // Color and scheme index
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        ColorPickerContent(
            selectedColor = selectedColor,
            selectedSchemeIndex = selectedSchemeIndex,
            onColorSelected = onColorSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ColorPickerContent(
    selectedColor: Color?,
    selectedSchemeIndex: Int = 0,
    onColorSelected: (Color, Int) -> Unit, // Color and scheme index
    modifier: Modifier = Modifier
) {
    val pageCount = ColorPalette.size // One page per color
    
    val initialPage = remember(selectedColor) {
        selectedColor?.let { color ->
            ColorPalette.indexOfFirst { it.toArgb() == color.toArgb() }.let {
                if (it >= 0) it else 0
            }
        } ?: 0
    }
    
    val pagerState = rememberPagerState(
        initialPage = initialPage
    ) {
        pageCount
    }

    Column(
        modifier = modifier.padding(vertical = 12.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().clearAndSetSemantics {},
            contentPadding = PaddingValues(horizontal = 12.dp),
            pageSpacing = 8.dp
        ) { page ->
            val color = ColorPalette[page]
            // Show 4 style options for this color
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorSchemeType.values().forEachIndexed { schemeIndex, schemeType ->
                    val option = ColorSchemeOption(color, schemeType, schemeIndex)
                    // Only select if both color AND scheme index match
                    val isSelected = selectedColor?.toArgb() == color.toArgb() && 
                                    selectedSchemeIndex == schemeIndex
                    
                    ColorButton(
                        colorOption = option,
                        isSelected = isSelected,
                        onClick = { onColorSelected(color, schemeIndex) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.ColorButton(
    colorOption: ColorSchemeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerSize by animateDpAsState(
        targetValue = if (isSelected) 28.dp else 0.dp,
        label = "containerSize"
    )
    val iconSize by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 0.dp,
        label = "iconSize"
    )
    
    // Generate 3 colors from seed color using Material Color Utilities
    val sourceColorHct = Hct.fromInt(colorOption.seedColor.toArgb())
    
    // Create scheme based on scheme type
    val scheme = when (colorOption.schemeType) {
        ColorSchemeType.TONAL_SPOT -> SchemeTonalSpot(sourceColorHct, false, 0.0)
        ColorSchemeType.VIBRANT -> SchemeVibrant(sourceColorHct, false, 0.0)
        ColorSchemeType.FRUIT_SALAD -> SchemeFruitSalad(sourceColorHct, false, 0.0)
        ColorSchemeType.EXPRESSIVE -> SchemeExpressive(sourceColorHct, false, 0.0)
    }
    
    // Get colors from tonal palettes at specific tones (similar to Seal's 80.a1, 90.a2, 60.a3)
    val color1 = Color(scheme.primaryPalette.tone(80))  // Primary color at 80 tone
    val color2 = Color(scheme.secondaryPalette.tone(90))  // Secondary color at 90 tone
    val color3 = Color(scheme.tertiaryPalette.tone(60))  // Tertiary color at 60 tone
    
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val onPrimaryContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
    val surfaceContainerHighestColor = MaterialTheme.colorScheme.surfaceContainerHighest

    Surface(
        modifier = modifier
            .padding(4.dp)
            .sizeIn(maxHeight = 80.dp, maxWidth = 80.dp, minHeight = 64.dp, minWidth = 64.dp)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        color = surfaceContainerHighestColor,
        onClick = onClick
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .drawBehind {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.minDimension / 2
                        
                        // Draw quadrant 1: 0-90 degrees (top-right) - Primary
                        val path1 = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(center.x, center.y - radius)
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    center.x - radius,
                                    center.y - radius,
                                    center.x + radius,
                                    center.y + radius
                                ),
                                startAngleDegrees = 270f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                            close()
                        }
                        drawPath(path1, color1)
                        
                        val path2 = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(center.x - radius, center.y)
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    center.x - radius,
                                    center.y - radius,
                                    center.x + radius,
                                    center.y + radius
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                            close()
                        }
                        drawPath(path2, color1)
                        
                        val path3 = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(center.x, center.y + radius)
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    center.x - radius,
                                    center.y - radius,
                                    center.x + radius,
                                    center.y + radius
                                ),
                                startAngleDegrees = 90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                            close()
                        }
                        drawPath(path3, color2)
                        
                        // Draw quadrant 4: 270-360 degrees (bottom-right) - Tertiary
                        val path4 = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(center.x + radius, center.y)
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    center.x - radius,
                                    center.y - radius,
                                    center.x + radius,
                                    center.y + radius
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                            close()
                        }
                        drawPath(path4, color3)
                    }
                    .align(Alignment.Center)
            ) {
                // Selection indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .size(containerSize)
                        .drawBehind {
                            drawCircle(primaryContainerColor)
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .align(Alignment.Center),
                        tint = onPrimaryContainerColor
                    )
                }
            }
        }
    }
}

