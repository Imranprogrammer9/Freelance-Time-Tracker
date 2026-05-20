package dev.shipkaro.kit.core.designsystem.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.core.designsystem.theme.KitTheme

enum class KitBannerStyle { INFO, SUCCESS, WARNING, ERROR }

/**
 * Inline banner for status messaging (top of screen / list / form).
 * Style maps to [KitTheme.colors] semantic state palette + Material error.
 */
@Composable
fun KitBanner(
    text: String,
    modifier: Modifier = Modifier,
    style: KitBannerStyle = KitBannerStyle.INFO,
    title: String? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val icons = KitTheme.icons
    val kc = KitTheme.colors
    val (bg: Color, fg: Color, leading) = when (style) {
        KitBannerStyle.INFO -> Triple(kc.info, kc.onInfo, icons.info)
        KitBannerStyle.SUCCESS -> Triple(kc.success, kc.onSuccess, icons.success)
        KitBannerStyle.WARNING -> Triple(kc.warning, kc.onWarning, icons.warning)
        KitBannerStyle.ERROR -> Triple(
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError,
            icons.error,
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg.copy(alpha = 0.12f))
            .padding(KitTheme.spacing.md),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm),
    ) {
        Icon(leading, contentDescription = null, tint = bg, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (title != null) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = fg.copy(alpha = 1f))
            }
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
        if (onDismiss != null) {
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(icons.close, contentDescription = "Dismiss", modifier = Modifier.size(18.dp))
            }
        }
    }
}
