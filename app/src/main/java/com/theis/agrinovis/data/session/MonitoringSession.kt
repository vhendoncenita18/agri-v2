package com.theis.agrinovis.data.session

import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.data.model.WeatherResponse
import com.theis.agrinovis.ml.cv.CvResult

object MonitoringSession {
    var currentPlant: PlantEntity? = null
    var latestWeather: WeatherResponse? = null
    var isOfflineMode: Boolean = false
    var offlineUserEmail: String? = null
    var diameterImageUri: String? = null
    var heightImageUri: String? = null
    var diameterCvResult: CvResult? = null
    var heightCvResult: CvResult? = null

    fun clearSession() {
        currentPlant = null
        diameterImageUri = null
        heightImageUri = null
        diameterCvResult = null
        heightCvResult = null
    }
}
