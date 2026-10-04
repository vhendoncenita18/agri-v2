package com.theis.agrinovis.data.remote

object WeatherConfig {
    // Replace with your active OpenWeatherMap API key to fetch live local weather matching Google Weather
    const val API_KEY: String = "596516483c36f22c925e006328ead21a"
    
    // GPS location timeout in milliseconds (increased to 4 seconds to ensure reliable hardware lock)
    const val GPS_TIMEOUT_MS: Long = 4000L
}
