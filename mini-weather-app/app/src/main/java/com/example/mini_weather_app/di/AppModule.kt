package com.example.mini_weather_app.di

import android.content.Context
import com.example.mini_weather_app.data.remote.ApiClient
import com.example.mini_weather_app.data.remote.WeatherApi
import com.example.mini_weather_app.data.repository.WeatherRepositoryImpl
import com.example.mini_weather_app.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return ApiClient.getRetrofit().create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApi: WeatherApi,
        @ApplicationContext context: Context
    ): WeatherRepository {
        return WeatherRepositoryImpl(weatherApi, context)
    }
}