package com.example.mini_weather_app.presentation.weather

import com.example.mini_weather_app.domain.model.WeatherInfo

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val errorMessage: String? = null
)