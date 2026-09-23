package com.theis.agrinovis.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theis.agrinovis.data.model.User
import com.theis.agrinovis.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _userState = MutableStateFlow(
        User(
            name = "Farmer",
            farmerId = "F-2026-GUEST",
            email = "offline@agrinovis.io",
            avatarUrl = null
        )
    )
    val userState: StateFlow<User> = _userState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val currentUser = SupabaseClient.client.auth.currentUserOrNull()
            if (currentUser != null) {
                val email = currentUser.email ?: "farmer@agrinovis.io"
                val nameFromEmail = email.substringBefore("@")
                    .split(".", "_", "-")
                    .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
                val farmerId = "F-2026-" + currentUser.id.take(8).uppercase()

                _userState.update { current ->
                    current.copy(
                        name = "Farmer $nameFromEmail",
                        farmerId = farmerId,
                        email = email
                    )
                }
            }
        }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onComplete()
            }
        }
    }

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
