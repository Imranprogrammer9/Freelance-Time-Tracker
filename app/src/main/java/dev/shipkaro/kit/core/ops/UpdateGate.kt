package dev.shipkaro.kit.core.ops

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.designsystem.components.KitDialog
import org.koin.compose.koinInject

/**
 * Wraps app [content] and overlays an update dialog driven by [UpdateManager].
 *
 *  - REQUIRED : non-dismissible dialog — only "Update" (opens the Play Store)
 *  - OPTIONAL : dismissible dialog — "Update" or "Later"
 *  - NONE     : just renders [content]
 *
 * Place it once near the root (around the nav host).
 */
@Composable
fun UpdateGate(content: @Composable () -> Unit) {
    val updateManager = koinInject<UpdateManager>()
    val state by updateManager.updateState.collectAsState(initial = UpdateManager.UpdateState.NONE)
    val context = LocalContext.current

    var optionalDismissed by remember { mutableStateOf(false) }

    content()

    val openPlayStore = {
        val uri = "market://details?id=${context.packageName}".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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

    when (state) {
        UpdateManager.UpdateState.REQUIRED -> KitDialog(
            title = stringResource(R.string.update_required_title),
            message = stringResource(R.string.update_required_message),
            confirmLabel = stringResource(R.string.update_action),
            dismissLabel = null, // non-dismissible — blocks until updated
            onConfirm = openPlayStore,
            onDismiss = { /* blocked */ },
        )
        UpdateManager.UpdateState.OPTIONAL -> if (!optionalDismissed) {
            KitDialog(
                title = stringResource(R.string.update_optional_title),
                message = stringResource(R.string.update_optional_message),
                confirmLabel = stringResource(R.string.update_action),
                dismissLabel = stringResource(R.string.update_later),
                onConfirm = openPlayStore,
                onDismiss = { optionalDismissed = true },
            )
        }
        UpdateManager.UpdateState.NONE -> Unit
    }
}
