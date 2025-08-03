package com.example.mini_weather_app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mini_weather_app.data.remote.WeatherApi
import com.example.mini_weather_app.domain.model.NetworkResult
import com.example.mini_weather_app.domain.model.WeatherInfo
import com.example.mini_weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val context: Context
) : WeatherRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weather_prefs")
    private val LAST_CITY_KEY = stringPreferencesKey("last_searched_city")

    override suspend fun getWeatherByCity(cityName: String): Flow<NetworkResult<WeatherInfo>> = flow {
        try {
            emit(NetworkResult.Loading())

            val response = weatherApi.getCurrentWeather(cityName = cityName.trim())

            if (response.isSuccessful) {
                response.body()?.let { weatherResponse ->
                    val weatherInfo = WeatherInfo(
                        cityName = "${weatherResponse.location.name}, ${weatherResponse.location.country}",
                        temperature = weatherResponse.current.tempC.toInt(),
                        description = weatherResponse.current.condition.text,
                        condition = weatherResponse.current.condition.text,
                        iconCode = weatherResponse.current.condition.icon
                    )

                    saveLastSearchedCity(cityName)

                    emit(NetworkResult.Success(weatherInfo))
                } ?: emit(NetworkResult.Error("No data received"))
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "City not found. Please check the city name."
                    401 -> "API key is invalid"
                    403 -> "API key exceeded quota or is disabled"
                    else -> "Network error: ${response.message()}"
                }
                emit(NetworkResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Unable to resolve host") == true ->
                    "No internet connection. Please check your network."
                e.message?.contains("timeout") == true ->
                    "Request timeout. Please try again."
                else -> "Something went wrong: ${e.message}"
            }
            emit(NetworkResult.Error(errorMessage))
        }
    }

    override suspend fun getLastSearchedCity(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_CITY_KEY]
        }.first()
    }

    override suspend fun saveLastSearchedCity(cityName: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_CITY_KEY] = cityName
        }
    }
}