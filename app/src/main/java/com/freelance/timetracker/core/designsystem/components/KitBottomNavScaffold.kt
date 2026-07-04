package com.freelance.timetracker.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * One entry in [KitBottomNavScaffold]. [selectedIcon] defaults to [icon] —
 * override when you want a filled icon for the selected tab and an outline for
 * the rest.
 */
data class KitBottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val badge: String? = null,
    val onClick: () -> Unit,
)

/**
 * Scaffold + Material 3 [NavigationBar]. Pass the list of tabs as
 * [KitBottomNavItem]s and the current [selectedIndex]; clicks are routed
 * through each item's `onClick` (do your nav-controller work there).
 *
 * Use this as the top-level wrapper for an app with bottom-nav UX. For a kit
 * app without bottom nav, keep using the plain [Scaffold] in your screens.
 */
@Composable
fun KitBottomNavScaffold(
    items: List<KitBottomNavItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        snackbarHost = snackbarHost,
        bottomBar = { KitBottomNavBar(items, selectedIndex) },
        content = content,
    )
}

@Composable
fun KitBottomNavBar(
    items: List<KitBottomNavItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            NavigationBarItem(
                selected = selected,
                onClick = item.onClick,
                icon = {
                    if (item.badge != null) {
                        BadgedBox(badge = { Badge { Text(item.badge) } }) {
                            Icon(
                                if (selected) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                            )
                        }
                    } else {
                        Icon(
                            if (selected) item.selectedIcon else item.icon,
                            contentDescription = item.label,
                        )
                    }
                },
                label = { Text(item.label) },
            )
        }
    }
}
