package com.theis.agrinovis.data.local.database.converters

import androidx.room.TypeConverter
import com.theis.agrinovis.data.model.NotificationType

class NotificationConverters {
    @TypeConverter
    fun fromNotificationType(type: NotificationType): String {
        return type.name
    }

    @TypeConverter
    fun toNotificationType(value: String): NotificationType {
        return NotificationType.valueOf(value)
    }
}
