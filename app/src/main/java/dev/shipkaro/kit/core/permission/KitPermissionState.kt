package dev.shipkaro.kit.core.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class KitPermissionStatus { GRANTED, DENIED, PERMANENTLY_DENIED }

/**
 * Observable state for a single runtime [KitPermission]. Obtain via [rememberKitPermissionState].
 *
 * Typical use:
 * ```
 * val notifications = rememberKitPermissionState(KitPermission.NOTIFICATIONS)
 * if (!notifications.isGranted) {
 *     KitPermissionPrimer(
 *         icon = KitTheme.icons.notification,
 *         title = stringResource(R.string.permission_notifications_title),
 *         rationale = stringResource(R.string.permission_notifications_rationale),
 *         allowLabel = stringResource(R.string.permission_action_allow),
 *         dismissLabel = stringResource(R.string.permission_action_skip),
 *         onAllow = {
 *             if (notifications.status == KitPermissionStatus.PERMANENTLY_DENIED) {
 *                 notifications.openAppSettings()
 *             } else {
 *                 notifications.requestPermission()
 *             }
 *         },
 *         onDismiss = { /* hide the primer */ },
 *     )
 * }
 * ```
 */
@Stable
class KitPermissionState internal constructor(val permission: KitPermission) {

    var status: KitPermissionStatus by mutableStateOf(KitPermissionStatus.DENIED)
        internal set

    internal var onRequest: () -> Unit = {}
    internal var onOpenSettings: () -> Unit = {}

    val isGranted: Boolean get() = status == KitPermissionStatus.GRANTED

    /** Triggers the system permission dialog. Show your [KitPermissionPrimer] rationale first. */
    fun requestPermission() = onRequest()

    /** Opens this app's system settings page — use when [status] is [KitPermissionStatus.PERMANENTLY_DENIED]. */
    fun openAppSettings() = onOpenSettings()
}

/**
 * Remembers a [KitPermissionState] for [permission], wiring the system permission launcher
 * and resolving granted / denied / permanently-denied. Recheck on screen entry is automatic.
 */
@Composable
fun rememberKitPermissionState(permission: KitPermission): KitPermissionState {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val state = remember(permission) { KitPermissionState(permission) }

    // Resolve per-SDK manifest string (PHOTO swaps to legacy READ_EXTERNAL_STORAGE on API < 33).
    val resolvedPermission = remember(permission) { resolveManifestPermission(permission) }

    // POST_NOTIFICATIONS only exists on API 33+; below that, notifications are granted by default.
    // ACTIVITY_RECOGNITION only exists on API 29+; below that, motion is granted by default.
    val implicitlyGranted = (
        permission == KitPermission.NOTIFICATIONS &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) || (
        permission == KitPermission.MOTION &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        )

    LaunchedEffect(permission) {
        val granted = implicitlyGranted ||
            ContextCompat.checkSelfPermission(context, resolvedPermission) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) {
            state.status = KitPermissionStatus.GRANTED
        } else if (state.status == KitPermissionStatus.GRANTED) {
            state.status = KitPermissionStatus.DENIED
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        state.status = when {
            granted -> KitPermissionStatus.GRANTED
            activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                resolvedPermission,
            ) -> KitPermissionStatus.DENIED
            else -> KitPermissionStatus.PERMANENTLY_DENIED
        }
    }

    state.onRequest = {
        if (implicitlyGranted) {
            state.status = KitPermissionStatus.GRANTED
        } else {
            launcher.launch(resolvedPermission)
        }
    }
    state.onOpenSettings = {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }
    return state
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Resolves the manifest permission string per Android version. Most permissions are stable;
 * PHOTO swaps to the legacy storage permission on API < 33.
 */
private fun resolveManifestPermission(permission: KitPermission): String = when {
    permission == KitPermission.PHOTO &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ->
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    else -> permission.manifestPermission
}
