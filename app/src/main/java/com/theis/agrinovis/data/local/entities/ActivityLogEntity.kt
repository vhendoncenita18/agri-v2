package com.theis.agrinovis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String,
    val affectedRecord: String,
    val timestamp: Long = System.currentTimeMillis(),
    val userName: String = "Farmer"
)
