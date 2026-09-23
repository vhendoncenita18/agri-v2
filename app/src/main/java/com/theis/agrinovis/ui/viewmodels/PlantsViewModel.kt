package com.theis.agrinovis.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.theis.agrinovis.data.local.database.AgrinovisDatabase
import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.data.local.entities.ActivityLogEntity
import com.theis.agrinovis.data.repository.PlantRepository
import com.theis.agrinovis.data.repository.SyncStatus
import com.theis.agrinovis.data.remote.StorageService
import com.theis.agrinovis.data.session.MonitoringSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SavePlantState {
    object Idle : SavePlantState()
    object Loading : SavePlantState()
    data class Success(val plantId: String, val message: String) : SavePlantState()
    data class Error(val message: String) : SavePlantState()
}

class PlantsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlantRepository
    private val activityDao = AgrinovisDatabase.getDatabase(application).activityLogDao()

    val allPlants: StateFlow<List<PlantEntity>>
    val allActivities: StateFlow<List<ActivityLogEntity>>

    private val _savePlantState = MutableStateFlow<SavePlantState>(SavePlantState.Idle)
    val savePlantState: StateFlow<SavePlantState> = _savePlantState.asStateFlow()

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

        // Automatically sync latest records from Supabase on init
        syncFromRemote()
    }

    fun savePlant(plant: PlantEntity) {
        viewModelScope.launch {
            _savePlantState.value = SavePlantState.Loading

            // 1. Upload Images to Supabase for Dataset Collection (best effort)
            uploadImages(plant.id)

            // 2. Perform Offline-First save & sync
            val result = repository.insert(plant, getApplication())

            if (result.isSuccess) {
                activityDao.insertActivity(ActivityLogEntity(action = "REGISTERED", affectedRecord = "Plant ${plant.id}"))
                
                // Clear temporary capture session
                MonitoringSession.diameterImageUri = null
                MonitoringSession.heightImageUri = null

                val status = result.getOrNull()
                val message = if (status == SyncStatus.SYNCED_ONLINE) {
                    "Plant ${plant.id} synced to Supabase online!"
                } else {
                    "Plant ${plant.id} saved locally. Will sync to Supabase when online!"
                }

                _savePlantState.value = SavePlantState.Success(plant.id, message)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to save plant."
                _savePlantState.value = SavePlantState.Error(errorMsg)
            }
        }
    }

    fun syncFromRemote() {
        viewModelScope.launch {
            repository.fetchRemotePlants()
        }
    }

    fun resetSaveState() {
        _savePlantState.value = SavePlantState.Idle
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
            repository.insert(plant, getApplication())
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
