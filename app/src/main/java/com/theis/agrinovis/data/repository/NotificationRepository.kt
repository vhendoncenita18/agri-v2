package com.theis.agrinovis.data.repository

import com.theis.agrinovis.data.local.dao.NotificationDao
import com.theis.agrinovis.data.local.entities.NotificationEntity
import com.theis.agrinovis.data.model.NotificationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepository(private val notificationDao: NotificationDao) {
    val allNotifications: Flow<List<NotificationModel>> = notificationDao.getAllNotifications().map { entities ->
        entities.map { it.toModel() }
    }

    val unreadCount: Flow<Int> = notificationDao.getUnreadCount()

    suspend fun insert(notification: NotificationModel) {
        notificationDao.insertNotification(notification.toEntity())
    }

    suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    private fun NotificationEntity.toModel() = NotificationModel(
        id = id,
        title = title,
        message = message,
        timestamp = timestamp,
        isSystemNotification = isSystemNotification,
        isRead = isRead,
        type = type
    )

    private fun NotificationModel.toEntity() = NotificationEntity(
        id = id,
        title = title,
        message = message,
        timestamp = timestamp,
        isSystemNotification = isSystemNotification,
        isRead = isRead,
        type = type
    )
}
