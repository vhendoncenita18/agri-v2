package com.theis.agrinovis.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theis.agrinovis.ui.theme.getStageColorTheme

/**
 * High-performance, lightweight 2D holographic plant preview for grid item list cards.
 * Replaces heavy 3D SceneView instances in scrollable lists to eliminate GPU/CPU lag.
 */
@Composable
fun HolographicPlantPreview(
    modifier: Modifier = Modifier,
    stage: String = "Vegetative",
    variety: String = "Olmetie"
) {
    val stageTheme = getStageColorTheme(stage)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        stageTheme.accentColor.copy(alpha = 0.25f),
                        stageTheme.containerColor.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
            .border(1.dp, stageTheme.borderColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Subtle scanline canvas overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 8.dp.toPx()
            val numLines = (size.height / step).toInt()
            for (i in 0 until numLines) {
                val y = i * step
                drawLine(
                    color = stageTheme.accentColor.copy(alpha = 0.08f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Center Stage Icon & Label
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stageTheme.icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = variety.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                text = "HOLOGRAM",
                fontSize = 7.sp,
                fontWeight = FontWeight.Black,
                color = stageTheme.accentColor.copy(alpha = 0.7f),
                letterSpacing = 1.2.sp
            )
        }
    }
}
