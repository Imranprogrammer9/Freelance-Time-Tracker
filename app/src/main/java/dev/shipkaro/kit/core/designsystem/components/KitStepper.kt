package dev.shipkaro.kit.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.core.designsystem.theme.KitTheme

/**
 * Integer stepper — `-` button, value read-out, `+` button. Clamps to
 * [valueRange]. Mirrors the SwiftUI Stepper component.
 *
 * For decimal / continuous values use [KitSlider] instead.
 */
@Composable
fun KitStepper(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: IntRange = 0..Int.MAX_VALUE,
    step: Int = 1,
    enabled: Boolean = true,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
            )
            IconButton(
                onClick = { onValueChange((value - step).coerceIn(valueRange)) },
                enabled = enabled && value > valueRange.first,
            ) {
                Icon(
                    imageVector = KitTheme.icons.close,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            IconButton(
                onClick = { onValueChange((value + step).coerceIn(valueRange)) },
                enabled = enabled && value < valueRange.last,
            ) {
                Icon(
                    imageVector = KitTheme.icons.add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
