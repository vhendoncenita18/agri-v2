package com.theis.agrinovis.data.repository

import com.theis.agrinovis.data.local.dao.PlantDao
import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.data.remote.SupabaseClient
import com.theis.agrinovis.data.remote.model.toDto
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {
    val allPlants: Flow<List<PlantEntity>> = plantDao.getAllPlants()

    suspend fun insert(plant: PlantEntity) {
        plantDao.insertPlant(plant)
        syncToRemote(plant)
    }

    private suspend fun syncToRemote(plant: PlantEntity) {
        try {
            SupabaseClient.client.postgrest["plants"].upsert(plant.toDto())
        } catch (e: Exception) {
            // In a production app, we would queue this for retry
            e.printStackTrace()
        }
    }

    suspend fun delete(plant: PlantEntity) {
        plantDao.deletePlant(plant)
    }
    
    suspend fun getPlantById(id: String): PlantEntity? {
        return plantDao.getPlantById(id)
    }
}
