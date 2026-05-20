package dev.shipkaro.kit.core.designsystem.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/** Static chip with optional leading icon. Use [KitFilterChip] for toggleable selection. */
@Composable
fun KitChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        enabled = enabled,
        modifier = modifier,
    )
}

@Composable
fun KitFilterChip(
    text: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = { Text(text) },
        enabled = enabled,
        modifier = modifier,
    )
}
