package dev.shipkaro.kit.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import dev.shipkaro.kit.core.designsystem.foundation.KitElevation
import dev.shipkaro.kit.core.designsystem.foundation.KitShapes
import dev.shipkaro.kit.core.designsystem.foundation.KitSpacing
import dev.shipkaro.kit.core.designsystem.foundation.LocalKitElevation
import dev.shipkaro.kit.core.designsystem.foundation.LocalKitSpacing
import dev.shipkaro.kit.core.designsystem.icons.KitIcons
import dev.shipkaro.kit.core.designsystem.icons.LocalKitIcons
import dev.shipkaro.kit.core.designsystem.icons.MaterialKitIcons

/** User-selectable theme. Persisted via SettingsRepository / DataStore. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

private val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = BrandSecondary,
    tertiary = BrandTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = LightError,
    onError = LightOnError,
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = BrandSecondary,
    tertiary = BrandTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = DarkError,
    onError = DarkOnError,
)

/**
 * App theme. Supports light, dark, follow-system, and (Android 12+) Material You
 * dynamic color. [themeMode] null = not loaded yet, fall back to system.
 *
 * @param icons Icon pack used kit-wide. Default = [MaterialKitIcons]. Switch to
 *   `FeatherKitIcons`, `TablerKitIcons`, or a custom subclass.
 * @param kitColors Semantic state colors (success/warning/info) not in Material 3.
 */
@Composable
fun ShipKaroTheme(
    themeMode: ThemeMode? = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    icons: KitIcons = MaterialKitIcons,
    kitColors: KitColors = KitColors(),
    spacing: KitSpacing = KitSpacing(),
    elevation: KitElevation = KitElevation(),
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        else -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> DarkColors
        else -> LightColors
    }

    CompositionLocalProvider(
        LocalKitIcons provides icons,
        LocalKitColors provides kitColors,
        LocalKitSpacing provides spacing,
        LocalKitElevation provides elevation,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KitTypography,
            shapes = KitShapes,
            content = content,
        )
    }
}

/**
 * Ergonomic accessor for kit tokens inside composables.
 *
 * ```
 * Spacer(Modifier.height(KitTheme.spacing.lg))
 * Icon(KitTheme.icons.back, contentDescription = null)
 * Box(Modifier.background(KitTheme.colors.success))
 * ```
 */
object KitTheme {
    val icons: KitIcons
        @Composable @ReadOnlyComposable
        get() = LocalKitIcons.current
    val colors: KitColors
        @Composable @ReadOnlyComposable
        get() = LocalKitColors.current
    val spacing: KitSpacing
        @Composable @ReadOnlyComposable
        get() = LocalKitSpacing.current
    val elevation: KitElevation
        @Composable @ReadOnlyComposable
        get() = LocalKitElevation.current
}
