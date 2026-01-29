/*
 * Custom Dynamic Material Theme that uses our modified ColorSpec classes
 */
package cc.sovellus.vrcaa.ui.mcu

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.DynamicScheme
import com.materialkolor.scheme.SchemeContent
import com.materialkolor.scheme.SchemeExpressive
import com.materialkolor.scheme.SchemeFidelity
import com.materialkolor.scheme.SchemeFruitSalad
import com.materialkolor.scheme.SchemeMonochrome
import com.materialkolor.scheme.SchemeNeutral
import com.materialkolor.scheme.SchemeRainbow
import com.materialkolor.scheme.SchemeTonalSpot
import com.materialkolor.scheme.SchemeVibrant

/**
 * Remember a [DynamicMaterialThemeState] that uses our custom ColorSpec classes.
 */
@Composable
fun rememberCustomDynamicMaterialThemeState(
    seedColor: Color,
    isDark: Boolean,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
    specVersion: ColorSpec.SpecVersion = ColorSpec.SpecVersion.SPEC_2021,
): DynamicMaterialThemeState {
    return remember(seedColor, isDark, style, contrastLevel, specVersion) {
        DynamicMaterialThemeState(
            seedColor = seedColor,
            isDark = isDark,
            style = style,
            contrastLevel = contrastLevel,
            specVersion = specVersion,
        )
    }
}

/**
 * State holder for dynamic material theme using custom ColorSpec.
 */
@Stable
class DynamicMaterialThemeState(
    seedColor: Color,
    isDark: Boolean,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
    specVersion: ColorSpec.SpecVersion = ColorSpec.SpecVersion.SPEC_2021,
) {
    var seedColor by mutableStateOf(seedColor)
    var isDark by mutableStateOf(isDark)
    var style by mutableStateOf(style)
    var contrastLevel by mutableStateOf(contrastLevel)
    var specVersion by mutableStateOf(specVersion)

    val colorScheme: ColorScheme
        get() = createColorScheme(
            seedColor = seedColor,
            isDark = isDark,
            style = style,
            contrastLevel = contrastLevel,
            specVersion = specVersion,
        )
}

/**
 * Create a ColorScheme using our custom DynamicScheme classes.
 */
private fun createColorScheme(
    seedColor: Color,
    isDark: Boolean,
    style: PaletteStyle,
    contrastLevel: Double,
    specVersion: ColorSpec.SpecVersion,
): ColorScheme {
    val hct = Hct.fromInt(seedColor.toArgb())
    
    // Create DynamicScheme using our custom classes (which use our ColorSpec2025)
    val scheme: DynamicScheme = when (style) {
        PaletteStyle.TonalSpot -> SchemeTonalSpot(hct, isDark, contrastLevel, specVersion)
        PaletteStyle.Neutral -> SchemeNeutral(hct, isDark, contrastLevel, specVersion)
        PaletteStyle.Vibrant -> SchemeVibrant(hct, isDark, contrastLevel, specVersion)
        PaletteStyle.Expressive -> SchemeExpressive(hct, isDark, contrastLevel, specVersion)
        PaletteStyle.Rainbow -> SchemeRainbow(hct, isDark, contrastLevel, specVersion)
        PaletteStyle.FruitSalad -> SchemeFruitSalad(hct, isDark, contrastLevel, specVersion)
        PaletteStyle.Monochrome -> SchemeMonochrome(hct, isDark, contrastLevel, specVersion)
        PaletteStyle.Fidelity -> SchemeFidelity(hct, isDark, contrastLevel, specVersion)
        PaletteStyle.Content -> SchemeContent(hct, isDark, contrastLevel, specVersion)
    }

    return ColorScheme(
        primary = Color(scheme.primary),
        onPrimary = Color(scheme.onPrimary),
        primaryContainer = Color(scheme.primaryContainer),
        onPrimaryContainer = Color(scheme.onPrimaryContainer),
        inversePrimary = Color(scheme.inversePrimary),
        secondary = Color(scheme.secondary),
        onSecondary = Color(scheme.onSecondary),
        secondaryContainer = Color(scheme.secondaryContainer),
        onSecondaryContainer = Color(scheme.onSecondaryContainer),
        tertiary = Color(scheme.tertiary),
        onTertiary = Color(scheme.onTertiary),
        tertiaryContainer = Color(scheme.tertiaryContainer),
        onTertiaryContainer = Color(scheme.onTertiaryContainer),
        background = Color(scheme.background),
        onBackground = Color(scheme.onBackground),
        surface = Color(scheme.surface),
        onSurface = Color(scheme.onSurface),
        surfaceVariant = Color(scheme.surfaceVariant),
        onSurfaceVariant = Color(scheme.onSurfaceVariant),
        surfaceTint = Color(scheme.surfaceTint),
        inverseSurface = Color(scheme.inverseSurface),
        inverseOnSurface = Color(scheme.inverseOnSurface),
        error = Color(scheme.error),
        onError = Color(scheme.onError),
        errorContainer = Color(scheme.errorContainer),
        onErrorContainer = Color(scheme.onErrorContainer),
        outline = Color(scheme.outline),
        outlineVariant = Color(scheme.outlineVariant),
        scrim = Color(scheme.scrim),
        surfaceBright = Color(scheme.surfaceBright),
        surfaceDim = Color(scheme.surfaceDim),
        surfaceContainer = Color(scheme.surfaceContainer),
        surfaceContainerHigh = Color(scheme.surfaceContainerHigh),
        surfaceContainerHighest = Color(scheme.surfaceContainerHighest),
        surfaceContainerLow = Color(scheme.surfaceContainerLow),
        surfaceContainerLowest = Color(scheme.surfaceContainerLowest),
    )
}

/**
 * Extension to convert Color to ARGB Int.
 */
private fun Color.toArgb(): Int {
    return android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}
