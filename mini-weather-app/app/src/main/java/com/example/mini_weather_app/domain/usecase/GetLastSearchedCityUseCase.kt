package com.example.mini_weather_app.domain.usecase

import com.example.mini_weather_app.domain.repository.WeatherRepository
import javax.inject.Inject

class GetLastSearchedCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(): String? {
        return repository.getLastSearchedCity()
    }
}