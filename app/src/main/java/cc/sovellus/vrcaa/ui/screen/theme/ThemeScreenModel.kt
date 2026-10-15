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

package cc.sovellus.vrcaa.ui.screen.theme

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.columnCountOption
import cc.sovellus.vrcaa.extension.colorContrastLevel
import cc.sovellus.vrcaa.extension.colorSchemeIndex
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.customFontName
import cc.sovellus.vrcaa.extension.customFontPath
import cc.sovellus.vrcaa.extension.fixedColumnSize
import cc.sovellus.vrcaa.extension.fontFamily
import cc.sovellus.vrcaa.extension.minimalistMode
import cc.sovellus.vrcaa.extension.primaryColorOverride
import cc.sovellus.vrcaa.extension.useLegacyMaterialTheme
import cc.sovellus.vrcaa.extension.useSystemColorTheme
import cc.sovellus.vrcaa.helper.FontHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThemeScreenModel : ScreenModel {
    val preferences: SharedPreferences = App.getContext().getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)
    val minimalistMode = mutableStateOf(preferences.minimalistMode)
    val useSystemColorTheme = mutableStateOf(preferences.useSystemColorTheme)
    val useLegacyMaterialTheme = mutableStateOf(preferences.useLegacyMaterialTheme)
    var currentIndex = mutableIntStateOf(preferences.currentThemeOption)
    var currentColumnIndex = mutableIntStateOf(preferences.columnCountOption)
    var currentColumnAmount = mutableFloatStateOf(preferences.fixedColumnSize.toFloat())
    var currentFontFamily = mutableIntStateOf(preferences.fontFamily)
    var contrastLevel = mutableFloatStateOf(preferences.colorContrastLevel)

    /** Name of the imported custom font, or null when none is imported. */
    val customFontName = mutableStateOf(
        preferences.customFontName.takeIf { preferences.customFontPath.isNotEmpty() }
    )
    private var isImportingFont = false

    val primaryColor: Color?
        get() = preferences.primaryColorOverride.takeIf { it != -1 }?.let { Color(it) }

    val colorSchemeIndex: Int
        get() = preferences.colorSchemeIndex

    fun setPrimaryColor(color: Color, schemeIndex: Int = 0) {
        preferences.primaryColorOverride = color.toArgb()
        preferences.colorSchemeIndex = schemeIndex
    }

    fun setUseSystemColorTheme(enabled: Boolean) {
        preferences.useSystemColorTheme = enabled
        useSystemColorTheme.value = enabled
    }

    fun setUseLegacyMaterialTheme(enabled: Boolean) {
        preferences.useLegacyMaterialTheme = enabled
        useLegacyMaterialTheme.value = enabled
    }

    fun setContrastLevel(value: Float) {
        preferences.colorContrastLevel = value
        contrastLevel.floatValue = value
    }

    fun selectFont(fontIndex: Int) {
        currentFontFamily.intValue = fontIndex
        preferences.fontFamily = fontIndex
    }

    /** Imports the font at [uri] and selects it on success. [onResult] is called on the main thread. */
    fun importCustomFont(uri: Uri, onResult: (FontHelper.ImportResult) -> Unit) {
        if (isImportingFont) return
        isImportingFont = true

        screenModelScope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) {
                    FontHelper.importFont(App.getContext(), uri, preferences)
                }
                if (result is FontHelper.ImportResult.Success) {
                    customFontName.value = result.name
                    currentFontFamily.intValue = FontHelper.FONT_CUSTOM
                }
                onResult(result)
            } finally {
                isImportingFont = false
            }
        }
    }

    /** Deletes the imported font; switches back to the system default if it was selected. */
    fun removeCustomFont() {
        FontHelper.removeCustomFont(App.getContext(), preferences)
        customFontName.value = null
        currentFontFamily.intValue = preferences.fontFamily
    }
}
