package com.theis.agrinovis.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.theis.agrinovis.ui.theme.getStageColorTheme
import com.theis.agrinovis.ui.utils.PlantUtils

/**
 * High-performance, lightweight holographic plant preview for grid item list cards.
 * Loads the actual 2D JPEG pictures from the `2d-picture` asset folder matching variety and stage,
 * overlaid with a subtle futuristic holographic scanline effect.
 */
@Composable
fun HolographicPlantPreview(
    modifier: Modifier = Modifier,
    stage: String = "Vegetative",
    variety: String = "Olmetie"
) {
    val stageTheme = getStageColorTheme(stage)
    val assetPath = PlantUtils.get2dPicturePathForVarietyAndStage(variety, stage)
    val imageUrl = "file:///android_asset/$assetPath"

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
        // Load actual 2D JPEG picture from assets
        AsyncImage(
            model = imageUrl,
            contentDescription = "$variety $stage preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Subtle scanline canvas overlay for futuristic hologram feel
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
    }
}
