package com.theis.agrinovis.data.session

import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.data.model.WeatherResponse

object MonitoringSession {
    var currentPlant: PlantEntity? = null
    var latestWeather: WeatherResponse? = null
    var diameterImageUri: String? = null
    var heightImageUri: String? = null
}
