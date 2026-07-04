package com.freelance.timetracker.core.designsystem.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.freelance.timetracker.core.designsystem.components.KitButton
import com.freelance.timetracker.core.designsystem.components.KitButtonStyle
import com.freelance.timetracker.core.designsystem.theme.KitTheme
import com.freelance.timetracker.core.permission.KitPermission
import com.freelance.timetracker.core.permission.KitPermissionStatus
import com.freelance.timetracker.core.permission.rememberKitPermissionState

/**
 * Full-screen permission request flow. Mirrors SwiftStarterKits' per-permission "view"
 * screens but parameterised — pass the [permission] + copy strings and you get the same
 * shape (icon chip → title → rationale → Allow + Skip buttons) with the permission
 * actually wired to the system request.
 *
 * The button label + behaviour swap automatically when the permission is permanently
 * denied: "Allow" becomes "Open Settings" and tap opens the app's system-settings page
 * (the OS dialog won't appear again at that point).
 *
 * State callbacks:
 *  - [onGranted] fires once the user grants — typical pattern: navigate forward.
 *  - [onSkip] fires when the user taps Skip — typical pattern: navigate forward without granting.
 *
 * Pre-instantiated variants for the eight common permissions live in
 * `com.freelance.timetracker.feature.permissions`. Drop one of those into your nav graph when
 * a feature needs the matching access.
 */
@Composable
fun KitPermissionScreen(
    permission: KitPermission,
    icon: ImageVector,
    title: String,
    rationale: String,
    rationaleDenied: String,
    allowLabel: String,
    skipLabel: String,
    openSettingsLabel: String,
    onGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberKitPermissionState(permission)
    var attempted by remember { mutableStateOf(false) }

    // The OS request fires asynchronously; trip onGranted once status flips while we're showing.
    LaunchedEffect(state.status) {
        if (state.isGranted) onGranted()
    }

    val isPermanentlyDenied = state.status == KitPermissionStatus.PERMANENTLY_DENIED
    val primaryLabel = if (isPermanentlyDenied) openSettingsLabel else allowLabel
    val body = if (isPermanentlyDenied) rationaleDenied else rationale

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(KitTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(KitTheme.spacing.xxl))
        Spacer(Modifier.weight(0.4f))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp),
            )
        }

        Spacer(Modifier.height(KitTheme.spacing.xl))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(KitTheme.spacing.md))

        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.85f),
        )

        Spacer(Modifier.weight(1f))

        KitButton(
            text = primaryLabel,
            onClick = {
                attempted = true
                if (isPermanentlyDenied) state.openAppSettings() else state.requestPermission()
            },
        )

        Spacer(Modifier.height(KitTheme.spacing.sm))

        KitButton(
            text = skipLabel,
            onClick = onSkip,
            style = KitButtonStyle.TEXT,
        )

        Spacer(Modifier.height(KitTheme.spacing.lg))
    }
}
