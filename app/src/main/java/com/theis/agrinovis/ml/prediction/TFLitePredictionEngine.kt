package com.theis.agrinovis.ml.prediction

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.IOException
import java.nio.MappedByteBuffer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * TensorFlow Lite execution engine for Harvest Prediction (Days to Harvest - Y target).
 *
 * Loads `models/harvest_prediction_model.tflite` from APK assets and executes regression inference.
 */
class TFLitePredictionEngine(private val context: Context) : PredictionModel {

    companion object {
        private const val TAG = "TFLitePredictionEngine"
        const val MODEL_FILE = "models/harvest_prediction_model.tflite"
        const val NUM_FEATURES = 8
        const val MODEL_VERSION = "v1.0-tflite-prediction"
    }

    private var interpreter: Interpreter? = null
    private var isModelLoaded = false

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val modelBuffer: MappedByteBuffer = FileUtil.loadMappedFile(context, MODEL_FILE)
            val options = Interpreter.Options().apply {
                setNumThreads(2)
            }
            interpreter = Interpreter(modelBuffer, options)
            isModelLoaded = true
            Log.i(TAG, "Harvest Prediction TFLite model loaded successfully from $MODEL_FILE")
        } catch (e: IOException) {
            Log.w(TAG, "Prediction TFLite model file '$MODEL_FILE' not found in assets. Engine ready for model drop-in: ${e.message}")
            isModelLoaded = false
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Prediction TFLite Interpreter: ${e.message}", e)
            isModelLoaded = false
        }
    }

    override suspend fun predict(input: PredictionInput): PredictionResult = withContext(Dispatchers.Default) {
        if (!isModelLoaded || interpreter == null) {
            return@withContext PredictionResult(
                isSuccessful = false,
                errorMessage = "Prediction model asset ('$MODEL_FILE') is not available. Please drop the trained .tflite model into app/src/main/assets/models/."
            )
        }

        try {
            // 1. Convert features to 2D Float tensor input: [1, NUM_FEATURES]
            val featureArray = input.toFeatureArray()
            val inputTensor = Array(1) { featureArray }

            // 2. Output tensor buffer: [1, 1] -> [Predicted Days To Harvest]
            val outputTensor = Array(1) { FloatArray(1) }

            // 3. Execute model inference
            interpreter?.run(inputTensor, outputTensor)

            val rawPredictedDays = outputTensor[0][0]
            val predictedDaysToHarvest = max(0, rawPredictedDays.roundToInt())

            // 4. Calculate estimated harvest date (Record Date + DaysToHarvest)
            val calculatedHarvestDate = calculateHarvestDate(input.recordDateIso, predictedDaysToHarvest)

            // 5. Determine growth stage based on DAP and remaining days
            val calculatedStage = inferGrowthStage(input.dap, predictedDaysToHarvest)

            PredictionResult(
                daysToHarvest = predictedDaysToHarvest,
                estimatedHarvestDate = calculatedHarvestDate,
                predictedStage = calculatedStage,
                confidence = 0.92f, // Placeholder metric updated upon research validation
                modelVersion = MODEL_VERSION,
                isSuccessful = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during Prediction TFLite inference execution: ${e.message}", e)
            PredictionResult(
                isSuccessful = false,
                errorMessage = "Prediction Inference error: ${e.localizedMessage}"
            )
        }
    }

    private fun calculateHarvestDate(recordDateIso: String, daysToAdd: Int): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val calendar = Calendar.getInstance()
            if (recordDateIso.isNotBlank()) {
                val parsedDate = sdf.parse(recordDateIso)
                if (parsedDate != null) {
                    calendar.time = parsedDate
                }
            }
            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
            outputFormat.format(calendar.time)
        } catch (e: Exception) {
            Log.w(TAG, "Date parsing fallback for '$recordDateIso': ${e.message}")
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
            SimpleDateFormat("MMM dd, yyyy", Locale.US).format(calendar.time)
        }
    }

    private fun inferGrowthStage(dap: Int, remainingDays: Int): String {
        return when {
            dap < 10 -> "Seedling"
            dap in 10..21 -> "Vegetative"
            dap in 22..35 -> "Heading"
            remainingDays <= 5 || dap >= 40 -> "Maturity"
            else -> "Vegetative"
        }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
        isModelLoaded = false
    }
}
