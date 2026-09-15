package com.theis.agrinovis.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp, // Slightly bigger as requested
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp,
        color = Color(0xFF333333)
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp, // Slightly bigger
        lineHeight = 18.sp,
        color = Color(0xFF757575)
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp, // Slightly bigger
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        color = Color(0xFF333333)
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp, // Slightly bigger
        lineHeight = 24.sp,
        color = Color(0xFF333333)
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp, // Slightly bigger
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = Color(0xFF333333)
    )
)
