package dev.shipkaro.kit.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Semantic haptic feedback helper — wraps Compose's [HapticFeedback] with
 * intent-named methods so call-sites read like the design intent ("success
 * vibrate" vs "long-press tick") instead of raw enum constants.
 *
 * Usage:
 *
 *     val haptics = rememberKitHaptics()
 *     Button(onClick = { haptics.lightTap(); save() }) { ... }
 */
class KitHaptics internal constructor(private val raw: HapticFeedback) {
    /** Short tap — quick button confirmation. */
    fun lightTap() = raw.performHapticFeedback(HapticFeedbackType.TextHandleMove)

    /** Slightly stronger — for value changes / scroll snap. */
    fun mediumTap() = raw.performHapticFeedback(HapticFeedbackType.LongPress)

    /** Long-press feedback — drag start, context menu open. */
    fun longPress() = raw.performHapticFeedback(HapticFeedbackType.LongPress)

    /** Success buzz — completed actions (save, send, purchase). */
    fun success() = raw.performHapticFeedback(HapticFeedbackType.LongPress)
}

@Composable
fun rememberKitHaptics(): KitHaptics {
    val raw = LocalHapticFeedback.current
    return KitHaptics(raw)
}
