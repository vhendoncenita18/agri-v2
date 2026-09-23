package com.theis.agrinovis.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

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
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(formatAuthError(e.message))
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
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(formatAuthError(e.message))
            }
        }
    }

    private fun formatAuthError(msg: String?): String {
        if (msg == null) return "Authentication failed. Please try again."
        val lowerMsg = msg.lowercase()
        return when {
            lowerMsg.contains("unable to resolve host") || lowerMsg.contains("connect") || lowerMsg.contains("network") ->
                "Network error: Internet required for initial login. If you previously logged in, restart the app to restore your session."
            lowerMsg.contains("over_email_send_rate_limit") || lowerMsg.contains("rate limit") -> 
                "Email rate limit exceeded. Please wait a few minutes before trying again, or disable 'Confirm Email' in Supabase settings."
            lowerMsg.contains("invalid login credentials") -> 
                "Email or password is incorrect."
            lowerMsg.contains("already registered") || lowerMsg.contains("user already exists") -> 
                "An account with this email already exists."
            lowerMsg.contains("email not confirmed") -> 
                "Please check your email to confirm your account."
            else -> {
                // Strip raw HTTP request headers if present
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
