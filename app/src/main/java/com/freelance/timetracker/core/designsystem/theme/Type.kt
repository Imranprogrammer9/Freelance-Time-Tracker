package com.freelance.timetracker.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Default Material 3 typography. Swap FontFamily for the app's font here.
private val KitFont = FontFamily.Default

val KitTypography = Typography(
    displayLarge = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Bold, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Bold, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Bold, fontSize = 36.sp),

    headlineLarge = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.SemiBold, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.SemiBold, fontSize = 24.sp),

    titleLarge = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Medium, fontSize = 14.sp),

    bodyLarge = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Normal, fontSize = 12.sp),

    labelLarge = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = KitFont, fontWeight = FontWeight.Medium, fontSize = 11.sp),
)
