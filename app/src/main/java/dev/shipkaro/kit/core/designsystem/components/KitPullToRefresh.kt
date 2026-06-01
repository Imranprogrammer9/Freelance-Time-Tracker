package dev.shipkaro.kit.core.designsystem.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Pull-to-refresh wrapper around the Material 3 [PullToRefreshBox]. Drop a list /
 * column inside [content] — the indicator pinned to the top spinner.
 *
 * Drive [isRefreshing] from the caller (usually a ViewModel `StateFlow<Boolean>`)
 * and start the refresh inside [onRefresh].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    content: @Composable BoxScope.() -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        content = content,
    )
}
