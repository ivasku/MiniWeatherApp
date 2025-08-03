package com.example.mini_weather_app.domain.usecase

import com.example.mini_weather_app.domain.model.NetworkResult
import com.example.mini_weather_app.domain.model.WeatherInfo
import com.example.mini_weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String): Flow<NetworkResult<WeatherInfo>> {
        return if (cityName.isBlank()) {
            kotlinx.coroutines.flow.flow {
                emit(NetworkResult.Error("City name cannot be empty"))
            }
        } else {
            repository.getWeatherByCity(cityName)
        }
    }
}