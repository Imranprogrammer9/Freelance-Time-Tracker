package com.freelance.timetracker.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.freelance.timetracker.core.designsystem.theme.KitTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Severity-tagged snackbar variants — inspired by TonnyL/Light. Maps to the same
 * semantic states as [KitBanner] (INFO / SUCCESS / WARNING / ERROR) so a feature
 * picks one shape for in-place banners and another for transient feedback.
 */
enum class KitSnackbarStyle { INFO, SUCCESS, WARNING, ERROR }

/**
 * [SnackbarVisuals] carrier for kit-styled snackbars. Pass instances of this to
 * [SnackbarHostState.showSnackbar] when you want the pre-styled rendering;
 * passing a plain `String` still works (renders the Material default).
 *
 * Use [KitSnackbarController] (returned by [rememberKitSnackbarController]) for
 * the ergonomic call-site — `controller.success("Saved!")` etc.
 */
data class KitSnackbarVisuals(
    override val message: String,
    val title: String? = null,
    val style: KitSnackbarStyle = KitSnackbarStyle.INFO,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val withDismissAction: Boolean = false,
    override val actionLabel: String? = null,
) : SnackbarVisuals

/**
 * Ergonomic wrapper around [SnackbarHostState] — `controller.success("Saved!")`
 * instead of `hostState.showSnackbar(KitSnackbarVisuals(..., SUCCESS))`.
 *
 * The four helpers all return immediately (work happens on the controller's
 * scope). Action callbacks are invoked only when the user taps the action
 * label, matching [SnackbarHostState.showSnackbar] semantics.
 */
class KitSnackbarController internal constructor(
    val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    fun info(
        message: String,
        title: String? = null,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null,
    ) = show(KitSnackbarStyle.INFO, message, title, actionLabel, duration, onAction)

    fun success(
        message: String,
        title: String? = null,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null,
    ) = show(KitSnackbarStyle.SUCCESS, message, title, actionLabel, duration, onAction)

    fun warning(
        message: String,
        title: String? = null,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null,
    ) = show(KitSnackbarStyle.WARNING, message, title, actionLabel, duration, onAction)

    fun error(
        message: String,
        title: String? = null,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Long,
        onAction: (() -> Unit)? = null,
    ) = show(KitSnackbarStyle.ERROR, message, title, actionLabel, duration, onAction)

    private fun show(
        style: KitSnackbarStyle,
        message: String,
        title: String?,
        actionLabel: String?,
        duration: SnackbarDuration,
        onAction: (() -> Unit)?,
    ) {
        scope.launch {
            hostState.currentSnackbarData?.dismiss()
            val visuals = KitSnackbarVisuals(
                message = message,
                title = title,
                style = style,
                duration = duration,
                withDismissAction = actionLabel == null,
                actionLabel = actionLabel,
            )
            val result = hostState.showSnackbar(visuals)
            if (result == SnackbarResult.ActionPerformed && onAction != null) {
                onAction()
            }
        }
    }
}

@Composable
fun rememberKitSnackbarController(
    hostState: SnackbarHostState = remember { SnackbarHostState() },
): KitSnackbarController {
    val scope = rememberCoroutineScope()
    return remember(hostState, scope) { KitSnackbarController(hostState, scope) }
}

/**
 * Snackbar host wired to render [KitSnackbarVisuals] with the matching icon +
 * tinted background. Falls back to the Material default for plain-string
 * snackbars so unrelated callers still work.
 *
 * Place inside a [androidx.compose.material3.Scaffold]'s `snackbarHost` slot.
 */
@Composable
fun KitSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        val visuals = data.visuals as? KitSnackbarVisuals
        if (visuals == null) {
            Snackbar(snackbarData = data)
            return@SnackbarHost
        }
        KitStyledSnackbar(visuals = visuals, data = data)
    }
}

@Composable
private fun KitStyledSnackbar(
    visuals: KitSnackbarVisuals,
    data: androidx.compose.material3.SnackbarData,
) {
    val icons = KitTheme.icons
    val kc = KitTheme.colors
    val (accent: Color, leading: ImageVector) = when (visuals.style) {
        KitSnackbarStyle.INFO -> kc.info to icons.info
        KitSnackbarStyle.SUCCESS -> kc.success to icons.success
        KitSnackbarStyle.WARNING -> kc.warning to icons.warning
        KitSnackbarStyle.ERROR -> MaterialTheme.colorScheme.error to icons.error
    }

    Row(
        modifier = Modifier
            .padding(KitTheme.spacing.md)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(KitTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm),
    ) {
        Icon(
            imageVector = leading,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            if (!visuals.title.isNullOrBlank()) {
                Text(
                    text = visuals.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (visuals.actionLabel != null) {
            TextButton(onClick = { data.performAction() }) {
                Text(visuals.actionLabel, color = accent, fontWeight = FontWeight.SemiBold)
            }
        } else if (visuals.withDismissAction) {
            IconButton(onClick = { data.dismiss() }) {
                Icon(
                    icons.close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
