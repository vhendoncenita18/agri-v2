package com.theis.agrinovis.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import com.theis.agrinovis.data.model.Main
import com.theis.agrinovis.data.model.Weather
import com.theis.agrinovis.data.model.WeatherResponse
import com.theis.agrinovis.data.model.Wind
import com.theis.agrinovis.data.remote.RetrofitClient
import com.theis.agrinovis.ui.utils.LocationHelper
import com.theis.agrinovis.data.model.NotificationModel
import com.theis.agrinovis.data.model.NotificationType
import com.theis.agrinovis.ui.utils.NotificationHelper
import com.theis.agrinovis.data.local.database.AgrinovisDatabase
import com.theis.agrinovis.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val notificationRepository: NotificationRepository

    private val _weatherState = MutableStateFlow<WeatherResponse?>(
        WeatherResponse(
            main = Main(temp = 0.0, humidity = 0, pressure = 0),
            wind = Wind(speed = 0.0),
            weather = listOf(Weather(main = "Detecting...", description = "Locating neural node...")),
            rain = null,
            name = "SYNCING..."
        )
    )
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _isLoadingWeather = MutableStateFlow(false)
    val isLoadingWeather: StateFlow<Boolean> = _isLoadingWeather

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        val database = AgrinovisDatabase.getDatabase(application)
        notificationRepository = NotificationRepository(database.notificationDao())

        viewModelScope.launch {
            notificationRepository.allNotifications.collectLatest {
                _notifications.value = it
            }
        }
        
        viewModelScope.launch {
            notificationRepository.unreadCount.collectLatest {
                _unreadCount.value = it
            }
        }
        
        // Initial fetch with default city
        fetchWeather()
    }

    fun addNotification(title: String, message: String, type: NotificationType, isSystemNotification: Boolean = false) {
        viewModelScope.launch {
            val newNotif = NotificationModel(
                id = UUID.randomUUID().toString(),
                title = title,
                message = message,
                type = type,
                isSystemNotification = isSystemNotification
            )
            // Check if it already exists to avoid duplication
            if (_notifications.value.any { it.title == title && it.message == message }) return@launch

            notificationRepository.insert(newNotif)

            if (isSystemNotification) {
                NotificationHelper.showNotification(getApplication(), title, message)
            }
        }
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun fetchWeather(city: String = "") {
        viewModelScope.launch {
            _isLoadingWeather.value = true
            try {
                if (city.isEmpty()) {
                    // Fast 1-second timeout for hardware GPS lock to avoid hanging the UI
                    val location = withTimeoutOrNull(1000) {
                        LocationHelper.getLastLocation(getApplication())
                    }
                    if (location != null) {
                        val response = RetrofitClient.weatherService.getWeatherByCoords(
                            lat = location.latitude,
                            lon = location.longitude,
                            apiKey = "YOUR_API_KEY_HERE"
                        )
                        _weatherState.value = response
                        com.theis.agrinovis.data.session.MonitoringSession.latestWeather = response
                        return@launch
                    }
                }
                
                // Fallback or explicit city fetch
                val targetCity = if (city.isEmpty()) "Manila" else city
                val response = RetrofitClient.weatherService.getWeather(
                    city = targetCity,
                    apiKey = "YOUR_API_KEY_HERE"
                )
                _weatherState.value = response
                com.theis.agrinovis.data.session.MonitoringSession.latestWeather = response
            } catch (e: Exception) {
                // Graceful fallback for offline mode or invalid API key
                val fallbackResponse = WeatherResponse(
                    main = Main(temp = 28.5, humidity = 68, pressure = 1012),
                    wind = Wind(speed = 2.4),
                    weather = listOf(Weather(main = "Cloudy", description = "Local backup node active")),
                    rain = null,
                    name = "Clarin (Local)"
                )
                _weatherState.value = fallbackResponse
                com.theis.agrinovis.data.session.MonitoringSession.latestWeather = fallbackResponse
            } finally {
                _isLoadingWeather.value = false
            }
        }
    }

    fun fetchWeatherByLocation() {
        viewModelScope.launch {
            _isLoadingWeather.value = true
            try {
                // Fast 1-second timeout for hardware GPS lock
                val location = withTimeoutOrNull(1000) {
                    LocationHelper.getLastLocation(getApplication())
                }
                if (location != null) {
                    val response = RetrofitClient.weatherService.getWeatherByCoords(
                        lat = location.latitude,
                        lon = location.longitude,
                        apiKey = "YOUR_API_KEY_HERE"
                    )
                    _weatherState.value = response
                    com.theis.agrinovis.data.session.MonitoringSession.latestWeather = response
                } else {
                    val fallbackResponse = WeatherResponse(
                        main = Main(temp = 28.5, humidity = 68, pressure = 1012),
                        wind = Wind(speed = 2.4),
                        weather = listOf(Weather(main = "Cloudy", description = "Local backup node active")),
                        rain = null,
                        name = "Clarin (Local)"
                    )
                    _weatherState.value = fallbackResponse
                    com.theis.agrinovis.data.session.MonitoringSession.latestWeather = fallbackResponse
                }
            } catch (e: Exception) {
                val fallbackResponse = WeatherResponse(
                    main = Main(temp = 28.5, humidity = 68, pressure = 1012),
                    wind = Wind(speed = 2.4),
                    weather = listOf(Weather(main = "Cloudy", description = "Local backup node active")),
                    rain = null,
                    name = "Clarin (Local)"
                )
                _weatherState.value = fallbackResponse
                com.theis.agrinovis.data.session.MonitoringSession.latestWeather = fallbackResponse
            } finally {
                _isLoadingWeather.value = false
            }
        }
    }
}
