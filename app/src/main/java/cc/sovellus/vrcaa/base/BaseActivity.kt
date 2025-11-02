package cc.sovellus.vrcaa.base

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.primaryColorOverride
import cc.sovellus.vrcaa.extension.secondaryColorOverride
import cc.sovellus.vrcaa.extension.useSystemColorTheme
import cc.sovellus.vrcaa.manager.ThemeManager
import cc.sovellus.vrcaa.ui.theme.LocalTheme
import cc.sovellus.vrcaa.ui.theme.Theme

open class BaseActivity : ComponentActivity(), ThemeManager.ThemeListener {

    private val currentTheme = mutableIntStateOf(-1)
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferences = getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)
        currentTheme.intValue = preferences.currentThemeOption

        // Register as theme listener
        ThemeManager.addListener(this)

        setContent {
            CompositionLocalProvider(LocalTheme provides currentTheme.intValue) {
                // Track whether to use system color theme
                var useSystemColor by remember { 
                    mutableStateOf(preferences.useSystemColorTheme)
                }
                
                // Use remember to track color overrides and update when preferences change
                var currentPrimary by remember { 
                    mutableStateOf<Color?>(
                        preferences.primaryColorOverride.takeIf { it != -1 }?.let { Color(it) }
                    )
                }
                var currentSecondary by remember { 
                    mutableStateOf<Color?>(
                        preferences.secondaryColorOverride.takeIf { it != -1 }?.let { Color(it) }
                    )
                }
                
                // Update colors when preferences change
                DisposableEffect(Unit) {
                    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        when (key) {
                            "useSystemColorTheme" -> {
                                useSystemColor = preferences.useSystemColorTheme
                            }
                            "primaryColorOverride" -> {
                                currentPrimary = preferences.primaryColorOverride.takeIf { it != -1 }?.let { Color(it) }
                            }
                            "secondaryColorOverride" -> {
                                currentSecondary = preferences.secondaryColorOverride.takeIf { it != -1 }?.let { Color(it) }
                            }
                        }
                    }
                    preferences.registerOnSharedPreferenceChangeListener(listener)
                    onDispose {
                        preferences.unregisterOnSharedPreferenceChangeListener(listener)
                    }
                }
                
                // Use system color theme if enabled, otherwise use custom colors
                val effectivePrimary = if (useSystemColor) null else currentPrimary
                val effectiveSecondary = if (useSystemColor) null else currentSecondary
                
                Theme(LocalTheme.current, effectivePrimary, effectiveSecondary) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Content(savedInstanceState)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister theme listener
        ThemeManager.removeListener(this)
    }

    override fun onPreferenceUpdate(theme: Int) {
        currentTheme.intValue = theme
    }

    @Composable
    open fun Content(bundle: Bundle?) {
        throw RuntimeException("Did you forgot to override Content?")
    }
}