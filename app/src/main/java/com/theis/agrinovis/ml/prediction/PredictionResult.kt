package com.theis.agrinovis.ml.prediction

/**
 * Structured result produced by the Harvest Prediction model.
 *
 * @property daysToHarvest Predicted remaining days until harvest point (Y target).
 * @property estimatedHarvestDate Calculated target harvest date string (Record Date + DaysToHarvest).
 * @property predictedStage Inferred plant growth stage.
 * @property confidence Model prediction confidence or accuracy metric.
 * @property modelVersion Version identifier of the deployed prediction model.
 * @property isSuccessful True if model executed inference successfully.
 * @property errorMessage Error description if prediction failed.
 */
data class PredictionResult(
    val daysToHarvest: Int = 0,
    val estimatedHarvestDate: String = "",
    val predictedStage: String = "Vegetative",
    val confidence: Float = 0f,
    val modelVersion: String = "v1.0-prediction",
    val isSuccessful: Boolean = false,
    val errorMessage: String? = null
)
