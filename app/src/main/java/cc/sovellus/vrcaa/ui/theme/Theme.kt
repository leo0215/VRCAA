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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                when (theme) {
                    0 -> dynamicLightColorScheme(context).let { scheme ->
                        if (primaryColor != null || secondaryColor != null) {
                            scheme.copy(
                                primary = primaryColor ?: scheme.primary,
                                secondary = secondaryColor ?: scheme.secondary
                            )
                        } else scheme
                    }
                    1 -> dynamicDarkColorScheme(context).let { scheme ->
                        if (primaryColor != null || secondaryColor != null) {
                            scheme.copy(
                                primary = primaryColor ?: scheme.primary,
                                secondary = secondaryColor ?: scheme.secondary
                            )
                        } else scheme
                    }
                    else -> {
                        if (isSystemInDarkTheme())
                            dynamicDarkColorScheme(context).let { scheme ->
                                if (primaryColor != null || secondaryColor != null) {
                                    scheme.copy(
                                        primary = primaryColor ?: scheme.primary,
                                        secondary = secondaryColor ?: scheme.secondary
                                    )
                                } else scheme
                            }
                        else
                            dynamicLightColorScheme(context).let { scheme ->
                                if (primaryColor != null || secondaryColor != null) {
                                    scheme.copy(
                                        primary = primaryColor ?: scheme.primary,
                                        secondary = secondaryColor ?: scheme.secondary
                                    )
                                } else scheme
                            }
                    }
                }
            }
            else -> {
                when (theme) {
                    0 -> expressiveLightColorScheme().let { scheme ->
                        if (primaryColor != null || secondaryColor != null) {
                            scheme.copy(
                                primary = primaryColor ?: scheme.primary,
                                secondary = secondaryColor ?: scheme.secondary
                            )
                        } else scheme
                    }
                    1 -> darkColorScheme().let { scheme ->
                        if (primaryColor != null || secondaryColor != null) {
                            scheme.copy(
                                primary = primaryColor ?: scheme.primary,
                                secondary = secondaryColor ?: scheme.secondary
                            )
                        } else scheme
                    }
                    else -> {
                        if (isSystemInDarkTheme())
                            darkColorScheme().let { scheme ->
                                if (primaryColor != null || secondaryColor != null) {
                                    scheme.copy(
                                        primary = primaryColor ?: scheme.primary,
                                        secondary = secondaryColor ?: scheme.secondary
                                    )
                                } else scheme
                            }
                        else
                            expressiveLightColorScheme().let { scheme ->
                                if (primaryColor != null || secondaryColor != null) {
                                    scheme.copy(
                                        primary = primaryColor ?: scheme.primary,
                                        secondary = secondaryColor ?: scheme.secondary
                                    )
                                } else scheme
                            }
                    }
                }
            }
        },
        typography = Typography(),
        content = content,
    )
}

val LocalTheme = compositionLocalOf { 2 }