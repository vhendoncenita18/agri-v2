package com.theis.agrinovis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    val userId: String? = null,
    val variety: String,
    val stage: String,
    val plantedDate: String,
    val dap: Int,
    val leafCount: Int,
    val height: Double,
    val diameter: Double,
    val temp: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val rainfall: Double,
    val condition: String,
    val harvestStatus: String,
    val estHarvestDate: String,
    val daysToHarvest: Int,
    val isSynced: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
