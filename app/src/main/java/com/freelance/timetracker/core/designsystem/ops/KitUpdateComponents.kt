package com.freelance.timetracker.core.designsystem.ops

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
 * Bottom sheet announcing an available app update. Drives both update modes:
 *  - REQUIRED : pass `dismissible = false` — no "Later", cannot be swiped/back-dismissed.
 *  - OPTIONAL : pass `dismissible = true`  — shows "Later" and can be dismissed.
 *
 * [changes] are the backend-published release notes for the update; pass an empty list
 * to hide the "What's changed" block.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitUpdateSheet(
    title: String,
    message: String,
    updateLabel: String,
    laterLabel: String,
    onUpdate: () -> Unit,
    onLater: () -> Unit,
    dismissible: Boolean,
    modifier: Modifier = Modifier,
    changes: List<String> = emptyList(),
    changesLabel: String = "",
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { dismissible || it != SheetValue.Hidden },
    )
    ModalBottomSheet(
        onDismissRequest = { if (dismissible) onLater() },
        sheetState = sheetState,
        modifier = modifier,
        dragHandle = if (dismissible) {
            { BottomSheetDefaults.DragHandle() }
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = KitTheme.spacing.lg, vertical = KitTheme.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OpsIcon(KitTheme.icons.update)
            Spacer(Modifier.height(KitTheme.spacing.md))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(KitTheme.spacing.sm))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (changes.isNotEmpty()) {
                Spacer(Modifier.height(KitTheme.spacing.lg))
                ChangelogNotes(
                    notes = changes,
                    label = changesLabel,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(KitTheme.spacing.lg))
            KitButton(text = updateLabel, onClick = onUpdate)
            if (dismissible) {
                Spacer(Modifier.height(KitTheme.spacing.xs))
                KitButton(text = laterLabel, onClick = onLater, style = KitButtonStyle.TEXT)
            }
            Spacer(Modifier.height(KitTheme.spacing.sm))
        }
    }
}

/**
 * Bottom sheet shown once after the app updates, listing what changed in the version the
 * user just installed. Always dismissible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitWhatsNewSheet(
    title: String,
    versionLabel: String,
    changes: List<String>,
    dismissLabel: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KitBottomSheet(onDismiss = onDismiss, modifier = modifier) {
        OpsIcon(KitTheme.icons.star)
        Spacer(Modifier.height(KitTheme.spacing.md))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = versionLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(KitTheme.spacing.lg))
        ChangelogNotes(notes = changes, label = "", modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(KitTheme.spacing.lg))
        KitButton(text = dismissLabel, onClick = onDismiss)
        Spacer(Modifier.height(KitTheme.spacing.sm))
    }
}

/**
 * Full-screen, non-dismissible maintenance screen. [com.freelance.timetracker.core.ops.UpdateGate]
 * renders this instead of the app content while the backend maintenance flag is on.
 */
@Composable
fun KitMaintenanceScreen(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(KitTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            OpsIcon(KitTheme.icons.maintenance, size = 72.dp)
            Spacer(Modifier.height(KitTheme.spacing.lg))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(KitTheme.spacing.sm))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Tinted round icon badge used at the top of the ops sheets/screens. */
@Composable
private fun OpsIcon(icon: ImageVector, size: androidx.compose.ui.unit.Dp = 56.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}

/** A labelled bullet list of release notes. */
@Composable
private fun ChangelogNotes(
    notes: List<String>,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
        if (label.isNotEmpty()) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        notes.forEach { note -> ChangelogBullet(note) }
    }
}

/** A single release-note bullet. Reused by the changelog screen. */
@Composable
fun ChangelogBullet(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
