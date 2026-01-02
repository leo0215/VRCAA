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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.columnCountOption
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.fixedColumnSize
import cc.sovellus.vrcaa.extension.minimalistMode
import cc.sovellus.vrcaa.extension.colorSchemeIndex
import cc.sovellus.vrcaa.extension.primaryColorOverride
import cc.sovellus.vrcaa.extension.secondaryColorOverride
import cc.sovellus.vrcaa.extension.useSystemColorTheme
import cc.sovellus.vrcaa.extension.fontFamily
import cc.sovellus.vrcaa.extension.android16ColorSchema

class ThemeScreenModel : ScreenModel {
    val preferences: SharedPreferences = App.getContext().getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)
    val minimalistMode = mutableStateOf(preferences.minimalistMode)
    val useSystemColorTheme = mutableStateOf(preferences.useSystemColorTheme)
    val android16ColorSchema = mutableStateOf(preferences.android16ColorSchema)
    var currentIndex = mutableIntStateOf(preferences.currentThemeOption)
    var currentColumnIndex = mutableIntStateOf(preferences.columnCountOption)
    var currentColumnAmount = mutableFloatStateOf(preferences.fixedColumnSize.toFloat())
    var currentFontFamily = mutableIntStateOf(preferences.fontFamily)
    
    val primaryColor: Color?
        get() = preferences.primaryColorOverride.takeIf { it != -1 }?.let { Color(it) }
    
    val colorSchemeIndex: Int
        get() = preferences.colorSchemeIndex
    
    fun setPrimaryColor(color: Color, schemeIndex: Int = 0) {
        preferences.primaryColorOverride = color.toArgb()
        preferences.colorSchemeIndex = schemeIndex
        // Notify theme change - you may need to add a listener here
    }
    
    fun setUseSystemColorTheme(enabled: Boolean) {
        preferences.useSystemColorTheme = enabled
        useSystemColorTheme.value = enabled
    }
    
    fun setAndroid16ColorSchema(enabled: Boolean) {
        preferences.android16ColorSchema = enabled
        android16ColorSchema.value = enabled
    }
}
