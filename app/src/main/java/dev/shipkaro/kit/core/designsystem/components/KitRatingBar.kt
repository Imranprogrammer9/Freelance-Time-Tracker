package dev.shipkaro.kit.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.core.designsystem.theme.KitTheme

/**
 * 5-star rating row. Tap a star to set the rating; tap the same star to clear
 * (`onRatingChange(0)`). Pass [readOnly] = true for display-only.
 *
 * For half-star rendering or different scales, fork this — kept minimal here.
 */
@Composable
fun KitRatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    max: Int = 5,
    starSize: Dp = 28.dp,
    readOnly: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for (i in 1..max) {
            val active = i <= rating
            Icon(
                imageVector = KitTheme.icons.star,
                contentDescription = null,
                tint = if (active) KitTheme.colors.warning else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                    .size(starSize)
                    .let { mod ->
                        if (readOnly) mod else mod.clickable {
                            onRatingChange(if (rating == i) 0 else i)
                        }
                    },
            )
        }
    }
}
