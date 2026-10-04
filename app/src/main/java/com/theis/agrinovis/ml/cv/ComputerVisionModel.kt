package com.theis.agrinovis.ml.cv

import android.graphics.Bitmap

/**
 * Mode of measurement extraction for the Computer Vision model.
 */
enum class CvMode {
    DIAMETER,
    HEIGHT,
    BOTH
}

/**
 * Interface contract for Computer Vision extraction models.
 */
interface ComputerVisionModel {
    /**
     * Processes an input bitmap image to automatically extract lettuce physical dimensions.
     *
     * @param bitmap Image captured by camera or loaded from storage.
     * @param mode Extraction target (DIAMETER, HEIGHT, or BOTH).
     * @return [CvResult] containing extracted dimensions or execution failure state.
     */
    suspend fun processImage(bitmap: Bitmap, mode: CvMode = CvMode.BOTH): CvResult
}
