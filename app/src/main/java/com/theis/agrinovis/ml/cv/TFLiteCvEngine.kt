package com.theis.agrinovis.ml.cv

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.DataType
import java.io.IOException
import java.nio.MappedByteBuffer

/**
 * TensorFlow Lite execution engine for Computer Vision dimension extraction.
 *
 * Loads `models/cv_model.tflite` from APK assets and executes inference on camera frame Bitmaps.
 */
class TFLiteCvEngine(private val context: Context) : ComputerVisionModel {

    companion object {
        private const val TAG = "TFLiteCvEngine"
        const val MODEL_FILE = "models/cv_model.tflite"
        const val INPUT_IMAGE_WIDTH = 224
        const val INPUT_IMAGE_HEIGHT = 224
        const val MODEL_VERSION = "v1.0-tflite-cv"
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
                setNumThreads(4)
            }
            interpreter = Interpreter(modelBuffer, options)
            isModelLoaded = true
            Log.i(TAG, "Computer Vision TFLite model loaded successfully from $MODEL_FILE")
        } catch (e: IOException) {
            Log.w(TAG, "CV TFLite model file '$MODEL_FILE' not found in assets. Engine ready for model drop-in: ${e.message}")
            isModelLoaded = false
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing CV TFLite Interpreter: ${e.message}", e)
            isModelLoaded = false
        }
    }

    override suspend fun processImage(bitmap: Bitmap, mode: CvMode): CvResult = withContext(Dispatchers.Default) {
        if (!isModelLoaded || interpreter == null) {
            return@withContext CvResult(
                isSuccessful = false,
                errorMessage = "CV model asset ('$MODEL_FILE') is not available. Please drop the trained .tflite model into app/src/main/assets/models/."
            )
        }

        try {
            // 1. Image Preprocessing pipeline
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(INPUT_IMAGE_HEIGHT, INPUT_IMAGE_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
                .build()

            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage = imageProcessor.process(tensorImage)

            // 2. Prepare output buffers (Expected shape: [1, 3] -> [diameter, height, confidence])
            val outputBuffer = Array(1) { FloatArray(3) }

            // 3. Execute inference
            interpreter?.run(tensorImage.buffer, outputBuffer)

            val rawOutputs = outputBuffer[0]
            val extractedDiameter = rawOutputs.getOrNull(0) ?: 0f
            val extractedHeight = rawOutputs.getOrNull(1) ?: 0f
            val confidenceScore = rawOutputs.getOrNull(2) ?: 0f

            CvResult(
                diameterCm = extractedDiameter,
                heightCm = extractedHeight,
                confidence = confidenceScore,
                modelVersion = MODEL_VERSION,
                isSuccessful = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during CV TFLite inference execution: ${e.message}", e)
            CvResult(
                isSuccessful = false,
                errorMessage = "CV Inference error: ${e.localizedMessage}"
            )
        }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
        isModelLoaded = false
    }
}
