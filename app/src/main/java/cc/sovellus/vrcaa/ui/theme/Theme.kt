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

import android.content.Context.MODE_PRIVATE
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.fontFamily
import cc.sovellus.vrcaa.extension.useLegacyMaterialTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(theme: Int, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val preferences = context.getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)
    Theme(theme, null, null, 0, preferences.fontFamily, preferences.useLegacyMaterialTheme, content)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(
    theme: Int, 
    primaryColor: Color? = null, 
    secondaryColor: Color? = null,
    schemeIndex: Int = 0,
    fontFamilyIndex: Int = 0,
    useLegacyMaterialTheme: Boolean = false,
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

    // Map schemeIndex to MaterialKolor PaletteStyle
    val paletteStyle = when (schemeIndex) {
        0 -> PaletteStyle.TonalSpot
        1 -> PaletteStyle.Expressive
        2 -> PaletteStyle.FruitSalad
        3 -> PaletteStyle.Vibrant
        else -> PaletteStyle.TonalSpot
    }

    // Use MaterialKolor when primaryColor is provided, otherwise use system/default colors
    val specVersion = if (useLegacyMaterialTheme) {
        ColorSpec.SpecVersion.SPEC_2025
    } else {
        ColorSpec.SpecVersion.SPEC_2021
    }
    
    val dynamicThemeState = if (primaryColor != null) {
        rememberDynamicMaterialThemeState(
            isDark = isDark,
            style = paletteStyle,
            specVersion = specVersion,
            seedColor = primaryColor
        )
    } else {
        null
    }
    
    val systemUiController = rememberSystemUiController()
    val colorScheme = dynamicThemeState?.colorScheme ?: run {
        when {
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
    }
    
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
    
    // Always use MaterialExpressiveTheme to avoid tree recreation when switching themes
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}

val LocalTheme = compositionLocalOf { 2 }