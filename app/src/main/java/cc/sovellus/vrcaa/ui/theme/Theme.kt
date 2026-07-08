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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.colorContrastLevel
import cc.sovellus.vrcaa.extension.colorSchemeIndex
import cc.sovellus.vrcaa.extension.fontFamily
import cc.sovellus.vrcaa.extension.primaryColorOverride
import cc.sovellus.vrcaa.extension.useLegacyMaterialTheme
import cc.sovellus.vrcaa.extension.useSystemColorTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme
import com.materialkolor.dynamiccolor.ColorSpec

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(theme: Int, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val preferences = context.getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)
    val primaryColor = if (preferences.useSystemColorTheme) {
        null
    } else {
        preferences.primaryColorOverride.takeIf { it != -1 }?.let { Color(it) }
    }
    Theme(
        theme = theme,
        primaryColor = primaryColor,
        schemeIndex = preferences.colorSchemeIndex,
        fontFamilyIndex = preferences.fontFamily,
        useLegacyMaterialTheme = preferences.useLegacyMaterialTheme,
        contrastLevel = preferences.colorContrastLevel.toDouble(),
        content = content,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(
    theme: Int,
    primaryColor: Color? = null,
    schemeIndex: Int = 0,
    fontFamilyIndex: Int = 0,
    useLegacyMaterialTheme: Boolean = false,
    contrastLevel: Double = 0.0,
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
                FontFamily.Default
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

    val systemUiController = rememberSystemUiController()
    val colorScheme = buildColorScheme(
        context = context,
        theme = theme,
        isDark = isDark,
        primaryColor = primaryColor,
        schemeIndex = schemeIndex,
        useLegacyMaterialTheme = useLegacyMaterialTheme,
        contrastLevel = contrastLevel,
    )

    LaunchedEffect(colorScheme, isDark) {
        systemUiController.setSystemBarsColor(
            color = colorScheme.appBackground,
            darkIcons = !isDark
        )
        systemUiController.setNavigationBarColor(
            color = colorScheme.navBarBackground,
            darkIcons = !isDark
        )
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}

private fun systemBaseColorScheme(context: Context, theme: Int, isDark: Boolean): ColorScheme {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            when (theme) {
                0 -> dynamicLightColorScheme(context)
                1 -> dynamicDarkColorScheme(context)
                else -> if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
        }
        else -> {
            when (theme) {
                0 -> expressiveLightColorScheme()
                1 -> darkColorScheme()
                else -> if (isDark) darkColorScheme() else expressiveLightColorScheme()
            }
        }
    }
}

private fun buildColorScheme(
    context: Context,
    theme: Int,
    isDark: Boolean,
    primaryColor: Color?,
    schemeIndex: Int,
    useLegacyMaterialTheme: Boolean,
    contrastLevel: Double,
): ColorScheme {
    val paletteStyle = when (schemeIndex) {
        0 -> PaletteStyle.TonalSpot
        1 -> PaletteStyle.Expressive
        2 -> PaletteStyle.FruitSalad
        3 -> PaletteStyle.Vibrant
        else -> PaletteStyle.TonalSpot
    }
    val specVersion = if (useLegacyMaterialTheme) {
        ColorSpec.SpecVersion.SPEC_2025
    } else {
        ColorSpec.SpecVersion.SPEC_2021
    }

    if (primaryColor != null) {
        return dynamicColorScheme(
            seedColor = primaryColor,
            isDark = isDark,
            style = paletteStyle,
            specVersion = specVersion,
            contrastLevel = contrastLevel,
        )
    }

    val base = systemBaseColorScheme(context, theme, isDark)
    if (contrastLevel == 0.0) return base

    return dynamicColorScheme(
        seedColor = base.primary,
        isDark = isDark,
        primary = base.primary,
        secondary = base.secondary,
        tertiary = base.tertiary,
        style = PaletteStyle.TonalSpot,
        contrastLevel = contrastLevel,
    )
}

val LocalTheme = compositionLocalOf { 2 }

/** App page / scaffold background */
val ColorScheme.appBackground: Color get() = surfaceContainer

/** Bottom navigation bar and system gesture bar */
val ColorScheme.navBarBackground: Color get() = surfaceContainerHigh

/** List and segmented card rows — light & dark: surfaceBright */
val ColorScheme.listCardBackground: Color get() = surfaceBright

/** Default Card background — light: surfaceContainer (M3 default), dark: surfaceBright */
val ColorScheme.defaultCardBackground: Color
    get() = if (surface.luminance() > 0.5f) surfaceContainer else surfaceBright
