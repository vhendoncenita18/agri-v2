package com.theis.agrinovis.ml.prediction

/**
 * Interface contract for Harvest Prediction ML models.
 */
interface PredictionModel {
    /**
     * Runs harvest prediction inference given a feature input vector.
     *
     * @param input [PredictionInput] feature values.
     * @return [PredictionResult] containing predicted days to harvest and calculated target date.
     */
    suspend fun predict(input: PredictionInput): PredictionResult
}
