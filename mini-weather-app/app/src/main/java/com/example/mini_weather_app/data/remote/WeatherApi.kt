package com.example.mini_weather_app.data.remote

import com.example.mini_weather_app.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String = API_KEY,
        @Query("q") cityName: String,
        @Query("aqi") aqi: String = "no"
    ): Response<WeatherResponse>

    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        // Get your free API key from: https://www.weatherapi.com/
        private const val API_KEY = "your_weatherapi_key_here"
    }
}