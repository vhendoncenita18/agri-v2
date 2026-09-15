package com.theis.agrinovis.data.local.dao

import androidx.room.*
import com.theis.agrinovis.data.local.entities.PlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY timestamp DESC")
    fun getAllPlants(): Flow<List<PlantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: String): PlantEntity?
}
