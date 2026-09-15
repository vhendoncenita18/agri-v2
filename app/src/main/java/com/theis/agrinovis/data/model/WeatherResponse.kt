package com.theis.agrinovis.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("main") val main: Main,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("rain") val rain: Rain?,
    @SerializedName("name") val name: String
)

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pressure") val pressure: Int
)

data class Wind(
    @SerializedName("speed") val speed: Double
)

data class Rain(
    @SerializedName("1h") val oneHour: Double?
)

data class Weather(
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String
)
