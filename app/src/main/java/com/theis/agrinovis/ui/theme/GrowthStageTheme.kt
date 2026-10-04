package com.theis.agrinovis.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SeedlingCyan = Color(0xFF00E5FF)
val VegetativePurple = Color(0xFFB388FF)
val HeadingAmber = Color(0xFFFFAB00)
val MaturityGreen = NeonGreen
val ReadyToHarvestGreen = NeonGreen
val HarvestedGray = Color(0xFF9E9E9E)

data class GrowthStageColorTheme(
    val accentColor: Color,
    val containerColor: Color = accentColor.copy(alpha = 0.12f),
    val borderColor: Color = accentColor.copy(alpha = 0.30f),
    val icon: String,
    val displayName: String
)

fun getStageColorTheme(stage: String?): GrowthStageColorTheme {
    val normalized = stage?.trim()?.uppercase() ?: ""
    return when {
        normalized.contains("SEEDLING") -> GrowthStageColorTheme(
            accentColor = SeedlingCyan,
            icon = "🌱",
            displayName = "Seedling"
        )
        normalized.contains("VEGETATIVE") ||
        normalized.contains("EARLY GROWTH") -> GrowthStageColorTheme(
            accentColor = VegetativePurple,
            icon = "🌿",
            displayName = if (normalized.isBlank()) "Vegetative" else stage!!.trim().lowercase().replaceFirstChar { it.uppercase() }
        )
        normalized.contains("HEADING") ||
        normalized.contains("DEVELOPING") ||
        normalized.contains("GROWING") -> GrowthStageColorTheme(
            accentColor = HeadingAmber,
            icon = "🥬",
            displayName = if (normalized.isBlank()) "Heading" else stage!!.trim().lowercase().replaceFirstChar { it.uppercase() }
        )
        normalized.contains("MATURITY") || normalized == "MATURE" -> GrowthStageColorTheme(
            accentColor = MaturityGreen,
            icon = "🌾",
            displayName = "Maturity"
        )
        normalized.contains("READY") || normalized.contains("HARVEST READY") -> GrowthStageColorTheme(
            accentColor = ReadyToHarvestGreen,
            icon = "🥬",
            displayName = "Ready to Harvest"
        )
        normalized.contains("HARVESTED") -> GrowthStageColorTheme(
            accentColor = HarvestedGray,
            icon = "📦",
            displayName = "Harvested"
        )
        else -> GrowthStageColorTheme(
            accentColor = NeonGreen,
            icon = "🥬",
            displayName = if (stage.isNullOrBlank()) "Unspecified" else stage.trim()
        )
    }
}

@Composable
fun GrowthStageBadge(
    stage: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    fontSize: TextUnit = 8.sp,
    horizontalPadding: Dp = 8.dp,
    verticalPadding: Dp = 2.dp
) {
    val theme = getStageColorTheme(stage)
    Surface(
        modifier = modifier,
        color = theme.containerColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, theme.borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (showIcon) {
                Text(
                    text = theme.icon,
                    fontSize = fontSize
                )
            }
            Text(
                text = stage.ifBlank { theme.displayName }.uppercase(),
                fontSize = fontSize,
                color = theme.accentColor,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}
