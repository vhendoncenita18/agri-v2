package com.theis.agrinovis.data.model

data class NotificationModel(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSystemNotification: Boolean = false,
    val isRead: Boolean = false,
    val type: NotificationType
)

enum class NotificationType {
    HARVEST, CLIMATE, SYSTEM
}
