package com.example.mini_weather_app.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_weather_app.domain.model.NetworkResult
import com.example.mini_weather_app.domain.usecase.GetLastSearchedCityUseCase
import com.example.mini_weather_app.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getLastSearchedCityUseCase: GetLastSearchedCityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _cityInput = MutableStateFlow("")
    val cityInput: StateFlow<String> = _cityInput.asStateFlow()

    init {
        loadLastSearchedCity()
    }

    fun updateCityInput(city: String) {
        _cityInput.value = city
    }

    fun searchWeather() {
        val city = _cityInput.value.trim()
        if (city.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter a city name"
            )
            return
        }

        viewModelScope.launch {
            getWeatherUseCase(city).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherInfo = result.data,
                            errorMessage = null
                        )
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun loadLastSearchedCity() {
        viewModelScope.launch {
            getLastSearchedCityUseCase()?.let { lastCity ->
                _cityInput.value = lastCity

                searchWeather()
            }
        }
    }

    fun retryLastSearch() {
        searchWeather()
    }
}
