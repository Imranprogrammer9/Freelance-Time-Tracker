package dev.shipkaro.kit.feature.permissions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.designsystem.permission.KitPermissionScreen
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import dev.shipkaro.kit.core.permission.KitPermission

/**
 * Pre-instantiated full-screen permission flows mirroring SwiftStarterKits' per-permission
 * "view" screens. Each is a one-liner wrapping [KitPermissionScreen] with the matching
 * [KitPermission], icon, and localised strings.
 *
 * Eight Android-relevant permissions ship here:
 *  - [NotificationsPermissionScreen]  · POST_NOTIFICATIONS  (API 33+)
 *  - [CameraPermissionScreen]         · CAMERA
 *  - [MicrophonePermissionScreen]     · RECORD_AUDIO
 *  - [PhotoPermissionScreen]          · READ_MEDIA_IMAGES (API 33+) / READ_EXTERNAL_STORAGE
 *  - [ContactsPermissionScreen]       · READ_CONTACTS
 *  - [CalendarPermissionScreen]       · READ_CALENDAR
 *  - [LocationPermissionScreen]       · ACCESS_FINE_LOCATION
 *  - [MotionPermissionScreen]         · ACTIVITY_RECOGNITION (API 29+)
 *
 * iOS-only permissions (ATT tracking, MusicLibrary) deliberately omitted — Android has no
 * equivalent runtime permission for either.
 *
 * Drop a screen into your nav graph at the moment the feature needs the permission:
 * ```
 * composable<Route.NotificationsPermission> {
 *     NotificationsPermissionScreen(
 *         onGranted = { navController.popBackStack() },
 *         onSkip    = { navController.popBackStack() },
 *     )
 * }
 * ```
 */

@Composable
fun NotificationsPermissionScreen(
    onGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitPermissionScreen(
        permission = KitPermission.NOTIFICATIONS,
        icon = KitTheme.icons.notification,
        title = stringResource(R.string.permission_notifications_title),
        rationale = stringResource(R.string.permission_notifications_rationale),
        rationaleDenied = stringResource(R.string.permission_notifications_denied),
        allowLabel = stringResource(R.string.permission_action_enable),
        skipLabel = stringResource(R.string.permission_action_skip),
        openSettingsLabel = stringResource(R.string.permission_action_open_settings),
        onGranted = onGranted,
        onSkip = onSkip,
        modifier = modifier,
    )
}

@Composable
fun CameraPermissionScreen(
    onGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitPermissionScreen(
        permission = KitPermission.CAMERA,
        icon = KitTheme.icons.camera,
        title = stringResource(R.string.permission_camera_title),
        rationale = stringResource(R.string.permission_camera_rationale),
        rationaleDenied = stringResource(R.string.permission_camera_denied),
        allowLabel = stringResource(R.string.permission_action_allow),
        skipLabel = stringResource(R.string.permission_action_skip),
        openSettingsLabel = stringResource(R.string.permission_action_open_settings),
        onGranted = onGranted,
        onSkip = onSkip,
        modifier = modifier,
    )
}

@Composable
fun MicrophonePermissionScreen(
    onGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitPermissionScreen(
        permission = KitPermission.MICROPHONE,
        icon = KitTheme.icons.microphone,
        title = stringResource(R.string.permission_microphone_title),
        rationale = stringResource(R.string.permission_microphone_rationale),
        rationaleDenied = stringResource(R.string.permission_microphone_denied),
        allowLabel = stringResource(R.string.permission_action_allow),
        skipLabel = stringResource(R.string.permission_action_skip),
        openSettingsLabel = stringResource(R.string.permission_action_open_settings),
        onGranted = onGranted,
        onSkip = onSkip,
        modifier = modifier,
    )
}

@Composable
fun PhotoPermissionScreen(
    onGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitPermissionScreen(
        permission = KitPermission.PHOTO,
        icon = KitTheme.icons.photo,
        title = stringResource(R.string.permission_photo_title),
        rationale = stringResource(R.string.permission_photo_rationale),
        rationaleDenied = stringResource(R.string.permission_photo_denied),
        allowLabel = stringResource(R.string.permission_action_allow),
        skipLabel = stringResource(R.string.permission_action_skip),
        openSettingsLabel = stringResource(R.string.permission_action_open_settings),
        onGranted = onGranted,
        onSkip = onSkip,
        modifier = modifier,
    )
}

@Composable
fun ContactsPermissionScreen(
    onGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitPermissionScreen(
        permission = KitPermission.CONTACTS,
        icon = KitTheme.icons.contacts,
        title = stringResource(R.string.permission_contacts_title),
        rationale = stringResource(R.string.permission_contacts_rationale),
        rationaleDenied = stringResource(R.string.permission_contacts_denied),
        allowLabel = stringResource(R.string.permission_action_allow),
        skipLabel = stringResource(R.string.permission_action_skip),
        openSettingsLabel = stringResource(R.string.permission_action_open_settings),
        onGranted = onGranted,
        onSkip = onSkip,
        modifier = modifier,
    )
}

@Composable
fun CalendarPermissionScreen(
    onGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitPermissionScreen(
        permission = KitPermission.CALENDAR,
        icon = KitTheme.icons.calendar,
        title = stringResource(R.string.permission_calendar_title),
        rationale = stringResource(R.string.permission_calendar_rationale),
        rationaleDenied = stringResource(R.string.permission_calendar_denied),
        allowLabel = stringResource(R.string.permission_action_allow),
        skipLabel = stringResource(R.string.permission_action_skip),
        openSettingsLabel = stringResource(R.string.permission_action_open_settings),
        onGranted = onGranted,
        onSkip = onSkip,
        modifier = modifier,
    )
}

@Composable
fun LocationPermissionScreen(
    onGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitPermissionScreen(
        permission = KitPermission.LOCATION,
        icon = KitTheme.icons.location,
        title = stringResource(R.string.permission_location_title),
        rationale = stringResource(R.string.permission_location_rationale),
        rationaleDenied = stringResource(R.string.permission_location_denied),
        allowLabel = stringResource(R.string.permission_action_allow),
        skipLabel = stringResource(R.string.permission_action_skip),
        openSettingsLabel = stringResource(R.string.permission_action_open_settings),
        onGranted = onGranted,
        onSkip = onSkip,
        modifier = modifier,
    )
}

@Composable
fun MotionPermissionScreen(
    onGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitPermissionScreen(
        permission = KitPermission.MOTION,
        icon = KitTheme.icons.motion,
        title = stringResource(R.string.permission_motion_title),
        rationale = stringResource(R.string.permission_motion_rationale),
        rationaleDenied = stringResource(R.string.permission_motion_denied),
        allowLabel = stringResource(R.string.permission_action_allow),
        skipLabel = stringResource(R.string.permission_action_skip),
        openSettingsLabel = stringResource(R.string.permission_action_open_settings),
        onGranted = onGranted,
        onSkip = onSkip,
        modifier = modifier,
    )
}
