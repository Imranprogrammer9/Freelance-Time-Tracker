package com.freelance.timetracker.core.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Confirm/cancel dialog. Pass `null` for [dismissLabel] to make the dialog modal-only
 * (single confirm action). All strings are caller-provided so the dialog stays localizable —
 * the kit deliberately has no English default for the dismiss button.
 *
 * @param destructive renders the confirm button in error color (used for delete-account etc).
 */
@Composable
fun KitDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissLabel: String?,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    confirmLabel,
                    color = if (destructive) {
                        androidx.compose.material3.MaterialTheme.colorScheme.error
                    } else {
                        androidx.compose.material3.MaterialTheme.colorScheme.primary
                    },
                )
            }
        },
        dismissButton = dismissLabel?.let {
            {
                TextButton(onClick = onDismiss) { Text(it) }
            }
        },
        modifier = modifier,
    )
}
