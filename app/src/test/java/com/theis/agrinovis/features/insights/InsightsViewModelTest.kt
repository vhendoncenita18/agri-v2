package com.theis.agrinovis.features.insights

import android.app.Application
import com.theis.agrinovis.data.local.entities.PlantEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class InsightsViewModelTest {

    @Test
    fun calculateData_correctlyAggregatesPlantMetrics() {
        // Arrange
        val testCrops = listOf(
            PlantEntity(
                id = "L001", variety = "Olmetie", stage = "Seedling",
                plantedDate = "Sep 01", dap = 5, leafCount = 4, height = 10.0, diameter = 12.0,
                temp = 28.0, humidity = 65, pressure = 1012, windSpeed = 2.0, rainfall = 0.0,
                condition = "Clear", harvestStatus = "Growing", estHarvestDate = "Sep 30", daysToHarvest = 15
            ),
            PlantEntity(
                id = "L002", variety = "Limambo", stage = "Maturity",
                plantedDate = "Aug 15", dap = 20, leafCount = 12, height = 15.0, diameter = 18.0,
                temp = 29.0, humidity = 70, pressure = 1012, windSpeed = 3.0, rainfall = 0.0,
                condition = "Clear", harvestStatus = "Ready to Harvest", estHarvestDate = "Sep 16", daysToHarvest = 2
            ),
            // Ready to harvest plant (DAP >= 45)
            PlantEntity(
                id = "L004", variety = "Orbital", stage = "Maturity",
                plantedDate = "Jul 01", dap = 45, leafCount = 15, height = 18.0, diameter = 22.0,
                temp = 27.5, humidity = 68, pressure = 1011, windSpeed = 1.5, rainfall = 0.0,
                condition = "Cloudy", harvestStatus = "Ready to Harvest", estHarvestDate = "Sep 15", daysToHarvest = 0
            ),
            // Harvested plant - should be excluded from total active counts
            PlantEntity(
                id = "L003", variety = "Olmetie", stage = "Seedling",
                plantedDate = "Sep 10", dap = 1, leafCount = 2, height = 0.0, diameter = 0.0,
                temp = 0.0, humidity = 0, pressure = 0, windSpeed = 0.0, rainfall = 0.0,
                condition = "", harvestStatus = "Harvested", estHarvestDate = "Oct 15", daysToHarvest = 30
            )
        )

        // Act
        val insights = InsightsCalculator.calculateData(testCrops)

        // Assert
        assertEquals(3, insights.totalPlants) // L001, L002, L004 are active
        
        // nearHarvestCount: activePlants where daysToHarvest in 0..7 AND NOT ready.
        // L002 (2d) -> IS near.
        // L004 (0d) -> IS ready, so NOT near.
        assertEquals(1, insights.nearHarvestCount) 
        
        // readyToHarvestCount: dap >= 45 || daysToHarvest <= 0
        // L004 -> Ready.
        assertEquals(1, insights.readyToHarvestCount)
        
        assertEquals(1, insights.stageDistribution["Seedling"]) // L001 is active Seedling
        assertEquals(2, insights.stageDistribution["Maturity"]) // L002 and L004 are active Maturity
        
        // Assert average sizes filter out 0.0 correctly and ignore harvested plants
        assertEquals(14.33, insights.avgHeight, 0.01)
        assertEquals(17.33, insights.avgDiameter, 0.01)

        // Assert upcoming harvests chronological sorting (only active plants: L002 (2), L001 (15))
        // L004 (0) is filtered out because it's ready.
        assertEquals("L002", insights.upcomingHarvests.first().id)
        assertEquals("L001", insights.upcomingHarvests.last().id)
    }
}