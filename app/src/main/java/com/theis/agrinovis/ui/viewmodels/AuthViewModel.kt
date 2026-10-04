package com.theis.agrinovis.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.theis.agrinovis.data.local.database.AgrinovisDatabase
import com.theis.agrinovis.data.local.entities.UserAccountEntity
import com.theis.agrinovis.data.session.MonitoringSession
import com.theis.agrinovis.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object SessionRestored : AuthState()
    object OfflineMode : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AgrinovisDatabase.getDatabase(application).userDao()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkExistingSession()
    }

    fun checkExistingSession() {
        viewModelScope.launch {
            try {
                val currentUser = SupabaseClient.client.auth.currentUserOrNull()
                if (currentUser != null) {
                    _authState.value = AuthState.SessionRestored
                }
            } catch (_: Exception) {
                // Session check failed or unauthenticated
            }
        }
    }

    fun signUp(fullName: String, email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    password = pass
                    data = buildJsonObject {
                        put("full_name", fullName)
                    }
                }
                userDao.insertUser(UserAccountEntity(email = email, passwordHash = pass, fullName = fullName))
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                // If offline during signup, cache locally so they can log in offline with this account
                try {
                    userDao.insertUser(UserAccountEntity(email = email, passwordHash = pass, fullName = fullName))
                    _authState.value = AuthState.Success
                } catch (_: Exception) {
                    _authState.value = AuthState.Error(formatAuthError(e.message))
                }
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    password = pass
                }
                // Save/update locally upon successful online login
                try {
                    userDao.insertUser(UserAccountEntity(email = email, passwordHash = pass, fullName = email.substringBefore("@")))
                } catch (_: Exception) {}

                MonitoringSession.isOfflineMode = false
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                // Fallback to offline local account check when online login fails (e.g. no internet)
                try {
                    val localUser = userDao.getUserByEmail(email)
                    if (localUser != null && localUser.passwordHash == pass) {
                        MonitoringSession.isOfflineMode = true
                        MonitoringSession.offlineUserEmail = email
                        _authState.value = AuthState.Success
                    } else {
                        _authState.value = AuthState.Error(formatAuthError(e.message))
                    }
                } catch (dbEx: Exception) {
                    _authState.value = AuthState.Error(formatAuthError(e.message))
                }
            }
        }
    }

    fun loginOffline(email: String = "") {
        viewModelScope.launch {
            MonitoringSession.isOfflineMode = true
            MonitoringSession.offlineUserEmail = email.ifBlank { "offline@agrinovis.io" }
            _authState.value = AuthState.OfflineMode
        }
    }

    private fun formatAuthError(msg: String?): String {
        if (msg == null) return "Authentication failed. Please try again."
        val lowerMsg = msg.lowercase()
        return when {
            lowerMsg.contains("unable to resolve host") || lowerMsg.contains("connect") || lowerMsg.contains("network") ->
                "Network error: Unable to connect to server. If you have an existing account, your offline credentials will be verified locally."
            lowerMsg.contains("over_email_send_rate_limit") || lowerMsg.contains("rate limit") -> 
                "Email rate limit exceeded. Please wait a few minutes before trying again."
            lowerMsg.contains("invalid login credentials") -> 
                "Email or password is incorrect."
            lowerMsg.contains("already registered") || lowerMsg.contains("user already exists") -> 
                "An account with this email already exists."
            lowerMsg.contains("email not confirmed") -> 
                "Please check your email to confirm your account."
            else -> {
                if (msg.contains("URL:") || msg.contains("Headers:")) {
                    msg.substringBefore("\nURL:").substringBefore("URL:").trim()
                } else {
                    msg
                }
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
