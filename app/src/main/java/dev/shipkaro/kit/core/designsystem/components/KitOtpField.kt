package dev.shipkaro.kit.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Single-line OTP code entry — renders [length] equal-sized cells reflecting
 * the typed characters. One actual transparent [BasicTextField] sits invisibly
 * on top to receive input; the cells are pure rendering. Calling [onValueChange]
 * with strings longer than [length] is silently clamped.
 *
 * Suitable for 4/5/6-digit SMS / email codes. Use [KitTextField] for free-form
 * input.
 */
@Composable
fun KitOtpField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    keyboardType: KeyboardType = KeyboardType.NumberPassword,
    enabled: Boolean = true,
) {
    val clamped = value.take(length)
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            repeat(length) { index ->
                val char = clamped.getOrNull(index)?.toString().orEmpty()
                val focused = index == clamped.length
                val borderColor = if (focused) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = if (focused) 2.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(10.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = char,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        BasicTextField(
            value = clamped,
            onValueChange = { onValueChange(it.filter(Char::isLetterOrDigit).take(length)) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            enabled = enabled,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = TextStyle(color = androidx.compose.ui.graphics.Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .size(width = 1.dp, height = 56.dp)
                .background(androidx.compose.ui.graphics.Color.Transparent),
            decorationBox = { inner ->
                // Hide BasicTextField rendering — cells above already show value.
                Box(Modifier.fillMaxWidth()) { inner() }
            },
        )
    }
}
