package com.theis.agrinovis.data.model

data class User(
    val name: String,
    val farmerId: String,
    val email: String,
    val avatarUrl: String? = null,
    val preferences: UserPreferences = UserPreferences()
)

data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val measurementUnit: String = "Metric (cm)",
    val themeMode: String = "Dark Hologram"
)
