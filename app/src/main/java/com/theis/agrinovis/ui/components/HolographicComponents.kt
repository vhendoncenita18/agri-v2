package com.theis.agrinovis.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theis.agrinovis.ui.theme.GlassBackground
import com.theis.agrinovis.ui.theme.NeonGreen

@Composable
fun HolographicCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassBackground)
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = title.uppercase(),
                fontWeight = FontWeight.Black,
                color = NeonGreen,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            content()
        }
    }
}

@Composable
fun HolographicDetailItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 13.sp, color = Color.White)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
fun ShimmerSkeletonItem(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 100.dp
) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.05f),
        Color.White.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.05f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun futuristicTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NeonGreen,
    unfocusedBorderColor = NeonGreen.copy(alpha = 0.4f),
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White,
    focusedTextColor = Color.White.copy(alpha = 0.7f),
    unfocusedTextColor = Color.White.copy(alpha = 0.7f),
    disabledTextColor = Color.White.copy(alpha = 0.5f),
    focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
    disabledLabelColor = Color.White.copy(alpha = 0.8f),
    disabledPlaceholderColor = Color.White.copy(alpha = 0.5f),
    cursorColor = NeonGreen
)
