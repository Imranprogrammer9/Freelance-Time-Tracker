package com.freelance.timetracker.core.designsystem.components

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Floating action button. Two modes:
 *  - icon-only (default): pass [icon] + [contentDescription].
 *  - extended: pass [icon] + [label] — renders an extended FAB.
 *
 * Use Material 3 directly when you need custom colors or sizing; this is the
 * kit-flavoured one-liner for the common case.
 */
@Composable
fun KitFab(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    label: String? = null,
) {
    if (label.isNullOrBlank()) {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(icon, contentDescription = contentDescription)
        }
    } else {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = { Icon(icon, contentDescription = contentDescription) },
            text = { Text(label) },
        )
    }
}
