package dev.shipkaro.kit.core.designsystem.components

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Numeric or short-text badge overlaid on a target composable (icon / avatar /
 * button). Pass [count] for numeric, [text] for short labels (e.g. "NEW").
 * Pass neither for a plain dot.
 */
@Composable
fun KitBadge(
    modifier: Modifier = Modifier,
    count: Int? = null,
    text: String? = null,
    maxCount: Int = 99,
    containerColor: Color = MaterialTheme.colorScheme.error,
    contentColor: Color = MaterialTheme.colorScheme.onError,
    target: @Composable () -> Unit,
) {
    BadgedBox(
        badge = {
            Badge(containerColor = containerColor, contentColor = contentColor) {
                when {
                    count != null -> Text(if (count > maxCount) "$maxCount+" else count.toString())
                    !text.isNullOrBlank() -> Text(text)
                    else -> {}
                }
            }
        },
        modifier = modifier,
        content = { target() },
    )
}
