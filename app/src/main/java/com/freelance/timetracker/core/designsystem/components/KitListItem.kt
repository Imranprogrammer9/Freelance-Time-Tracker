package com.freelance.timetracker.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Generic list row: leading icon + headline + supporting text + trailing slot.
 * Pass [onClick] to make the row tappable.
 */
@Composable
fun KitListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    leading: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(headline) },
        supportingContent = supporting?.let { { Text(it) } },
        leadingContent = leading?.let { { Icon(it, contentDescription = null) } },
        trailingContent = trailing,
        colors = ListItemDefaults.colors(),
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
    )
}
