package dev.shipkaro.kit.core.designsystem.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Default icon pack — backed by androidx `material-icons-extended`.
 *
 * Override individual icons by subclassing:
 * ```
 * object MyIcons : MaterialKitIcons() {
 *     override val back: ImageVector = MyBackVector
 * }
 * ```
 *
 * Note: There's no Material "Google" logo icon (trademark). We reuse [Icons.Default.AccountCircle]
 * as a neutral fallback — override in your custom pack if needed.
 */
open class MaterialKitIconsImpl : KitIcons {
    override val back: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    override val close: ImageVector = Icons.Default.Close
    override val menu: ImageVector = Icons.Default.Menu
    override val search: ImageVector = Icons.Default.Search
    override val more: ImageVector = Icons.Default.MoreVert
    override val arrowRight: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
    override val chevronRight: ImageVector = Icons.Default.ChevronRight

    override val add: ImageVector = Icons.Default.Add
    override val edit: ImageVector = Icons.Default.Edit
    override val delete: ImageVector = Icons.Default.Delete
    override val check: ImageVector = Icons.Default.Check
    override val share: ImageVector = Icons.Default.Share
    override val logout: ImageVector = Icons.AutoMirrored.Filled.Logout

    override val account: ImageVector = Icons.Default.Person
    override val settings: ImageVector = Icons.Default.Settings
    override val notification: ImageVector = Icons.Default.Notifications
    override val lock: ImageVector = Icons.Default.Lock
    override val email: ImageVector = Icons.Default.Email
    override val visibility: ImageVector = Icons.Default.Visibility
    override val visibilityOff: ImageVector = Icons.Default.VisibilityOff
    override val google: ImageVector = Icons.Default.Person // override with a brand vector in your pack

    override val success: ImageVector = Icons.Default.CheckCircle
    override val info: ImageVector = Icons.Default.Info
    override val warning: ImageVector = Icons.Default.Warning
    override val error: ImageVector = Icons.Default.Error
}

/** Default singleton instance. Use directly or subclass [MaterialKitIconsImpl] for overrides. */
object MaterialKitIcons : MaterialKitIconsImpl()
