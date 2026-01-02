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

import android.content.Context
import android.content.Context.MODE_PRIVATE
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.fontFamily
import cc.sovellus.vrcaa.extension.android16ColorSchema
import hct.Hct
import scheme.SchemeTonalSpot
import scheme.SchemeExpressive
import scheme.SchemeFruitSalad
import scheme.SchemeVibrant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(theme: Int, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val preferences = context.getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)
    Theme(theme, null, null, 0, preferences.fontFamily, preferences.android16ColorSchema, content)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(
    theme: Int, 
    primaryColor: Color? = null, 
    secondaryColor: Color? = null,
    schemeIndex: Int = 0,
    fontFamilyIndex: Int = 0,
    useAndroid16ColorSchema: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

   

    val customFontFamily = try {
        when (fontFamilyIndex) {
            1 -> {
               
                FontFamily(
                    Font(R.font.googlesans, FontWeight.Normal),
                    Font(R.font.googlesans, FontWeight.Medium),
                    Font(R.font.googlesans, FontWeight.SemiBold),
                    Font(R.font.googlesans, FontWeight.Bold)
                )
            }
            2 -> {
                
                FontFamily(
                    Font(R.font.googlesansflex, FontWeight.Normal),
                    Font(R.font.googlesansflex, FontWeight.Medium),
                    Font(R.font.googlesansflex, FontWeight.SemiBold),
                    Font(R.font.googlesansflex, FontWeight.Bold)
                )
            }
            3 -> {
               
                FontFamily(
                    Font(R.font.google_sans_rounded_regular, FontWeight.Normal),
                    Font(R.font.google_sans_rounded_regular, FontWeight.Medium),
                    Font(R.font.google_sans_rounded_regular, FontWeight.SemiBold),
                    Font(R.font.google_sans_rounded_regular, FontWeight.Bold)
                )
            }
            else -> {
               
                FontFamily.Default // System Default
            }
        }
    } catch (e: Exception) {
      
        FontFamily.Default
    }

   

    val typography = if (fontFamilyIndex != 0) {
        Typography().copy(
            displayLarge = Typography().displayLarge.copy(fontFamily = customFontFamily),
            displayMedium = Typography().displayMedium.copy(fontFamily = customFontFamily),
            displaySmall = Typography().displaySmall.copy(fontFamily = customFontFamily),
            headlineLarge = Typography().headlineLarge.copy(fontFamily = customFontFamily),
            headlineMedium = Typography().headlineMedium.copy(fontFamily = customFontFamily),
            headlineSmall = Typography().headlineSmall.copy(fontFamily = customFontFamily),
            titleLarge = Typography().titleLarge.copy(fontFamily = customFontFamily),
            titleMedium = Typography().titleMedium.copy(fontFamily = customFontFamily),
            titleSmall = Typography().titleSmall.copy(fontFamily = customFontFamily),
            bodyLarge = Typography().bodyLarge.copy(fontFamily = customFontFamily),
            bodyMedium = Typography().bodyMedium.copy(fontFamily = customFontFamily),
            bodySmall = Typography().bodySmall.copy(fontFamily = customFontFamily),
            labelLarge = Typography().labelLarge.copy(fontFamily = customFontFamily),
            labelMedium = Typography().labelMedium.copy(fontFamily = customFontFamily),
            labelSmall = Typography().labelSmall.copy(fontFamily = customFontFamily)
        )
    } else {
        Typography()
    }

    val isDark = when (theme) {
        1 -> true
        2 -> isSystemInDarkTheme()
        else -> false
    }

    val (baseColorScheme, surfaceBrightColor) = when {
            primaryColor != null -> {
                // Generate complete ColorScheme from seed color
                val result = colorSchemeFromSeed(primaryColor, isDark, schemeIndex)
                Pair(result.colorScheme, result.surfaceBright)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val scheme = when (theme) {
                    0 -> dynamicLightColorScheme(context)
                    1 -> dynamicDarkColorScheme(context)
                    else -> {
                        if (isSystemInDarkTheme())
                            dynamicDarkColorScheme(context)
                        else
                            dynamicLightColorScheme(context)
                    }
                }
                // For system dynamic colors, use surface as approximation for surfaceBright
                Pair(scheme, scheme.surface)
            }
            else -> {
                val scheme = when (theme) {
                    0 -> expressiveLightColorScheme()
                    1 -> darkColorScheme()
                    else -> {
                        if (isSystemInDarkTheme())
                            darkColorScheme()
                        else
                            expressiveLightColorScheme()
                    }
                }
                // For default colors, use surface as approximation for surfaceBright
                Pair(scheme, scheme.surface)
            }
        }
    
    // Android 16 Color Schema: background -> surfaceContainer, card -> surfaceBright
    val colorScheme = if (useAndroid16ColorSchema) {
        baseColorScheme.copy(
            background = baseColorScheme.surfaceContainer,
            surfaceContainerHighest = surfaceBrightColor
        )
    } else {
        baseColorScheme
    }

    val systemUiController = rememberSystemUiController()

    LaunchedEffect(colorScheme, isDark) {
        systemUiController.setSystemBarsColor(
            color = colorScheme.surface,
            darkIcons = !isDark
        )
        systemUiController.setNavigationBarColor(
            color = colorScheme.surface,
            darkIcons = !isDark
        )
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}

val LocalTheme = compositionLocalOf { 2 }

/**
 * Data class to hold ColorScheme and surfaceBright color
 */
private data class ColorSchemeWithSurfaceBright(
    val colorScheme: ColorScheme,
    val surfaceBright: Color
)

/**
 * Generate a ColorScheme from a seed color using Material Color Utilities
 * @param schemeIndex 0=TonalSpot, 1=Expressive, 2=FruitSalad, 3=Vibrant
 */
private fun colorSchemeFromSeed(seedColor: Color, isDark: Boolean, schemeIndex: Int = 0): ColorSchemeWithSurfaceBright {
    val sourceColorHct = Hct.fromInt(seedColor.toArgb())
    val scheme = when (schemeIndex) {
        0 -> SchemeTonalSpot(sourceColorHct, isDark, 0.0)
        1 -> SchemeExpressive(sourceColorHct, isDark, 0.0)
        2 -> SchemeFruitSalad(sourceColorHct, isDark, 0.0)
        3 -> SchemeVibrant(sourceColorHct, isDark, 0.0)
        else -> SchemeTonalSpot(sourceColorHct, isDark, 0.0) // Default fallback
    }
    
    val surfaceBrightColor = Color(scheme.surfaceBright)
    
    val colorScheme = if (isDark) {
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
    
    return ColorSchemeWithSurfaceBright(colorScheme, surfaceBrightColor)
}