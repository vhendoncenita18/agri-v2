package com.theis.agrinovis.ml.cv

/**
 * Data contract representing the output produced by the Computer Vision model.
 *
 * @property diameterCm Extracted lettuce diameter in centimeters.
 * @property heightCm Extracted lettuce height in centimeters.
 * @property confidence Model detection/segmentation confidence score (0.0 to 1.0).
 * @property modelVersion Deployed CV model version identifier.
 * @property isSuccessful True if model successfully extracted measurements.
 * @property errorMessage Error description if image processing or model execution failed.
 */
data class CvResult(
    val diameterCm: Float = 0f,
    val heightCm: Float = 0f,
    val confidence: Float = 0f,
    val modelVersion: String = "v1.0-cv",
    val isSuccessful: Boolean = false,
    val errorMessage: String? = null
)
