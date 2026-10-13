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
    val temperature: Float = 28.5f,
    val humidity: Float = 65f,
    val windSpeed: Float = 3.2f,
    val pressure: Float = 1012f,
    val variety: String = "Limambo",
    val growthStage: String = "Vegetative",
    val recordDateIso: String = ""
) {
    /**
     * Legacy raw feature array representation.
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

    /**
     * Converts raw feature inputs into the exact 10-element preprocessed feature array
     * expected by `agrinovis_model.tflite` (StandardScaler + OneHotEncoder).
     *
     * Features:
     * 0: num__DAP (mean=16.846516, scale=8.585533, median=15.0)
     * 1: num__Leaf Count (mean=9.767171, scale=4.467764, median=9.0)
     * 2: num__Plant Height (cm) (mean=6.902604, scale=3.137056, median=6.6)
     * 3: num__Plant Diameter (cm) (mean=11.999256, scale=5.707681, median=10.7)
     * 4: num__Temp (°C) (mean=29.564667, scale=1.231027, median=29.1)
     * 5: num__Humidity (%) (mean=65.946194, scale=5.106681, median=65.0)
     * 6: num__Wind (m/s) (mean=3.411629, scale=0.702986, median=3.2)
     * 7: num__Pressure (hPa) (mean=1011.871411, scale=0.388169, median=1011.7)
     * 8: cat__Variety_Limambo (1.0f if Limambo else 0.0f)
     * 9: cat__Variety_Orbital (1.0f if Orbital else 0.0f)
     */
    fun toPreprocessedFeatureVector(): FloatArray {
        val rawDap = if (dap >= 0) dap.toFloat() else 15.0f
        val rawLeafCount = if (leafCount >= 0) leafCount.toFloat() else 9.0f
        val rawHeight = if (heightCm > 0f) heightCm else 6.6f
        val rawDiameter = if (diameterCm > 0f) diameterCm else 10.7f
        val rawTemp = if (temperature > 0f) temperature else 29.1f
        val rawHumidity = if (humidity > 0f) humidity else 65.0f
        val rawWind = if (windSpeed >= 0f) windSpeed else 3.2f
        val rawPressure = if (pressure > 0f) pressure else 1011.7f

        val scaledDap = (rawDap - 16.846516f) / 8.585533f
        val scaledLeafCount = (rawLeafCount - 9.767171f) / 4.467764f
        val scaledHeight = (rawHeight - 6.902604f) / 3.137056f
        val scaledDiameter = (rawDiameter - 11.999256f) / 5.707681f
        val scaledTemp = (rawTemp - 29.564667f) / 1.231027f
        val scaledHumidity = (rawHumidity - 65.946194f) / 5.106681f
        val scaledWind = (rawWind - 3.411629f) / 0.702986f
        val scaledPressure = (rawPressure - 1011.871411f) / 0.388169f

        val normalizedVariety = variety.trim().lowercase()
        val (isLimambo, isOrbital) = when (normalizedVariety) {
            "orbital" -> Pair(0.0f, 1.0f)
            "limambo" -> Pair(1.0f, 0.0f)
            else -> Pair(1.0f, 0.0f) // Default fallback category "Limambo"
        }

        return floatArrayOf(
            scaledDap,
            scaledLeafCount,
            scaledHeight,
            scaledDiameter,
            scaledTemp,
            scaledHumidity,
            scaledWind,
            scaledPressure,
            isLimambo,
            isOrbital
        )
    }
}
