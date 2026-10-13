package com.theis.agrinovis.ml.prediction

import kotlin.math.PI
import kotlin.math.max

/**
 * Constructs a 3-step, 21-feature time-series tensor `[1, 3, 21]` expected by the
 * Temporal Attention LSTM Harvest Prediction Model (`lstm_harvest_model.tflite`).
 *
 * @property observations List of exactly 3 chronological observations for a single tracked plant.
 */
data class LstmObservationSequence(
    val observations: List<PredictionInput>
) {
    init {
        require(observations.size == 3) {
            "LSTM inference requires exactly 3 chronological observations. Provided count: ${observations.size}"
        }
    }

    /**
     * Builds a 3D float tensor `[1, 3, 21]` for TFLite interpreter input.
     * Dimensions:
     * - Dimension 0: Batch size = 1
     * - Dimension 1: Sequence steps = 3 (Ascending chronological order)
     * - Dimension 2: Features = 21
     */
    fun buildInputTensor(): Array<Array<FloatArray>> {
        val sequenceMatrix = Array(3) { FloatArray(21) }

        for (t in 0 until 3) {
            val curr = observations[t]
            val prev = if (t > 0) observations[t - 1] else null

            val dapVal = if (curr.dap >= 0) curr.dap.toFloat() else 15.0f
            val leafVal = if (curr.leafCount >= 0) curr.leafCount.toFloat() else 9.0f
            val heightVal = if (curr.heightCm > 0f) curr.heightCm else 6.6f
            val diamVal = if (curr.diameterCm > 0f) curr.diameterCm else 10.7f
            val tempVal = if (curr.temperature > 0f) curr.temperature else 29.1f
            val humVal = if (curr.humidity > 0f) curr.humidity else 65.0f
            val windVal = if (curr.windSpeed >= 0f) curr.windSpeed else 3.2f
            val pressVal = if (curr.pressure > 0f) curr.pressure else 1011.7f

            // 0..7: Scaled numeric features
            val zDap = (dapVal - 16.846516f) / 8.585533f
            val zLeaf = (leafVal - 9.767171f) / 4.467764f
            val zHeight = (heightVal - 6.902604f) / 3.137056f
            val zDiam = (diamVal - 11.999256f) / 5.707681f
            val zTemp = (tempVal - 29.564667f) / 1.231027f
            val zHum = (humVal - 65.946194f) / 5.106681f
            val zWind = (windVal - 3.411629f) / 0.702986f
            val zPress = (pressVal - 1011.871411f) / 0.388169f

            // 8..11: Delta features
            val deltaDap = if (prev != null) max(0.0f, (curr.dap - prev.dap).toFloat()) else 0.0f
            val deltaHeight = if (prev != null) curr.heightCm - prev.heightCm else 0.0f
            val deltaDiam = if (prev != null) curr.diameterCm - prev.diameterCm else 0.0f
            val deltaLeaf = if (prev != null) (curr.leafCount - prev.leafCount).toFloat() else 0.0f

            // 12..15: Growth rates & Morphological features
            val heightRate = if (deltaDap > 0f) deltaHeight / deltaDap else 0.0f
            val diamRate = if (deltaDap > 0f) deltaDiam / deltaDap else 0.0f

            val hdRatioRaw = heightVal / max(0.1f, diamVal)
            val zHdRatio = (hdRatioRaw - 0.62f) / 0.25f

            val radius = diamVal / 2.0f
            val volumeProxyRaw = (PI.toFloat() * radius * radius * heightVal)
            val zVolume = (volumeProxyRaw - 1250.0f) / 950.0f

            // 16..18: Variety One-Hot Encoding (Green Coral, Olmetie, Prizehead)
            val varNorm = curr.variety.trim().lowercase()
            val isGreenCoral = if (varNorm.contains("green") || varNorm.contains("limambo")) 1.0f else 0.0f
            val isOlmetie = if (varNorm.contains("olmetie")) 1.0f else 0.0f
            val isPrizehead = if (varNorm.contains("prize") || varNorm.contains("orbital") || (!varNorm.contains("green") && !varNorm.contains("olmetie") && !varNorm.contains("limambo"))) 1.0f else 0.0f

            // 19..20: Weather Condition One-Hot Encoding
            val condNorm = curr.growthStage.lowercase() // Use stage or condition indicator
            val isClear = if (condNorm.contains("clear") || condNorm.contains("sun")) 1.0f else 0.0f
            val isCloudy = if (!condNorm.contains("clear") && !condNorm.contains("sun")) 1.0f else 0.0f

            val stepFeatures = floatArrayOf(
                zDap, zLeaf, zHeight, zDiam, zTemp, zHum, zWind, zPress,
                deltaDap, deltaHeight, deltaDiam, deltaLeaf,
                heightRate, diamRate, zHdRatio, zVolume,
                isGreenCoral, isOlmetie, isPrizehead,
                isClear, isCloudy
            )

            sequenceMatrix[t] = stepFeatures
        }

        return Array(1) { sequenceMatrix }
    }
}
