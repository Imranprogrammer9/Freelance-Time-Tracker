package dev.shipkaro.kit.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animated shimmer modifier — sweeping gradient across the painted area. Use on
 * a clipped surface to render the skeleton loading effect.
 *
 *     Modifier
 *         .clip(RoundedCornerShape(8.dp))
 *         .kitShimmer()
 *
 * Pair with [KitSkeleton] for the common loading-placeholder pattern.
 */
fun Modifier.kitShimmer(): Modifier = composed {
    val infinite = rememberInfiniteTransition(label = "kitShimmer")
    val progress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "kitShimmerProgress",
    )
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
    val brushColors = listOf(base, highlight, base)
    drawBehind {
        val width = size.width
        val travel = width * 2f
        val startX = -width + (travel * progress)
        drawRect(
            brush = Brush.linearGradient(
                colors = brushColors,
                start = Offset(startX, 0f),
                end = Offset(startX + width, size.height),
            ),
        )
    }
}

/**
 * Single shimmer placeholder — a clipped rectangle of [width]×[height]. Compose
 * a few of these in a [Column] / [androidx.compose.foundation.layout.Row] to
 * model the shape of the screen that's loading (avatar circle + two text bars).
 */
@Composable
fun KitSkeleton(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    width: Dp? = null,
    cornerRadius: Dp = 8.dp,
) {
    val sized = if (width != null) {
        modifier.size(width = width, height = height)
    } else {
        modifier
            .fillMaxWidth()
            .height(height)
    }
    Box(
        modifier = sized
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .kitShimmer(),
    )
}

/**
 * Three-line skeleton — typical "list item loading" placeholder. Use inside a
 * [Column] alongside other [KitSkeleton]s for richer loading states.
 */
@Composable
fun KitSkeletonListItem(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        KitSkeleton(height = 14.dp, width = 180.dp)
        KitSkeleton(height = 12.dp)
        KitSkeleton(height = 12.dp, width = 220.dp)
    }
}
