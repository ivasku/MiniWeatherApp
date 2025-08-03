package com.example.mini_weather_app.domain.usecase


import com.example.mini_weather_app.domain.repository.WeatherRepository
import javax.inject.Inject

class SaveLastSearchedCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String) {
        repository.saveLastSearchedCity(cityName)
    }
}