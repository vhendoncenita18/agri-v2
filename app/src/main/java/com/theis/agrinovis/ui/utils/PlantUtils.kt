package com.theis.agrinovis.ui.utils

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
}
