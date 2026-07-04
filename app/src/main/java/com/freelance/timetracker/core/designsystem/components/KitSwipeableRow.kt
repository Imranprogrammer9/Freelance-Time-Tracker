package com.freelance.timetracker.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.freelance.timetracker.core.designsystem.theme.KitTheme

/**
 * Swipe-to-act row — wrap a list item, get a colored background that reveals
 * when the user drags. By default left-swipe = destructive (delete), right-swipe
 * = constructive (archive); pass nulls to disable a direction.
 *
 * State resets after each swipe so the same row can be swiped multiple times.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitSwipeableRow(
    onSwipeEndToStart: (() -> Unit)? = null,
    onSwipeStartToEnd: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    endToStartIcon: ImageVector = KitTheme.icons.delete,
    startToEndIcon: ImageVector = KitTheme.icons.check,
    endToStartBg: Color = MaterialTheme.colorScheme.error,
    startToEndBg: Color = KitTheme.colors.success,
    content: @Composable () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState()

    LaunchedEffect(state.currentValue) {
        when (state.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                onSwipeEndToStart?.invoke()
                state.reset()
            }
            SwipeToDismissBoxValue.StartToEnd -> {
                onSwipeStartToEnd?.invoke()
                state.reset()
            }
            SwipeToDismissBoxValue.Settled -> Unit
        }
    }

    SwipeToDismissBox(
        state = state,
        modifier = modifier,
        enableDismissFromEndToStart = onSwipeEndToStart != null,
        enableDismissFromStartToEnd = onSwipeStartToEnd != null,
        backgroundContent = {
            val (bg, icon, alignment) = when (state.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Triple(endToStartBg, endToStartIcon, Alignment.CenterEnd)
                SwipeToDismissBoxValue.StartToEnd -> Triple(startToEndBg, startToEndIcon, Alignment.CenterStart)
                SwipeToDismissBoxValue.Settled -> Triple(Color.Transparent, endToStartIcon, Alignment.Center)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bg)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment,
            ) {
                if (state.dismissDirection != SwipeToDismissBoxValue.Settled) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                }
            }
        },
        content = { content() },
    )
}
