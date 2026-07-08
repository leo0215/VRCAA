package cc.sovellus.vrcaa.base

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.colorContrastLevel
import cc.sovellus.vrcaa.extension.colorSchemeIndex
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.fontFamily
import cc.sovellus.vrcaa.extension.primaryColorOverride
import cc.sovellus.vrcaa.extension.useLegacyMaterialTheme
import cc.sovellus.vrcaa.extension.useSystemColorTheme
import cc.sovellus.vrcaa.manager.ThemeManager
import cc.sovellus.vrcaa.ui.theme.LocalTheme
import cc.sovellus.vrcaa.ui.theme.Theme
import cc.sovellus.vrcaa.ui.theme.appBackground

open class BaseActivity : ComponentActivity(), ThemeManager.ThemeListener {

    private val currentTheme = mutableIntStateOf(-1)
    val preferences: SharedPreferences = App.getPreferences()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        currentTheme.intValue = preferences.currentThemeOption
        ThemeManager.addListener(this)

        setContent {
            CompositionLocalProvider(LocalTheme provides currentTheme.intValue) {
                var useSystemColor by remember {
                    mutableStateOf(preferences.useSystemColorTheme)
                }
                var currentPrimary by remember {
                    mutableStateOf<Color?>(
                        preferences.primaryColorOverride.takeIf { it != -1 }?.let { Color(it) }
                    )
                }
                var currentSchemeIndex by remember {
                    mutableIntStateOf(preferences.colorSchemeIndex)
                }
                var currentFontFamily by remember {
                    mutableIntStateOf(preferences.fontFamily)
                }
                var useLegacyMaterialTheme by remember {
                    mutableStateOf(preferences.useLegacyMaterialTheme)
                }
                var currentContrastLevel by remember {
                    mutableStateOf(preferences.colorContrastLevel)
                }

                DisposableEffect(Unit) {
                    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        when (key) {
                            "useSystemColorTheme" -> {
                                useSystemColor = preferences.useSystemColorTheme
                            }
                            "primaryColorOverride" -> {
                                currentPrimary = preferences.primaryColorOverride.takeIf { it != -1 }?.let { Color(it) }
                            }
                            "colorSchemeIndex" -> {
                                currentSchemeIndex = preferences.colorSchemeIndex
                            }
                            "fontFamily" -> {
                                currentFontFamily = preferences.fontFamily
                            }
                            "useLegacyMaterialTheme" -> {
                                useLegacyMaterialTheme = preferences.useLegacyMaterialTheme
                            }
                            "colorContrastLevel" -> {
                                currentContrastLevel = preferences.colorContrastLevel
                            }
                        }
                    }
                    preferences.registerOnSharedPreferenceChangeListener(listener)
                    onDispose {
                        preferences.unregisterOnSharedPreferenceChangeListener(listener)
                    }
                }

                val effectivePrimary = if (useSystemColor) null else currentPrimary

                Theme(
                    theme = LocalTheme.current,
                    primaryColor = effectivePrimary,
                    schemeIndex = currentSchemeIndex,
                    fontFamilyIndex = currentFontFamily,
                    useLegacyMaterialTheme = useLegacyMaterialTheme,
                    contrastLevel = currentContrastLevel.toDouble(),
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.appBackground
                    ) {
                        Content(savedInstanceState)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        ThemeManager.removeListener(this)
        super.onDestroy()
    }

    override fun onPreferenceUpdate(theme: Int) {
        currentTheme.intValue = theme
    }

    @Composable
    open fun Content(bundle: Bundle?) {
        throw RuntimeException("Did you forgot to override Content?")
    }
}
