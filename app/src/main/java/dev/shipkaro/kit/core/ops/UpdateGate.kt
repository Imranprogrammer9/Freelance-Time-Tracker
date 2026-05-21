package dev.shipkaro.kit.core.ops

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import dev.shipkaro.kit.BuildConfig
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.core.designsystem.ops.KitMaintenanceScreen
import dev.shipkaro.kit.core.designsystem.ops.KitUpdateSheet
import dev.shipkaro.kit.core.designsystem.ops.KitWhatsNewSheet
import org.koin.compose.koinInject

/**
 * Wraps app [content] and overlays the backend-driven ops gates. Place it once near the
 * root (around the nav host). Priority, highest first:
 *
 *  1. MAINTENANCE — backend `maintenance_mode` on → full-screen block, app content hidden.
 *  2. REQUIRED update — running build below `min_supported_version` → non-dismissible sheet.
 *  3. OPTIONAL update — build below `latest_version` → dismissible sheet ("Update" / "Later").
 *  4. WHAT'S NEW — first launch after a version bump → one-time release-notes sheet.
 *
 * Update sheets and the What's New sheet render release notes published on the backend
 * (`app_changelog`); see [ChangelogManager].
 */
@Composable
fun UpdateGate(content: @Composable () -> Unit) {
    val updateManager = koinInject<UpdateManager>()
    val changelogManager = koinInject<ChangelogManager>()
    val settings = koinInject<SettingsRepository>()
    val context = LocalContext.current

    val updateState by updateManager.updateState
        .collectAsState(initial = UpdateManager.UpdateState.NONE)
    val maintenance by updateManager.maintenance
        .collectAsState(initial = UpdateManager.Maintenance(active = false, message = ""))
    val releases by changelogManager.releases.collectAsState(initial = emptyList())
    val lastSeenVersion by settings.lastSeenVersionCode.collectAsState(initial = -1)

    var optionalDismissed by remember { mutableStateOf(false) }
    var whatsNewRelease by remember { mutableStateOf<ChangelogRelease?>(null) }
    var whatsNewHandled by remember { mutableStateOf(false) }
    var whatsNewDismissed by remember { mutableStateOf(false) }

    // What's New: show notes for the running build once, the first launch after an update.
    // A fresh install (lastSeen == 0) is recorded silently — nothing changed for the user yet.
    LaunchedEffect(lastSeenVersion, releases) {
        if (lastSeenVersion < 0 || whatsNewHandled) return@LaunchedEffect
        val current = BuildConfig.VERSION_CODE
        if (lastSeenVersion in 1 until current) {
            whatsNewRelease = releases.firstOrNull { it.versionCode == current }
        }
        settings.setLastSeenVersionCode(current)
        whatsNewHandled = true
    }

    val openPlayStore = {
        val marketUri = "market://details?id=${context.packageName}".toUri()
        val intent = Intent(Intent.ACTION_VIEW, marketUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
            .onFailure {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://play.google.com/store/apps/details?id=${context.packageName}".toUri(),
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                )
            }
        Unit
    }

    if (maintenance.active) {
        KitMaintenanceScreen(
            title = stringResource(R.string.maintenance_title),
            message = maintenance.message.ifEmpty {
                stringResource(R.string.maintenance_message_default)
            },
        )
        return
    }

    content()

    val pendingNotes = releases
        .filter { it.versionCode > BuildConfig.VERSION_CODE }
        .flatMap { it.notes }

    when (updateState) {
        UpdateManager.UpdateState.REQUIRED -> KitUpdateSheet(
            title = stringResource(R.string.update_required_title),
            message = stringResource(R.string.update_required_message),
            updateLabel = stringResource(R.string.update_action),
            laterLabel = stringResource(R.string.update_later),
            onUpdate = openPlayStore,
            onLater = { },
            dismissible = false,
            changes = pendingNotes,
            changesLabel = stringResource(R.string.update_whats_changed),
        )

        UpdateManager.UpdateState.OPTIONAL -> if (!optionalDismissed) {
            KitUpdateSheet(
                title = stringResource(R.string.update_optional_title),
                message = stringResource(R.string.update_optional_message),
                updateLabel = stringResource(R.string.update_action),
                laterLabel = stringResource(R.string.update_later),
                onUpdate = openPlayStore,
                onLater = { optionalDismissed = true },
                dismissible = true,
                changes = pendingNotes,
                changesLabel = stringResource(R.string.update_whats_changed),
            )
        }

        UpdateManager.UpdateState.NONE -> {
            val release = whatsNewRelease
            if (release != null && !whatsNewDismissed) {
                KitWhatsNewSheet(
                    title = stringResource(R.string.whats_new_title),
                    versionLabel = stringResource(R.string.whats_new_version, release.versionName),
                    changes = release.notes,
                    dismissLabel = stringResource(R.string.whats_new_dismiss),
                    onDismiss = { whatsNewDismissed = true },
                )
            }
        }
    }
}
