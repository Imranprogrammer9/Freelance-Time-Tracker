package dev.shipkaro.kit.core.designsystem.icons

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.TablerIcons
import compose.icons.tablericons.AlertCircle
import compose.icons.tablericons.AlertTriangle
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.ArrowRight
import compose.icons.tablericons.Bell
import compose.icons.tablericons.BrandGoogle
import compose.icons.tablericons.Camera
import compose.icons.tablericons.Check
import compose.icons.tablericons.ChevronRight
import compose.icons.tablericons.CircleCheck
import compose.icons.tablericons.Dots
import compose.icons.tablericons.Download
import compose.icons.tablericons.Edit
import compose.icons.tablericons.Eye
import compose.icons.tablericons.EyeOff
import compose.icons.tablericons.InfoCircle
import compose.icons.tablericons.Language
import compose.icons.tablericons.Lock
import compose.icons.tablericons.Logout
import compose.icons.tablericons.Mail
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Menu2
import compose.icons.tablericons.Palette
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Search
import compose.icons.tablericons.Settings
import compose.icons.tablericons.Share
import compose.icons.tablericons.Shield
import compose.icons.tablericons.Star
import compose.icons.tablericons.Tool
import compose.icons.tablericons.Trash
import compose.icons.tablericons.User
import compose.icons.tablericons.X

/**
 * Tabler icon pack (outline, Heroicons-like style). Activate via `ShipKaroTheme(icons = TablerKitIcons)`.
 *
 * Tabler is the closest readily available stand-in for Heroicons (no Heroicons-specific module
 * exists in compose-icons). For true Heroicons, copy-paste vectors from composables.com/icons
 * into your custom pack.
 */
open class TablerKitIconsImpl : KitIcons {
    override val back: ImageVector = TablerIcons.ArrowLeft
    override val close: ImageVector = TablerIcons.X
    override val menu: ImageVector = TablerIcons.Menu2
    override val search: ImageVector = TablerIcons.Search
    override val more: ImageVector = TablerIcons.Dots
    override val arrowRight: ImageVector = TablerIcons.ArrowRight
    override val chevronRight: ImageVector = TablerIcons.ChevronRight

    override val add: ImageVector = TablerIcons.Plus
    override val edit: ImageVector = TablerIcons.Edit
    override val delete: ImageVector = TablerIcons.Trash
    override val check: ImageVector = TablerIcons.Check
    override val share: ImageVector = TablerIcons.Share
    override val logout: ImageVector = TablerIcons.Logout

    override val account: ImageVector = TablerIcons.User
    override val settings: ImageVector = TablerIcons.Settings
    override val notification: ImageVector = TablerIcons.Bell
    override val lock: ImageVector = TablerIcons.Lock
    override val email: ImageVector = TablerIcons.Mail
    override val visibility: ImageVector = TablerIcons.Eye
    override val visibilityOff: ImageVector = TablerIcons.EyeOff
    override val google: ImageVector = TablerIcons.BrandGoogle

    override val palette: ImageVector = TablerIcons.Palette
    override val language: ImageVector = TablerIcons.Language
    override val star: ImageVector = TablerIcons.Star
    override val shield: ImageVector = TablerIcons.Shield

    override val update: ImageVector = TablerIcons.Download
    override val maintenance: ImageVector = TablerIcons.Tool
    override val camera: ImageVector = TablerIcons.Camera
    override val location: ImageVector = TablerIcons.MapPin

    override val success: ImageVector = TablerIcons.CircleCheck
    override val info: ImageVector = TablerIcons.InfoCircle
    override val warning: ImageVector = TablerIcons.AlertTriangle
    override val error: ImageVector = TablerIcons.AlertCircle
}

object TablerKitIcons : TablerKitIconsImpl()
