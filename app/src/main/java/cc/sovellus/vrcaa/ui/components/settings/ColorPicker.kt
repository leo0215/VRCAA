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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.controls.connectedButtonGroupToggleColors
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.SchemeExpressive
import com.materialkolor.scheme.SchemeFruitSalad
import com.materialkolor.scheme.SchemeTonalSpot
import com.materialkolor.scheme.SchemeVibrant

// Color list using HCT color space (same as Seal)
private val ColorPalette = ((4..10) + (1..3)).map { it * 35.0 }.map { Color(Hct.from(it, 40.0, 40.0).toInt()) }

// Color scheme types (4 styles per color)
private enum class ColorSchemeType(val labelRes: Int) {
    TONAL_SPOT(R.string.theme_page_color_scheme_tonal),
    EXPRESSIVE(R.string.theme_page_color_scheme_expressive),
    FRUIT_SALAD(R.string.theme_page_color_scheme_fruit),
    VIBRANT(R.string.theme_page_color_scheme_vibrant)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColorPicker(
    selectedColor: Color?,
    selectedSchemeIndex: Int = 0,
    onColorSelected: (Color, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        )
    ) {
        ColorPickerContent(
            selectedColor = selectedColor,
            selectedSchemeIndex = selectedSchemeIndex,
            onColorSelected = onColorSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColorPickerContent(
    selectedColor: Color?,
    selectedSchemeIndex: Int = 0,
    onColorSelected: (Color, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val schemeOptions = ColorSchemeType.values()
    val schemeLabels = schemeOptions.map { stringResource(it.labelRes) }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        // Scheme style: Connected ToggleButton group (Expressive pattern)
        Text(
            text = stringResource(R.string.theme_page_color_palette_style),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
        ) {
            schemeOptions.forEachIndexed { index, _ ->
                ToggleButton(
                    checked = selectedSchemeIndex == index,
                    onCheckedChange = {
                        val currentColor = selectedColor ?: ColorPalette.first()
                        onColorSelected(currentColor, index)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { role = Role.RadioButton },
                    shapes = when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        schemeOptions.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
                    colors = connectedButtonGroupToggleColors(),
                ) {
                    Text(
                        text = schemeLabels[index],
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Color swatches: Horizontal scroll of circular chips
        Text(
            text = stringResource(R.string.theme_page_color_seed_colors),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        val scrollState = rememberScrollState()
        val chipSize = 56.dp
        val chipSpacing = 12.dp

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(chipSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorPalette.forEach { color ->
                val isSelected = selectedColor?.toArgb() == color.toArgb()
                ColorSchemeChip(
                    seedColor = color,
                    schemeIndex = selectedSchemeIndex,
                    isSelected = isSelected,
                    onClick = { onColorSelected(color, selectedSchemeIndex) },
                    modifier = Modifier.size(chipSize)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorSchemeChip(
    seedColor: Color,
    schemeIndex: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp,
        label = "borderWidth"
    )
    val outlineColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "outlineColor"
    )

    val sourceColorHct = Hct.fromInt(seedColor.toArgb())
    val scheme = when (ColorSchemeType.values()[schemeIndex]) {
        ColorSchemeType.TONAL_SPOT -> SchemeTonalSpot(sourceColorHct, false, 0.0)
        ColorSchemeType.VIBRANT -> SchemeVibrant(sourceColorHct, false, 0.0)
        ColorSchemeType.FRUIT_SALAD -> SchemeFruitSalad(sourceColorHct, false, 0.0)
        ColorSchemeType.EXPRESSIVE -> SchemeExpressive(sourceColorHct, false, 0.0)
    }
    val color1 = Color(scheme.primaryPalette.tone(80))
    val color2 = Color(scheme.secondaryPalette.tone(90))
    val color3 = Color(scheme.tertiaryPalette.tone(60))
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val onPrimaryContainerColor = MaterialTheme.colorScheme.onPrimaryContainer

    Surface(
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(borderWidth, outlineColor, CircleShape)
                } else Modifier
            ),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceBright,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .drawBehind {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2 - 2

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
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                        .drawBehind {
                            drawCircle(primaryContainerColor)
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.Center),
                        tint = onPrimaryContainerColor
                    )
                }
            }
        }
    }
}
