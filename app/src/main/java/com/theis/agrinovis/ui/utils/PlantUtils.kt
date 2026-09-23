package com.theis.agrinovis.ui.utils

data class StageRecommendation(
    val primaryFocus: String,
    val irrigationAdvice: String,
    val lightingAdvice: String,
    val nutrientAdvice: String,
    val monitoringTip: String
)

object PlantUtils {
    fun getModelPathForStage(stage: String): String {
        return when (stage.lowercase()) {
            "seedling" -> "models/seedling.glb"
            "vegetative" -> "models/vegetative.glb"
            "heading" -> "models/heading.glb"
            "maturity" -> "models/maturity.glb"
            "harvest ready" -> "models/maturity.glb"
            else -> "models/lettuce.glb"
        }
    }

    fun getRecommendationsForStage(stage: String): StageRecommendation {
        return when (stage.lowercase()) {
            "seedling" -> StageRecommendation(
                primaryFocus = "Root establishment & delicate leaf formation",
                irrigationAdvice = "Gentle misting twice daily; keep media consistently damp without saturation",
                lightingAdvice = "12–14 hours filtered indirect sunlight or low-heat grow LED",
                nutrientAdvice = "Mild starter solution (EC ~0.8 - 1.0 mS/cm)",
                monitoringTip = "Check daily for root rot or damping-off; keep ambient humidity ~65-75%"
            )
            "vegetative" -> StageRecommendation(
                primaryFocus = "Rapid leaf expansion & vigorous canopy growth",
                irrigationAdvice = "Regular root-level watering; increase volume as leaf area expands",
                lightingAdvice = "14–16 hours bright sunlight to fuel rapid photosynthesis",
                nutrientAdvice = "Nitrogen-rich formulation (EC ~1.4 - 1.8 mS/cm) for leafy growth",
                monitoringTip = "Inspect lower leaves for aphids; maintain steady airflow to prevent tipburn"
            )
            "heading" -> StageRecommendation(
                primaryFocus = "Inner head compaction & structural firmness",
                irrigationAdvice = "Deep ground-level watering; avoid wetting inside the tightening head",
                lightingAdvice = "Full sunlight with temperature control to prevent heat stress",
                nutrientAdvice = "Balanced Calcium & Potassium mix to strengthen leaf cell walls",
                monitoringTip = "Squeeze head lightly to gauge density; ensure ventilation inside canopy"
            )
            "maturity", "harvest ready", "ready to harvest" -> StageRecommendation(
                primaryFocus = "Harvest readiness & flavor/crispness preservation",
                irrigationAdvice = "Slightly reduce irrigation 24h prior to harvest for peak crispness",
                lightingAdvice = "Cool, shaded ambient environment to prevent bitter bolting",
                nutrientAdvice = "Flush with fresh water / low EC solution before harvesting",
                monitoringTip = "Harvest in early morning hours when plant moisture is highest"
            )
            else -> StageRecommendation(
                primaryFocus = "General crop growth monitoring",
                irrigationAdvice = "Maintain steady moisture levels appropriate for environmental conditions",
                lightingAdvice = "Provide consistent daily natural sunlight exposure",
                nutrientAdvice = "Standard hydroponic or soil nutrient balance",
                monitoringTip = "Observe leaf health and capture periodic dimensions for ML tracking"
            )
        }
    }
}
