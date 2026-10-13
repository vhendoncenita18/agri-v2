package com.theis.agrinovis.ml.prediction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TFLitePredictionEngineTest {

    @Test
    fun `toPreprocessedFeatureVector returns exact 10 element float array`() {
        val input = PredictionInput(
            dap = 15,
            leafCount = 9,
            diameterCm = 10.7f,
            heightCm = 6.6f,
            temperature = 29.1f,
            humidity = 65f,
            windSpeed = 3.2f,
            pressure = 1011.7f,
            variety = "Limambo"
        )

        val vector = input.toPreprocessedFeatureVector()
        assertNotNull(vector)
        assertEquals(10, vector.size)
    }

    @Test
    fun `toPreprocessedFeatureVector encodes mean values close to zero`() {
        // Mean values from training metadata
        val input = PredictionInput(
            dap = 17, // ~16.84
            leafCount = 10, // ~9.76
            diameterCm = 12.0f, // ~11.99
            heightCm = 6.9f, // ~6.90
            temperature = 29.56f,
            humidity = 65.95f,
            windSpeed = 3.41f,
            pressure = 1011.87f,
            variety = "Limambo"
        )

        val vector = input.toPreprocessedFeatureVector()

        // Scaled values for inputs close to training mean should be close to 0.0
        assertEquals(0.017f, vector[0], 0.1f) // DAP
        assertEquals(0.052f, vector[1], 0.1f) // Leaf Count
        assertEquals(0.000f, vector[2], 0.1f) // Height
        assertEquals(0.000f, vector[3], 0.1f) // Diameter
        assertEquals(0.0f, vector[4], 0.1f) // Temp
        assertEquals(0.0f, vector[5], 0.1f) // Humidity
        assertEquals(0.0f, vector[6], 0.1f) // Wind
        assertEquals(0.0f, vector[7], 0.1f) // Pressure

        // Variety Limambo: [1.0, 0.0]
        assertEquals(1.0f, vector[8], 0.001f)
        assertEquals(0.0f, vector[9], 0.001f)
    }

    @Test
    fun `toPreprocessedFeatureVector encodes Orbital variety correctly`() {
        val input = PredictionInput(
            dap = 20,
            variety = "Orbital"
        )

        val vector = input.toPreprocessedFeatureVector()

        // Variety Orbital: [0.0, 1.0]
        assertEquals(0.0f, vector[8], 0.001f)
        assertEquals(1.0f, vector[9], 0.001f)
    }

    @Test
    fun `toPreprocessedFeatureVector falls back to default Limambo for unknown variety`() {
        val input = PredictionInput(
            dap = 10,
            variety = "UnknownVariety"
        )

        val vector = input.toPreprocessedFeatureVector()

        // Fallback Variety: [1.0, 0.0]
        assertEquals(1.0f, vector[8], 0.001f)
        assertEquals(0.0f, vector[9], 0.001f)
    }
}
