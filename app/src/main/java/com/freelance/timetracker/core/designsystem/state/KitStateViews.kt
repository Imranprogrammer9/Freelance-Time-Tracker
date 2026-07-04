package com.freelance.timetracker.core.designsystem.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.freelance.timetracker.core.designsystem.components.KitButton
import com.freelance.timetracker.core.designsystem.theme.KitTheme

/** Full-screen centered spinner. Use during initial screen loads. */
@Composable
fun KitLoadingState(modifier: Modifier = Modifier, label: String? = null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            if (label != null) {
                Spacer(Modifier.height(KitTheme.spacing.md))
                Text(label, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** Inline spinner row. Use within a card / list footer. */
@Composable
fun KitInlineLoading(modifier: Modifier = Modifier, label: String? = null) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KitTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm),
    ) {
        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
        if (label != null) Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun StateScaffold(
    icon: ImageVector,
    title: String,
    message: String?,
    actionText: String?,
    onAction: (() -> Unit)?,
    iconTint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    fullScreen: Boolean = true,
) {
    val sizing = if (fullScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()
    Box(modifier = modifier.then(sizing).padding(KitTheme.spacing.xl), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(56.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            if (message != null) {
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            if (actionText != null && onAction != null) {
                Spacer(Modifier.height(KitTheme.spacing.sm))
                KitButton(text = actionText, onClick = onAction, fillWidth = false)
            }
        }
    }
}

@Composable
fun KitEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    fullScreen: Boolean = true,
) {
    StateScaffold(
        icon = KitTheme.icons.info,
        title = title,
        message = message,
        actionText = actionText,
        onAction = onAction,
        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
        fullScreen = fullScreen,
    )
}

@Composable
fun KitErrorState(
    title: String = "Something went wrong",
    modifier: Modifier = Modifier,
    message: String? = null,
    actionText: String? = "Retry",
    onAction: (() -> Unit)? = null,
    fullScreen: Boolean = true,
) {
    StateScaffold(
        icon = KitTheme.icons.error,
        title = title,
        message = message,
        actionText = actionText,
        onAction = onAction,
        iconTint = MaterialTheme.colorScheme.error,
        modifier = modifier,
        fullScreen = fullScreen,
    )
}

@Composable
fun KitSuccessState(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    fullScreen: Boolean = true,
) {
    StateScaffold(
        icon = KitTheme.icons.success,
        title = title,
        message = message,
        actionText = actionText,
        onAction = onAction,
        iconTint = KitTheme.colors.success,
        modifier = modifier,
        fullScreen = fullScreen,
    )
}
