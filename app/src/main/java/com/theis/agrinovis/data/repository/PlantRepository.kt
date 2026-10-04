package com.theis.agrinovis.data.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.theis.agrinovis.data.local.dao.PlantDao
import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.data.remote.SupabaseClient
import com.theis.agrinovis.data.remote.model.PlantDto
import com.theis.agrinovis.data.remote.model.toDto
import com.theis.agrinovis.data.remote.model.toEntity
import com.theis.agrinovis.data.worker.SyncPlantsWorker
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeoutOrNull

enum class SyncStatus {
    SYNCED_ONLINE,
    SAVED_LOCALLY_PENDING_SYNC
}

class PlantRepository(private val plantDao: PlantDao) {
    val allPlants: Flow<List<PlantEntity>> = plantDao.getAllPlants()

    suspend fun insert(plant: PlantEntity, context: Context? = null): Result<SyncStatus> {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        val plantWithUser = if (plant.userId.isNullOrEmpty() && user != null) {
            plant.copy(userId = user.id)
        } else {
            plant
        }

        // 1. INSTANT LOCAL SAVE TO ROOM DB (isSynced = false)
        val localPlant = plantWithUser.copy(isSynced = false)
        plantDao.insertPlant(localPlant)
        Log.d("PlantRepository", "Saved plant ${localPlant.id} locally to Room database.")

        // 2. ATTEMPT IMMEDIATE SUPABASE SYNC (with 3.5s timeout safeguard)
        return try {
            if (user != null) {
                Log.d("PlantRepository", "Attempting immediate online Supabase upload for plant ${localPlant.id}...")
                
                val synced = withTimeoutOrNull(3500L) {
                    SupabaseClient.client.postgrest["plants"].upsert(localPlant.toDto())
                    true
                }
                
                if (synced == true) {
                    // Mark as synced in local Room DB
                    plantDao.markAsSynced(localPlant.id)
                    Log.d("PlantRepository", "Successfully synced plant ${localPlant.id} to Supabase online!")
                    Result.success(SyncStatus.SYNCED_ONLINE)
                } else {
                    Log.w("PlantRepository", "Supabase online sync timed out (>3.5s). Scheduling background worker...")
                    context?.let { enqueueSyncWorker(it) }
                    Result.success(SyncStatus.SAVED_LOCALLY_PENDING_SYNC)
                }
            } else {
                // User offline or not authenticated -> Queue WorkManager background sync
                context?.let { enqueueSyncWorker(it) }
                Result.success(SyncStatus.SAVED_LOCALLY_PENDING_SYNC)
            }
        } catch (e: Exception) {
            Log.w("PlantRepository", "Immediate Supabase sync unavailable (${e.message}). Scheduling background worker...")
            context?.let { enqueueSyncWorker(it) }
            Result.success(SyncStatus.SAVED_LOCALLY_PENDING_SYNC)
        }
    }

    suspend fun syncUnsyncedPlants(): Result<Int> {
        val user = SupabaseClient.client.auth.currentUserOrNull()
            ?: return Result.failure(IllegalStateException("User not authenticated."))

        val unsyncedList = plantDao.getUnsyncedPlants()
        if (unsyncedList.isEmpty()) {
            Log.d("PlantRepository", "No pending unsynced plants found.")
            return Result.success(0)
        }

        var syncedCount = 0
        Log.d("PlantRepository", "Found ${unsyncedList.size} unsynced plant records. Syncing to Supabase...")

        for (plant in unsyncedList) {
            try {
                val plantToUpload = if (plant.userId.isNullOrEmpty()) plant.copy(userId = user.id) else plant
                
                SupabaseClient.client.postgrest["plants"].upsert(plantToUpload.toDto())
                plantDao.markAsSynced(plant.id)
                syncedCount++
                Log.d("PlantRepository", "Background sync completed for plant ${plant.id}.")
            } catch (e: Exception) {
                Log.e("PlantRepository", "Failed background sync for plant ${plant.id}: ${e.message}", e)
            }
        }

        return Result.success(syncedCount)
    }

    fun enqueueSyncWorker(context: Context) {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncPlantsWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "SyncPlantsWork",
                ExistingWorkPolicy.KEEP,
                syncWorkRequest
            )
            Log.d("PlantRepository", "Enqueued WorkManager SyncPlantsWorker with NetworkType.CONNECTED constraint.")
        } catch (e: Exception) {
            Log.e("PlantRepository", "Failed to enqueue WorkManager: ${e.message}", e)
        }
    }

    suspend fun fetchRemotePlants(): Result<List<PlantEntity>> {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        if (user == null) {
            return Result.failure(IllegalStateException("User not authenticated."))
        }

        return try {
            val remoteDtos = SupabaseClient.client.postgrest["plants"]
                .select {
                    filter {
                        eq("user_id", user.id)
                    }
                }
                .decodeList<PlantDto>()

            val entities = remoteDtos.map { it.toEntity().copy(isSynced = true) }
            entities.forEach { plantDao.insertPlant(it) }

            Log.d("PlantRepository", "Fetched and cached ${entities.size} plants from Supabase for user ${user.id}.")
            Result.success(entities)
        } catch (e: Exception) {
            Log.e("PlantRepository", "Failed to fetch plants from Supabase: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun delete(plant: PlantEntity): Result<Unit> {
        // 1. Instant Local Room Deletion
        plantDao.deletePlant(plant)
        Log.d("PlantRepository", "Deleted plant ${plant.id} locally from Room database.")

        // 2. Remote Supabase Deletion if user authenticated
        return try {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            if (user != null) {
                withTimeoutOrNull(3500L) {
                    SupabaseClient.client.postgrest["plants"].delete {
                        filter {
                            eq("id", plant.id)
                        }
                    }
                }
                Log.d("PlantRepository", "Successfully deleted plant ${plant.id} from Supabase online.")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PlantRepository", "Failed to delete plant ${plant.id} from Supabase: ${e.message}", e)
            Result.success(Unit)
        }
    }
    
    suspend fun getPlantById(id: String): PlantEntity? {
        return plantDao.getPlantById(id)
    }

    suspend fun forceSync(): Result<Int> {
        val syncResult = syncUnsyncedPlants()
        val fetchResult = fetchRemotePlants()
        return if (syncResult.isSuccess || fetchResult.isSuccess) {
            Result.success(syncResult.getOrDefault(0))
        } else {
            Result.failure(syncResult.exceptionOrNull() ?: Exception("Sync failed"))
        }
    }
}
