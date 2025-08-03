package com.example.mini_weather_app.domain

import com.example.mini_weather_app.domain.model.NetworkResult
import com.example.mini_weather_app.domain.model.WeatherInfo
import com.example.mini_weather_app.domain.repository.WeatherRepository
import com.example.mini_weather_app.domain.usecase.GetWeatherUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetWeatherUseCaseTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var getWeatherUseCase: GetWeatherUseCase

    @Before
    fun setup() {
        weatherRepository = mockk()
        getWeatherUseCase = GetWeatherUseCase(weatherRepository)
    }

    @Test
    fun `when city name is blank, returns Error result`() = runTest {
        // Given
        val blankCityName = "   "

        // When
        val result = getWeatherUseCase(blankCityName).first()

        // Then
        assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
        val errorResult = result as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo("City name cannot be empty")
    }

    @Test
    fun `when city name is empty, returns Error result`() = runTest {
        // Given
        val emptyCityName = ""

        // When
        val result = getWeatherUseCase(emptyCityName).first()

        // Then
        assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
        val errorResult = result as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo("City name cannot be empty")
    }

    @Test
    fun `when city name is valid, calls repository and returns result`() = runTest {
        // Given
        val cityName = "London"
        val weatherInfo = WeatherInfo(
            cityName = "London, United Kingdom",
            temperature = 20,
            description = "Partly cloudy",
            condition = "Partly cloudy",
            iconCode = "//cdn.weatherapi.com/weather/64x64/day/116.png"
        )

        coEvery { weatherRepository.getWeatherByCity(cityName) } returns flowOf(
            NetworkResult.Success(weatherInfo)
        )

        // When
        val result = getWeatherUseCase(cityName).first()

        // Then
        assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
        val successResult = result as NetworkResult.Success
        assertThat(successResult.data).isEqualTo(weatherInfo)
    }
}