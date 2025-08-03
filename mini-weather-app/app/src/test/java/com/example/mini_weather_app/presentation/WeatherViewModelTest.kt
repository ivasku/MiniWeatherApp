package com.example.mini_weather_app.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mini_weather_app.domain.model.NetworkResult
import com.example.mini_weather_app.domain.model.WeatherInfo
import com.example.mini_weather_app.domain.usecase.GetLastSearchedCityUseCase
import com.example.mini_weather_app.domain.usecase.GetWeatherUseCase
import com.example.mini_weather_app.presentation.weather.WeatherViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var getLastSearchedCityUseCase: GetLastSearchedCityUseCase
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getWeatherUseCase = mockk()
        getLastSearchedCityUseCase = mockk()

        coEvery { getLastSearchedCityUseCase() } returns null

        viewModel = WeatherViewModel(getWeatherUseCase, getLastSearchedCityUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when updateCityInput is called, cityInput state is updated`() {
        // Given
        val cityName = "London"

        // When
        viewModel.updateCityInput(cityName)

        // Then
        assertThat(viewModel.cityInput.value).isEqualTo(cityName)
    }

    @Test
    fun `when searchWeather is called with empty city, error message is set`() {
        // Given
        viewModel.updateCityInput("")

        // When
        viewModel.searchWeather()

        // Then
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Please enter a city name")
    }

    @Test
    fun `when searchWeather is successful, weatherInfo is updated`() = runTest {
        // Given
        val cityName = "London"
        val weatherInfo = WeatherInfo(
            cityName = "London, United Kingdom",
            temperature = 20,
            description = "Partly cloudy",
            condition = "Partly cloudy",
            iconCode = "//cdn.weatherapi.com/weather/64x64/day/116.png"
        )

        viewModel.updateCityInput(cityName)
        coEvery { getWeatherUseCase(cityName) } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Success(weatherInfo)
        )

        // When
        viewModel.searchWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.weatherInfo).isEqualTo(weatherInfo)
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    @Test
    fun `when searchWeather fails, error message is set`() = runTest {
        // Given
        val cityName = "InvalidCity"
        val errorMessage = "City not found"

        viewModel.updateCityInput(cityName)
        coEvery { getWeatherUseCase(cityName) } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Error(errorMessage)
        )

        // When
        viewModel.searchWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.weatherInfo).isNull()
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `when clearError is called, error message is cleared`() {
        // Given - Set an error state first
        viewModel.updateCityInput("")
        viewModel.searchWeather() // This will set an error

        // When
        viewModel.clearError()

        // Then
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    @Test
    fun `when loading last searched city, cityInput is updated`() = runTest {
        // Given
        val lastCity = "Paris"
        coEvery { getLastSearchedCityUseCase() } returns lastCity

        val weatherInfo = WeatherInfo(
            cityName = "Paris, France",
            temperature = 15,
            description = "Clear",
            condition = "Clear",
            iconCode = "//cdn.weatherapi.com/weather/64x64/day/113.png"
        )

        coEvery { getWeatherUseCase(lastCity) } returns flowOf(
            NetworkResult.Success(weatherInfo)
        )

        // When
        val newViewModel = WeatherViewModel(getWeatherUseCase, getLastSearchedCityUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(newViewModel.cityInput.value).isEqualTo(lastCity)
    }
}