package com.theis.agrinovis.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.theis.agrinovis.data.local.entities.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<ActivityLogEntity>>

    @Insert
    suspend fun insertActivity(activity: ActivityLogEntity)
}
