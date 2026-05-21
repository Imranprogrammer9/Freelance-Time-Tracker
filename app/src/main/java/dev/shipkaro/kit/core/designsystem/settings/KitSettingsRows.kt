package dev.shipkaro.kit.core.designsystem.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.core.designsystem.components.KitAvatar
import dev.shipkaro.kit.core.designsystem.theme.KitTheme

/**
 * Section: an uppercase label over a grouped, rounded surface holding the rows
 * (iOS-style grouped list). Put [SettingsDivider] between rows inside the content.
 */
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
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                start = KitTheme.spacing.lg,
                bottom = KitTheme.spacing.sm,
            ),
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KitTheme.spacing.lg),
        ) {
            Column { content() }
        }
        Spacer(Modifier.height(KitTheme.spacing.lg))
    }
}

/** Tinted rounded-square icon container (Swift-kit settings signature). */
@Composable
private fun IconChip(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun BaseRow(
    leading: ImageVector?,
    chipColor: Color?,
    title: String,
    subtitle: String?,
    onClick: (() -> Unit)?,
    trailing: (@Composable () -> Unit)?,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = KitTheme.spacing.md, vertical = KitTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
    ) {
        if (leading != null) {
            IconChip(icon = leading, color = chipColor ?: MaterialTheme.colorScheme.primary)
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
    chipColor: Color? = null,
    subtitle: String? = null,
    enabled: Boolean = true,
) {
    BaseRow(
        leading = leading,
        chipColor = chipColor,
        title = title,
        subtitle = subtitle,
        onClick = if (enabled) { { onCheckedChange(!checked) } } else null,
        trailing = { Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled) },
        modifier = modifier,
    )
}

/** Navigation row — optional value text + chevron, tappable. */
@Composable
fun NavRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: ImageVector? = null,
    chipColor: Color? = null,
    subtitle: String? = null,
    valueText: String? = null,
) {
    BaseRow(
        leading = leading,
        chipColor = chipColor,
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

/** Account header row — avatar + name + email + nav chevron. */
@Composable
fun AccountRow(
    name: String,
    email: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatarUrl: String? = null,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = KitTheme.spacing.lg),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(KitTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
        ) {
            KitAvatar(imageUrl = avatarUrl, initials = name.firstInitials(), size = 52.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
}

private fun String.firstInitials(): String =
    trim().split(" ").filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().toString() }

/** Destructive action row — red title + icon chip, no chevron. */
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
        chipColor = MaterialTheme.colorScheme.error,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        trailing = null,
        titleColor = MaterialTheme.colorScheme.error,
        modifier = modifier,
    )
}

/** Footer row of legal links (privacy / terms). */
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

/** Hairline divider between rows inside a [SettingsSection]. */
@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}
