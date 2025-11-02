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

package cc.sovellus.vrcaa.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import hct.Hct
import scheme.SchemeTonalSpot

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(theme: Int, content: @Composable () -> Unit) {
    Theme(theme, null, null, content)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(
    theme: Int, 
    primaryColor: Color? = null, 
    secondaryColor: Color? = null, 
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    MaterialExpressiveTheme(
        colorScheme = when {
            primaryColor != null -> {
                // Generate complete ColorScheme from seed color
                val isDark = when (theme) {
                    1 -> true
                    2 -> isSystemInDarkTheme()
                    else -> false
                }
                colorSchemeFromSeed(primaryColor, isDark)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                when (theme) {
                    0 -> dynamicLightColorScheme(context)
                    1 -> dynamicDarkColorScheme(context)
                    else -> {
                        if (isSystemInDarkTheme())
                            dynamicDarkColorScheme(context)
                        else
                            dynamicLightColorScheme(context)
                    }
                }
            }
            else -> {
                when (theme) {
                    0 -> expressiveLightColorScheme()
                    1 -> darkColorScheme()
                    else -> {
                        if (isSystemInDarkTheme())
                            darkColorScheme()
                        else
                            expressiveLightColorScheme()
                    }
                }
            }
        },
        typography = Typography(),
        content = content,
    )
}

val LocalTheme = compositionLocalOf { 2 }

/**
 * Generate a ColorScheme from a seed color using Material Color Utilities
 */
private fun colorSchemeFromSeed(seedColor: Color, isDark: Boolean): ColorScheme {
    val sourceColorHct = Hct.fromInt(seedColor.toArgb())
    val scheme = SchemeTonalSpot(sourceColorHct, isDark, 0.0)
    
    return if (isDark) {
        darkColorScheme(
            primary = Color(scheme.primary),
            onPrimary = Color(scheme.onPrimary),
            primaryContainer = Color(scheme.primaryContainer),
            onPrimaryContainer = Color(scheme.onPrimaryContainer),
            secondary = Color(scheme.secondary),
            onSecondary = Color(scheme.onSecondary),
            secondaryContainer = Color(scheme.secondaryContainer),
            onSecondaryContainer = Color(scheme.onSecondaryContainer),
            tertiary = Color(scheme.tertiary),
            onTertiary = Color(scheme.onTertiary),
            tertiaryContainer = Color(scheme.tertiaryContainer),
            onTertiaryContainer = Color(scheme.onTertiaryContainer),
            error = Color(scheme.error),
            onError = Color(scheme.onError),
            errorContainer = Color(scheme.errorContainer),
            onErrorContainer = Color(scheme.onErrorContainer),
            background = Color(scheme.background),
            onBackground = Color(scheme.onBackground),
            surface = Color(scheme.surface),
            onSurface = Color(scheme.onSurface),
            surfaceVariant = Color(scheme.surfaceVariant),
            onSurfaceVariant = Color(scheme.onSurfaceVariant),
            outline = Color(scheme.outline),
            outlineVariant = Color(scheme.outlineVariant),
            inverseSurface = Color(scheme.inverseSurface),
            inverseOnSurface = Color(scheme.inverseOnSurface),
            inversePrimary = Color(scheme.inversePrimary),
            surfaceContainerLowest = Color(scheme.surfaceContainerLowest),
            surfaceContainerLow = Color(scheme.surfaceContainerLow),
            surfaceContainer = Color(scheme.surfaceContainer),
            surfaceContainerHigh = Color(scheme.surfaceContainerHigh),
            surfaceContainerHighest = Color(scheme.surfaceContainerHighest),
        )
    } else {
        lightColorScheme(
            primary = Color(scheme.primary),
            onPrimary = Color(scheme.onPrimary),
            primaryContainer = Color(scheme.primaryContainer),
            onPrimaryContainer = Color(scheme.onPrimaryContainer),
            secondary = Color(scheme.secondary),
            onSecondary = Color(scheme.onSecondary),
            secondaryContainer = Color(scheme.secondaryContainer),
            onSecondaryContainer = Color(scheme.onSecondaryContainer),
            tertiary = Color(scheme.tertiary),
            onTertiary = Color(scheme.onTertiary),
            tertiaryContainer = Color(scheme.tertiaryContainer),
            onTertiaryContainer = Color(scheme.onTertiaryContainer),
            error = Color(scheme.error),
            onError = Color(scheme.onError),
            errorContainer = Color(scheme.errorContainer),
            onErrorContainer = Color(scheme.onErrorContainer),
            background = Color(scheme.background),
            onBackground = Color(scheme.onBackground),
            surface = Color(scheme.surface),
            onSurface = Color(scheme.onSurface),
            surfaceVariant = Color(scheme.surfaceVariant),
            onSurfaceVariant = Color(scheme.onSurfaceVariant),
            outline = Color(scheme.outline),
            outlineVariant = Color(scheme.outlineVariant),
            inverseSurface = Color(scheme.inverseSurface),
            inverseOnSurface = Color(scheme.inverseOnSurface),
            inversePrimary = Color(scheme.inversePrimary),
            surfaceContainerLowest = Color(scheme.surfaceContainerLowest),
            surfaceContainerLow = Color(scheme.surfaceContainerLow),
            surfaceContainer = Color(scheme.surfaceContainer),
            surfaceContainerHigh = Color(scheme.surfaceContainerHigh),
            surfaceContainerHighest = Color(scheme.surfaceContainerHighest),
        )
    }
}