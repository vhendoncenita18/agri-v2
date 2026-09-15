package com.theis.agrinovis.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.theis.agrinovis.data.model.User
import com.theis.agrinovis.data.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _userState = MutableStateFlow(
        User(
            name = "Farmer John Doe",
            farmerId = "F-2026-AGRI",
            email = "john.doe@agrinovis.io",
            avatarUrl = null
        )
    )
    val userState: StateFlow<User> = _userState.asStateFlow()

    fun toggleNotifications(enabled: Boolean) {
        _userState.update { 
            it.copy(preferences = it.preferences.copy(notificationsEnabled = enabled))
        }
    }

    fun updateMeasurementUnit(unit: String) {
        _userState.update {
            it.copy(preferences = it.preferences.copy(measurementUnit = unit))
        }
    }
}
