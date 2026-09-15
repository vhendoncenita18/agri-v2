package com.theis.agrinovis.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.theis.agrinovis.data.local.database.AgrinovisDatabase
import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.data.local.entities.ActivityLogEntity
import com.theis.agrinovis.data.repository.PlantRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlantRepository
    private val activityDao = AgrinovisDatabase.getDatabase(application).activityLogDao()

    val allPlants: StateFlow<List<PlantEntity>>
    val allActivities: StateFlow<List<ActivityLogEntity>>

    init {
        val plantDao = AgrinovisDatabase.getDatabase(application).plantDao()
        repository = PlantRepository(plantDao)
        
        allPlants = repository.allPlants.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allActivities = activityDao.getAllActivities().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun savePlant(plant: PlantEntity) {
        viewModelScope.launch {
            repository.insert(plant)
            activityDao.insertActivity(ActivityLogEntity(action = "REGISTERED", affectedRecord = "Plant ${plant.id}"))
        }
    }

    fun updatePlant(plant: PlantEntity) {
        viewModelScope.launch {
            repository.insert(plant) // Room @Insert(onConflict = REPLACE) handles updates
            activityDao.insertActivity(ActivityLogEntity(action = "UPDATED", affectedRecord = "Plant ${plant.id}"))
        }
    }

    fun deletePlant(plant: PlantEntity) {
        viewModelScope.launch {
            repository.delete(plant)
            activityDao.insertActivity(ActivityLogEntity(action = "DELETED", affectedRecord = "Plant ${plant.id}"))
        }
    }
    
    suspend fun getPlantById(id: String): PlantEntity? {
        return repository.getPlantById(id)
    }
}
