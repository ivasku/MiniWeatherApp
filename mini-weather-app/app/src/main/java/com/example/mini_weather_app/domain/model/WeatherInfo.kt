package com.example.mini_weather_app.domain.model

data class WeatherInfo(
    val cityName: String,
    val temperature: Int,
    val description: String,
    val condition: String,
    val iconCode: String
) {
    fun getIconUrl(): String = "https:$iconCode"

    fun getTemperatureDisplay(): String = "${temperature}°C"
}