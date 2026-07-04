package com.freelance.timetracker.core.designsystem.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.freelance.timetracker.core.designsystem.components.KitBottomSheet
import com.freelance.timetracker.core.designsystem.components.KitButton
import com.freelance.timetracker.core.designsystem.components.KitButtonStyle
import com.freelance.timetracker.core.designsystem.theme.KitTheme

/**
 * Permission-priming rationale UI — show this BEFORE triggering the system permission
 * dialog so the user understands why the app needs access (higher grant rates, and a
 * graceful path when the OS dialog can no longer be shown).
 *
 * Pure presentation: pair it with `rememberKitPermissionState` from
 * `com.freelance.timetracker.core.permission`. See [KitPermissionPrimerSheet] for the bottom-sheet form.
 */
@Composable
fun KitPermissionPrimer(
    icon: ImageVector,
    title: String,
    rationale: String,
    allowLabel: String,
    dismissLabel: String,
    onAllow: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(KitTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(KitTheme.spacing.md))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(KitTheme.spacing.sm))
        Text(
            text = rationale,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(KitTheme.spacing.lg))
        KitButton(text = allowLabel, onClick = onAllow)
        Spacer(Modifier.height(KitTheme.spacing.xs))
        KitButton(text = dismissLabel, onClick = onDismiss, style = KitButtonStyle.TEXT)
    }
}

/** [KitPermissionPrimer] presented inside a dismissible bottom sheet. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitPermissionPrimerSheet(
    icon: ImageVector,
    title: String,
    rationale: String,
    allowLabel: String,
    dismissLabel: String,
    onAllow: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitBottomSheet(onDismiss = onDismiss, modifier = modifier) {
        KitPermissionPrimer(
            icon = icon,
            title = title,
            rationale = rationale,
            allowLabel = allowLabel,
            dismissLabel = dismissLabel,
            onAllow = onAllow,
            onDismiss = onDismiss,
        )
    }
}
