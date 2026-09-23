package com.theis.agrinovis.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theis.agrinovis.ui.theme.NeonGreen

@Composable
fun AgrinovisLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = size.toPx()
            val h = size.toPx()
            val cx = w / 2f
            val cy = h / 2f

            // 1. Outer Tech Scanning Hexagon Frame
            val outerHex = Path().apply {
                moveTo(cx, cy - h * 0.46f)
                lineTo(cx + w * 0.42f, cy - h * 0.22f)
                lineTo(cx + w * 0.42f, cy + h * 0.22f)
                lineTo(cx, cy + h * 0.46f)
                lineTo(cx - w * 0.42f, cy + h * 0.22f)
                lineTo(cx - w * 0.42f, cy - h * 0.22f)
                close()
            }

            // Outer Frame Fill (Subtle Glow)
            drawPath(
                path = outerHex,
                brush = Brush.radialGradient(
                    colors = listOf(NeonGreen.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = w * 0.5f
                )
            )

            // Outer Frame Dashed Stroke
            drawPath(
                path = outerHex,
                color = NeonGreen.copy(alpha = 0.6f),
                style = Stroke(
                    width = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), 0f)
                )
            )

            // 2. Corner Node Circles
            val nodePoints = listOf(
                Offset(cx, cy - h * 0.46f),
                Offset(cx + w * 0.42f, cy - h * 0.22f),
                Offset(cx + w * 0.42f, cy + h * 0.22f),
                Offset(cx, cy + h * 0.46f),
                Offset(cx - w * 0.42f, cy + h * 0.22f),
                Offset(cx - w * 0.42f, cy - h * 0.22f)
            )
            nodePoints.forEach { pt ->
                drawCircle(color = NeonGreen, radius = 3.5f, center = pt)
            }

            // 3. Central Geometric Tech Leaf Left Half
            val leftLeaf = Path().apply {
                moveTo(cx, cy - h * 0.32f)
                cubicTo(
                    cx - w * 0.35f, cy - h * 0.12f,
                    cx - w * 0.3f, cy + h * 0.25f,
                    cx, cy + h * 0.3f
                )
                close()
            }
            drawPath(
                path = leftLeaf,
                brush = Brush.verticalGradient(
                    colors = listOf(NeonGreen, NeonGreen.copy(alpha = 0.3f))
                )
            )

            // 4. Central Geometric Tech Leaf Right Half (Digital Grid Cut)
            val rightLeaf = Path().apply {
                moveTo(cx, cy - h * 0.32f)
                cubicTo(
                    cx + w * 0.35f, cy - h * 0.12f,
                    cx + w * 0.3f, cy + h * 0.25f,
                    cx, cy + h * 0.3f
                )
                close()
            }
            drawPath(
                path = rightLeaf,
                brush = Brush.verticalGradient(
                    colors = listOf(NeonGreen.copy(alpha = 0.5f), Color(0xFF00C853).copy(alpha = 0.1f))
                )
            )

            // 5. Central AI Vein / Circuit Line
            drawLine(
                color = Color.Black,
                start = Offset(cx, cy - h * 0.32f),
                end = Offset(cx, cy + h * 0.32f),
                strokeWidth = 3f
            )

            // Horizontal AI Data Nodes across the leaf
            drawLine(
                color = NeonGreen,
                start = Offset(cx - w * 0.18f, cy - h * 0.05f),
                end = Offset(cx + w * 0.18f, cy - h * 0.05f),
                strokeWidth = 2f
            )
            drawLine(
                color = NeonGreen,
                start = Offset(cx - w * 0.14f, cy + h * 0.1f),
                end = Offset(cx + w * 0.14f, cy + h * 0.1f),
                strokeWidth = 2f
            )

            // Tech Node Dots on Leaf
            drawCircle(color = Color.White, radius = 3f, center = Offset(cx, cy - h * 0.05f))
            drawCircle(color = NeonGreen, radius = 2.5f, center = Offset(cx - w * 0.18f, cy - h * 0.05f))
            drawCircle(color = NeonGreen, radius = 2.5f, center = Offset(cx + w * 0.18f, cy - h * 0.05f))
        }
    }
}
