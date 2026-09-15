package com.theis.agrinovis.data.repository

import com.theis.agrinovis.data.local.dao.PlantDao
import com.theis.agrinovis.data.local.entities.PlantEntity
import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {
    val allPlants: Flow<List<PlantEntity>> = plantDao.getAllPlants()

    suspend fun insert(plant: PlantEntity) {
        plantDao.insertPlant(plant)
    }

    suspend fun delete(plant: PlantEntity) {
        plantDao.deletePlant(plant)
    }
    
    suspend fun getPlantById(id: String): PlantEntity? {
        return plantDao.getPlantById(id)
    }
}
