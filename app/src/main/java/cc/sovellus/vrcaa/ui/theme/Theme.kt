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
    Theme(theme, null, null, 0, preferences.fontFamily, content)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(
    theme: Int, 
    primaryColor: Color? = null, 
    secondaryColor: Color? = null,
    schemeIndex: Int = 0,
    fontFamilyIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // #region agent log
    try {
        val logFile = java.io.File("d:\\Doc\\workspace\\VRCAA\\.cursor\\debug.log")
        val logEntry = """{"sessionId":"debug-session","runId":"run1","hypothesisId":"A","location":"Theme.kt:67","message":"Theme composable executed","data":{"fontFamilyIndex":$fontFamilyIndex},"timestamp":${System.currentTimeMillis()}}""" + "\n"
        logFile.appendText(logEntry)
    } catch (e: Exception) {}
    // #endregion

    val customFontFamily = try {
        when (fontFamilyIndex) {
            1 -> {
                // #region agent log
                try {
                    val logFile = java.io.File("d:\\Doc\\workspace\\VRCAA\\.cursor\\debug.log")
                    val logEntry = """{"sessionId":"debug-session","runId":"run1","hypothesisId":"B","location":"Theme.kt:78","message":"Loading Google Sans font","data":{"fontFamilyIndex":$fontFamilyIndex},"timestamp":${System.currentTimeMillis()}}""" + "\n"
                    logFile.appendText(logEntry)
                } catch (e: Exception) {}
                // #endregion
                FontFamily(
                    Font(R.font.googlesans, FontWeight.Normal),
                    Font(R.font.googlesans, FontWeight.Medium),
                    Font(R.font.googlesans, FontWeight.SemiBold),
                    Font(R.font.googlesans, FontWeight.Bold)
                )
            }
            2 -> {
                // #region agent log
                try {
                    val logFile = java.io.File("d:\\Doc\\workspace\\VRCAA\\.cursor\\debug.log")
                    val logEntry = """{"sessionId":"debug-session","runId":"run1","hypothesisId":"B","location":"Theme.kt:90","message":"Loading Google Sans Flex font","data":{"fontFamilyIndex":$fontFamilyIndex},"timestamp":${System.currentTimeMillis()}}""" + "\n"
                    logFile.appendText(logEntry)
                } catch (e: Exception) {}
                // #endregion
                FontFamily(
                    Font(R.font.googlesansflex, FontWeight.Normal),
                    Font(R.font.googlesansflex, FontWeight.Medium),
                    Font(R.font.googlesansflex, FontWeight.SemiBold),
                    Font(R.font.googlesansflex, FontWeight.Bold)
                )
            }
            3 -> {
                // #region agent log
                try {
                    val logFile = java.io.File("d:\\Doc\\workspace\\VRCAA\\.cursor\\debug.log")
                    val logEntry = """{"sessionId":"debug-session","runId":"run1","hypothesisId":"B","location":"Theme.kt:109","message":"Loading Google Sans Rounded font","data":{"fontFamilyIndex":$fontFamilyIndex},"timestamp":${System.currentTimeMillis()}}""" + "\n"
                    logFile.appendText(logEntry)
                } catch (e: Exception) {}
                // #endregion
                FontFamily(
                    Font(R.font.google_sans_rounded_regular, FontWeight.Normal),
                    Font(R.font.google_sans_rounded_regular, FontWeight.Medium),
                    Font(R.font.google_sans_rounded_regular, FontWeight.SemiBold),
                    Font(R.font.google_sans_rounded_regular, FontWeight.Bold)
                )
            }
            else -> {
                // #region agent log
                try {
                    val logFile = java.io.File("d:\\Doc\\workspace\\VRCAA\\.cursor\\debug.log")
                    val logEntry = """{"sessionId":"debug-session","runId":"run1","hypothesisId":"B","location":"Theme.kt:102","message":"Using system default font","data":{"fontFamilyIndex":$fontFamilyIndex},"timestamp":${System.currentTimeMillis()}}""" + "\n"
                    logFile.appendText(logEntry)
                } catch (e: Exception) {}
                // #endregion
                FontFamily.Default // System Default
            }
        }
    } catch (e: Exception) {
        // #region agent log
        try {
            val logFile = java.io.File("d:\\Doc\\workspace\\VRCAA\\.cursor\\debug.log")
            val logEntry = """{"sessionId":"debug-session","runId":"run1","hypothesisId":"B","location":"Theme.kt:107","message":"Font loading failed","data":{"fontFamilyIndex":$fontFamilyIndex,"error":"${e.message}"},"timestamp":${System.currentTimeMillis()}}""" + "\n"
            logFile.appendText(logEntry)
        } catch (ex: Exception) {}
        // #endregion
        FontFamily.Default
    }

    // #region agent log
    try {
        val logFile = java.io.File("d:\\Doc\\workspace\\VRCAA\\.cursor\\debug.log")
        val logEntry = """{"sessionId":"debug-session","runId":"run1","hypothesisId":"D","location":"Theme.kt:85","message":"Creating typography","data":{"fontFamilyIndex":$fontFamilyIndex,"willUseCustomFont":${fontFamilyIndex != 0}},"timestamp":${System.currentTimeMillis()}}""" + "\n"
        logFile.appendText(logEntry)
    } catch (e: Exception) {}
    // #endregion

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

    val colorScheme = when {
            primaryColor != null -> {
                // Generate complete ColorScheme from seed color
                colorSchemeFromSeed(primaryColor, isDark, schemeIndex)
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
 * Generate a ColorScheme from a seed color using Material Color Utilities
 * @param schemeIndex 0=TonalSpot, 1=Expressive, 2=FruitSalad, 3=Vibrant
 */
private fun colorSchemeFromSeed(seedColor: Color, isDark: Boolean, schemeIndex: Int = 0): ColorScheme {
    val sourceColorHct = Hct.fromInt(seedColor.toArgb())
    val scheme = when (schemeIndex) {
        0 -> SchemeTonalSpot(sourceColorHct, isDark, 0.0)
        1 -> SchemeExpressive(sourceColorHct, isDark, 0.0)
        2 -> SchemeFruitSalad(sourceColorHct, isDark, 0.0)
        3 -> SchemeVibrant(sourceColorHct, isDark, 0.0)
        else -> SchemeTonalSpot(sourceColorHct, isDark, 0.0) // Default fallback
    }
    
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