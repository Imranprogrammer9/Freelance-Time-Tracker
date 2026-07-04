package com.freelance.timetracker.core.designsystem.icons

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Semantic icon catalog. Components reference icons by *role* (back, close, account…),
 * not by a hardcoded vector. Swap the pack at theme level — or override individual
 * icons by subclassing one of the bundled implementations.
 *
 * Bundled implementations:
 *  - [MaterialKitIcons] (default — uses androidx material-icons-extended)
 *  - [FeatherKitIcons]  (compose-icons feather)
 *  - [TablerKitIcons]   (compose-icons tabler-icons)
 *
 * Usage at theme level:
 * ```
 * ShipKaroTheme(icons = FeatherKitIcons) { ... }
 * ```
 *
 * Custom pack (mix Material defaults + your own):
 * ```
 * object MyIcons : MaterialKitIcons() {
 *     override val back: ImageVector = MyCustomBackVector
 * }
 * ShipKaroTheme(icons = MyIcons) { ... }
 * ```
 *
 * Per-callsite override:
 * ```
 * KitIconButton(icon = TablerIcons.ArrowLeft, onClick = { ... })
 * ```
 */
interface KitIcons {
    // Navigation
    val back: ImageVector
    val close: ImageVector
    val menu: ImageVector
    val search: ImageVector
    val more: ImageVector
    val arrowRight: ImageVector
    val chevronRight: ImageVector

    // Actions
    val add: ImageVector
    val edit: ImageVector
    val delete: ImageVector
    val check: ImageVector
    val share: ImageVector
    val logout: ImageVector

    // Account
    val account: ImageVector
    val settings: ImageVector
    val notification: ImageVector
    val lock: ImageVector
    val email: ImageVector
    val visibility: ImageVector
    val visibilityOff: ImageVector
    val google: ImageVector

    // Settings / misc
    val palette: ImageVector
    val language: ImageVector
    val star: ImageVector
    val shield: ImageVector

    // Ops / permissions
    val update: ImageVector
    val maintenance: ImageVector
    val camera: ImageVector
    val location: ImageVector
    val microphone: ImageVector
    val photo: ImageVector
    val contacts: ImageVector
    val calendar: ImageVector
    val motion: ImageVector

    // State
    val success: ImageVector
    val info: ImageVector
    val warning: ImageVector
    val error: ImageVector
}

/**
 * CompositionLocal carrying the active icon pack. Set by [ShipKaroTheme]; read by
 * any composable that needs the kit-wide icon set.
 *
 * Default = [MaterialKitIcons] so a user who passes no `icons =` arg still works.
 */
val LocalKitIcons = staticCompositionLocalOf<KitIcons> { FeatherKitIcons }
