package dev.shipkaro.kit.core.designsystem.icons

/* Disabled by default — the kit ships Material icons only. To enable the Feather
   pack: uncomment composeicons-feather in gradle/libs.versions.toml AND
   implementation(libs.composeicons.feather) in app/build.gradle.kts, remove this
   /* ... */ wrapper, and set LocalKitIcons in KitIcons.kt to FeatherKitIcons.
   `/kit-setup-theme` does all of this for you when you pick Feather.

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.Activity
import compose.icons.feathericons.AlertCircle
import compose.icons.feathericons.AlertTriangle
import compose.icons.feathericons.Aperture
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.ArrowRight
import compose.icons.feathericons.Bell
import compose.icons.feathericons.Calendar
import compose.icons.feathericons.Camera
import compose.icons.feathericons.Check
import compose.icons.feathericons.CheckCircle
import compose.icons.feathericons.ChevronRight
import compose.icons.feathericons.Download
import compose.icons.feathericons.Edit2
import compose.icons.feathericons.Eye
import compose.icons.feathericons.EyeOff
import compose.icons.feathericons.Globe
import compose.icons.feathericons.Image
import compose.icons.feathericons.Info
import compose.icons.feathericons.Lock
import compose.icons.feathericons.LogOut
import compose.icons.feathericons.Mail
import compose.icons.feathericons.MapPin
import compose.icons.feathericons.Menu
import compose.icons.feathericons.Mic
import compose.icons.feathericons.MoreVertical
import compose.icons.feathericons.Plus
import compose.icons.feathericons.Search
import compose.icons.feathericons.Settings
import compose.icons.feathericons.Share2
import compose.icons.feathericons.Shield
import compose.icons.feathericons.Star
import compose.icons.feathericons.Tool
import compose.icons.feathericons.Trash2
import compose.icons.feathericons.Users
import compose.icons.feathericons.User
import compose.icons.feathericons.X

/**
 * Feather icon pack (outline style). Activate via `ShipKaroTheme(icons = FeatherKitIcons)`.
 *
 * Note: Feather has no Google brand icon — `google` falls back to [FeatherIcons.User].
 * Override in a subclass if you need the brand mark (typically via a vector drawable resource).
 */
open class FeatherKitIconsImpl : KitIcons {
    override val back: ImageVector = FeatherIcons.ArrowLeft
    override val close: ImageVector = FeatherIcons.X
    override val menu: ImageVector = FeatherIcons.Menu
    override val search: ImageVector = FeatherIcons.Search
    override val more: ImageVector = FeatherIcons.MoreVertical
    override val arrowRight: ImageVector = FeatherIcons.ArrowRight
    override val chevronRight: ImageVector = FeatherIcons.ChevronRight

    override val add: ImageVector = FeatherIcons.Plus
    override val edit: ImageVector = FeatherIcons.Edit2
    override val delete: ImageVector = FeatherIcons.Trash2
    override val check: ImageVector = FeatherIcons.Check
    override val share: ImageVector = FeatherIcons.Share2
    override val logout: ImageVector = FeatherIcons.LogOut

    override val account: ImageVector = FeatherIcons.User
    override val settings: ImageVector = FeatherIcons.Settings
    override val notification: ImageVector = FeatherIcons.Bell
    override val lock: ImageVector = FeatherIcons.Lock
    override val email: ImageVector = FeatherIcons.Mail
    override val visibility: ImageVector = FeatherIcons.Eye
    override val visibilityOff: ImageVector = FeatherIcons.EyeOff
    override val google: ImageVector = FeatherIcons.User

    override val palette: ImageVector = FeatherIcons.Aperture
    override val language: ImageVector = FeatherIcons.Globe
    override val star: ImageVector = FeatherIcons.Star
    override val shield: ImageVector = FeatherIcons.Shield

    override val update: ImageVector = FeatherIcons.Download
    override val maintenance: ImageVector = FeatherIcons.Tool
    override val camera: ImageVector = FeatherIcons.Camera
    override val location: ImageVector = FeatherIcons.MapPin
    override val microphone: ImageVector = FeatherIcons.Mic
    override val photo: ImageVector = FeatherIcons.Image
    override val contacts: ImageVector = FeatherIcons.Users
    override val calendar: ImageVector = FeatherIcons.Calendar
    override val motion: ImageVector = FeatherIcons.Activity

    override val success: ImageVector = FeatherIcons.CheckCircle
    override val info: ImageVector = FeatherIcons.Info
    override val warning: ImageVector = FeatherIcons.AlertTriangle
    override val error: ImageVector = FeatherIcons.AlertCircle
}

object FeatherKitIcons : FeatherKitIconsImpl()
*/
