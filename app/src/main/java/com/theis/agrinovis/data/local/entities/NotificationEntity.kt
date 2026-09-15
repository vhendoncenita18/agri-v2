package com.theis.agrinovis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theis.agrinovis.data.model.NotificationType

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isSystemNotification: Boolean,
    val isRead: Boolean,
    val type: NotificationType
)
