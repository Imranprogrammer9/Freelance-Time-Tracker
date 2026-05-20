package dev.shipkaro.kit.core.designsystem.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.core.designsystem.components.KitAvatar
import dev.shipkaro.kit.core.designsystem.theme.KitTheme

/** Section header — small uppercase label preceding a group of rows. */
@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                horizontal = KitTheme.spacing.lg,
                vertical = KitTheme.spacing.sm,
            ),
        )
        content()
        Spacer(Modifier.height(KitTheme.spacing.lg))
    }
}

@Composable
private fun BaseRow(
    leading: ImageVector?,
    title: String,
    subtitle: String?,
    onClick: (() -> Unit)?,
    trailing: (@Composable () -> Unit)?,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = KitTheme.spacing.lg, vertical = KitTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
    ) {
        if (leading != null) {
            Icon(leading, contentDescription = null, tint = titleColor, modifier = Modifier.size(24.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = titleColor)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (trailing != null) trailing()
    }
}

/** Toggle row — Switch on the right. */
@Composable
fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leading: ImageVector? = null,
    subtitle: String? = null,
    enabled: Boolean = true,
) {
    BaseRow(
        leading = leading,
        title = title,
        subtitle = subtitle,
        onClick = if (enabled) { { onCheckedChange(!checked) } } else null,
        trailing = { Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled) },
        modifier = modifier,
    )
}

/** Navigation row — chevron trailing, tappable. */
@Composable
fun NavRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: ImageVector? = null,
    subtitle: String? = null,
    valueText: String? = null,
) {
    BaseRow(
        leading = leading,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (valueText != null) {
                    Text(
                        valueText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.size(KitTheme.spacing.sm))
                }
                Icon(
                    KitTheme.icons.chevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        modifier = modifier,
    )
}

/** Account header row — avatar + name + email + nav chevron. Tap to open profile. */
@Composable
fun AccountRow(
    name: String,
    email: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatarUrl: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(KitTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
    ) {
        KitAvatar(imageUrl = avatarUrl, initials = name.firstInitials(), size = 48.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text(
                email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            KitTheme.icons.chevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun String.firstInitials(): String =
    trim().split(" ").filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().toString() }

/** Destructive action row — red text, no chevron (terminal action). */
@Composable
fun DangerRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: ImageVector? = null,
    subtitle: String? = null,
) {
    BaseRow(
        leading = leading,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        trailing = null,
        titleColor = MaterialTheme.colorScheme.error,
        modifier = modifier,
    )
}

/** Footer row of legal links (privacy / terms). Each entry: label → URL or void callback. */
@Composable
fun LegalLinks(
    links: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(KitTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.lg, Alignment.CenterHorizontally),
    ) {
        links.forEachIndexed { i, (label, onClick) ->
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onClick),
            )
            if (i < links.lastIndex) {
                Text(
                    "·",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Thin divider for between rows. */
@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = KitTheme.spacing.lg),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}
