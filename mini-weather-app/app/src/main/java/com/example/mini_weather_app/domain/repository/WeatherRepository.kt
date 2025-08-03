package com.example.mini_weather_app.domain.repository

import com.example.mini_weather_app.domain.model.NetworkResult
import com.example.mini_weather_app.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherByCity(cityName: String): Flow<NetworkResult<WeatherInfo>>
    suspend fun getLastSearchedCity(): String?
    suspend fun saveLastSearchedCity(cityName: String)
}