package com.theis.agrinovis.ml.prediction

/**
 * Feature input vector expected by the Harvest Prediction machine learning model.
 *
 * @property dap Days After Planting.
 * @property leafCount Observed number of leaves.
 * @property diameterCm Computer Vision extracted diameter in centimeters.
 * @property heightCm Computer Vision extracted height in centimeters.
 * @property temperature Ambient temperature in °C.
 * @property humidity Relative humidity in %.
 * @property windSpeed Wind speed in m/s.
 * @property pressure Atmospheric pressure in hPa.
 * @property variety Lettuce variety name.
 * @property growthStage Current observed growth stage.
 * @property recordDateIso Record ISO date string (YYYY-MM-DD).
 */
data class PredictionInput(
    val dap: Int = 0,
    val leafCount: Int = 0,
    val diameterCm: Float = 0f,
    val heightCm: Float = 0f,
    val temperature: Float = 28f,
    val humidity: Float = 75f,
    val windSpeed: Float = 1.5f,
    val pressure: Float = 1013f,
    val variety: String = "Grand Rapids",
    val growthStage: String = "Vegetative",
    val recordDateIso: String = ""
) {
    /**
     * Converts feature input vector into float array representation for TFLite tensor input.
     * Order: [DAP, LeafCount, Diameter, Height, Temperature, Humidity, WindSpeed, Pressure]
     */
    fun toFeatureArray(): FloatArray {
        return floatArrayOf(
            dap.toFloat(),
            leafCount.toFloat(),
            diameterCm,
            heightCm,
            temperature,
            humidity,
            windSpeed,
            pressure
        )
    }
}
