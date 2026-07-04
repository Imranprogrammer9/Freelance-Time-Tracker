package com.freelance.timetracker.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.freelance.timetracker.core.designsystem.theme.KitTheme

enum class KitButtonStyle { PRIMARY, SECONDARY, TEXT }

/**
 * Primary kit button. Supports three visual styles, optional leading icon, and a
 * loading state (replaces label with spinner; disables interaction).
 *
 * @param style PRIMARY (filled), SECONDARY (outlined), TEXT (no background).
 * @param loading shows spinner + disables click. Use during async work.
 */
@Composable
fun KitButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: KitButtonStyle = KitButtonStyle.PRIMARY,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    fillWidth: Boolean = true,
) {
    val content: @Composable () -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current,
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                    androidx.compose.foundation.layout.Spacer(Modifier.width(KitTheme.spacing.sm))
                }
                Text(text)
            }
        }
    }

    val sizing = modifier
        .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
        .height(52.dp)

    when (style) {
        KitButtonStyle.PRIMARY -> Button(
            onClick = onClick,
            modifier = sizing,
            enabled = enabled && !loading,
            content = { content() },
        )
        KitButtonStyle.SECONDARY -> OutlinedButton(
            onClick = onClick,
            modifier = sizing,
            enabled = enabled && !loading,
            content = { content() },
        )
        KitButtonStyle.TEXT -> TextButton(
            onClick = onClick,
            modifier = sizing,
            enabled = enabled && !loading,
            colors = ButtonDefaults.textButtonColors(),
            content = { content() },
        )
    }
}
