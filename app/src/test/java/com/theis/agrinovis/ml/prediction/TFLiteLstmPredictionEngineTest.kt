package com.theis.agrinovis.ml.prediction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TFLiteLstmPredictionEngineTest {

    @Test(expected = IllegalArgumentException::class)
    fun `LstmObservationSequence rejects sequence with less than 3 observations`() {
        val obs1 = PredictionInput(dap = 10, variety = "Green Coral")
        val obs2 = PredictionInput(dap = 15, variety = "Green Coral")
        
        LstmObservationSequence(listOf(obs1, obs2))
    }

    @Test
    fun `buildInputTensor outputs 3D array with shape 1 x 3 x 21`() {
        val obs1 = PredictionInput(dap = 10, leafCount = 5, diameterCm = 8.0f, heightCm = 5.0f, variety = "Green Coral")
        val obs2 = PredictionInput(dap = 15, leafCount = 8, diameterCm = 12.0f, heightCm = 8.0f, variety = "Green Coral")
        val obs3 = PredictionInput(dap = 20, leafCount = 11, diameterCm = 16.0f, heightCm = 11.0f, variety = "Green Coral")

        val sequence = LstmObservationSequence(listOf(obs1, obs2, obs3))
        val tensor = sequence.buildInputTensor()

        assertNotNull(tensor)
        assertEquals(1, tensor.size)          // Batch size = 1
        assertEquals(3, tensor[0].size)       // 3 Sequence steps
        assertEquals(21, tensor[0][0].size)   // 21 Features per step
    }

    @Test
    fun `buildInputTensor calculates delta features correctly for sequence steps`() {
        val obs1 = PredictionInput(dap = 10, leafCount = 5, diameterCm = 8.0f, heightCm = 5.0f, variety = "Olmetie")
        val obs2 = PredictionInput(dap = 15, leafCount = 8, diameterCm = 12.0f, heightCm = 8.0f, variety = "Olmetie")
        val obs3 = PredictionInput(dap = 20, leafCount = 12, diameterCm = 16.0f, heightCm = 11.0f, variety = "Olmetie")

        val sequence = LstmObservationSequence(listOf(obs1, obs2, obs3))
        val tensor = sequence.buildInputTensor()

        // Step 0: deltas are 0.0
        val step0 = tensor[0][0]
        assertEquals(0.0f, step0[8], 0.001f)  // Delta DAP
        assertEquals(0.0f, step0[9], 0.001f)  // Delta Height
        assertEquals(0.0f, step0[10], 0.001f) // Delta Diameter
        assertEquals(0.0f, step0[11], 0.001f) // Delta Leaf Count

        // Step 1: deltas from step 0
        val step1 = tensor[0][1]
        assertEquals(5.0f, step1[8], 0.001f)  // Delta DAP = 15 - 10 = 5
        assertEquals(3.0f, step1[9], 0.001f)  // Delta Height = 8 - 5 = 3
        assertEquals(4.0f, step1[10], 0.001f) // Delta Diameter = 12 - 8 = 4
        assertEquals(3.0f, step1[11], 0.001f) // Delta Leaf Count = 8 - 5 = 3

        // Step 2: deltas from step 1
        val step2 = tensor[0][2]
        assertEquals(5.0f, step2[8], 0.001f)  // Delta DAP = 20 - 15 = 5
        assertEquals(3.0f, step2[9], 0.001f)  // Delta Height = 11 - 8 = 3
        assertEquals(4.0f, step2[10], 0.001f) // Delta Diameter = 16 - 12 = 4
        assertEquals(4.0f, step2[11], 0.001f) // Delta Leaf Count = 12 - 8 = 4
    }

    @Test
    fun `buildInputTensor encodes variety one hot categories correctly`() {
        val obsOlmetie = PredictionInput(dap = 10, variety = "Olmetie")
        val obsGreen = PredictionInput(dap = 15, variety = "Green Coral")
        val obsPrize = PredictionInput(dap = 20, variety = "Prizehead")

        val sequence = LstmObservationSequence(listOf(obsOlmetie, obsGreen, obsPrize))
        val tensor = sequence.buildInputTensor()

        // Olmetie step: [0.0, 1.0, 0.0]
        assertEquals(0.0f, tensor[0][0][16], 0.001f) // Green Coral
        assertEquals(1.0f, tensor[0][0][17], 0.001f) // Olmetie
        assertEquals(0.0f, tensor[0][0][18], 0.001f) // Prizehead

        // Green Coral step: [1.0, 0.0, 0.0]
        assertEquals(1.0f, tensor[0][1][16], 0.001f) // Green Coral
        assertEquals(0.0f, tensor[0][1][17], 0.001f) // Olmetie
        assertEquals(0.0f, tensor[0][1][18], 0.001f) // Prizehead

        // Prizehead step: [0.0, 0.0, 1.0]
        assertEquals(0.0f, tensor[0][2][16], 0.001f) // Green Coral
        assertEquals(0.0f, tensor[0][2][17], 0.001f) // Olmetie
        assertEquals(1.0f, tensor[0][2][18], 0.001f) // Prizehead
    }
}
