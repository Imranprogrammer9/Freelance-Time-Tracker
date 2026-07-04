package com.freelance.timetracker.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.freelance.timetracker.core.designsystem.theme.KitTheme

/**
 * Circular avatar. Resolution priority: [imageUrl] → [initials] → account fallback icon.
 *
 * @param size diameter. Default 40dp (Material list-item spec).
 */
@Composable
fun KitAvatar(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    initials: String? = null,
    size: Dp = 40.dp,
) {
    val bg = MaterialTheme.colorScheme.primaryContainer
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        when {
            imageUrl != null -> AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size),
            )
            !initials.isNullOrBlank() -> Text(
                text = initials.take(2).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
            )
            else -> Icon(
                imageVector = KitTheme.icons.account,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
