package com.theis.agrinovis.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.theis.agrinovis.data.local.database.AgrinovisDatabase
import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.data.local.entities.ActivityLogEntity
import com.theis.agrinovis.data.repository.PlantRepository
import com.theis.agrinovis.data.remote.StorageService
import com.theis.agrinovis.data.session.MonitoringSession
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
            // 1. Upload Images to Supabase for Dataset Collection
            uploadImages(plant.id)

            // 2. Save locally and sync DB record
            repository.insert(plant)
            activityDao.insertActivity(ActivityLogEntity(action = "REGISTERED", affectedRecord = "Plant ${plant.id}"))
            
            // 3. Clear session
            MonitoringSession.diameterImageUri = null
            MonitoringSession.heightImageUri = null
        }
    }

    private suspend fun uploadImages(plantId: String) {
        val diameterUri = MonitoringSession.diameterImageUri
        val heightUri = MonitoringSession.heightImageUri

        diameterUri?.let { uriString ->
            readUriBytes(uriString)?.let { bytes ->
                StorageService.uploadLettuceImage("diameter_$plantId.jpg", bytes)
            }
        }

        heightUri?.let { uriString ->
            readUriBytes(uriString)?.let { bytes ->
                StorageService.uploadLettuceImage("height_$plantId.jpg", bytes)
            }
        }
    }

    private fun readUriBytes(uriString: String): ByteArray? {
        return try {
            val uri = Uri.parse(uriString)
            getApplication<Application>().contentResolver.openInputStream(uri)?.use { 
                it.readBytes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
