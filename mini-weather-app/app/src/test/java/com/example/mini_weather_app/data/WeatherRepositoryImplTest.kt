package com.example.mini_weather_app.data

import android.content.Context
import com.example.mini_weather_app.data.remote.WeatherApi
import com.example.mini_weather_app.data.repository.WeatherRepositoryImpl
import com.example.mini_weather_app.domain.model.NetworkResult
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherRepositoryImplTest {

    private lateinit var weatherApi: WeatherApi
    private lateinit var context: Context
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        weatherApi = mockk()
        context = mockk(relaxed = true)

        repository = WeatherRepositoryImpl(weatherApi, context)
    }

    @Test
    fun `when getWeatherByCity returns 400, returns Error result with appropriate message`() = runTest {
        // Given
        val cityName = "InvalidCity"
        coEvery { weatherApi.getCurrentWeather(cityName = cityName) } returns Response.error(
            400,
            "Bad Request".toResponseBody()
        )

        // When
        val result = repository.getWeatherByCity(cityName).toList()

        // Then
        assertThat(result).hasSize(2) // Loading + Error
        assertThat(result[0]).isInstanceOf(NetworkResult.Loading::class.java)
        assertThat(result[1]).isInstanceOf(NetworkResult.Error::class.java)

        val errorResult = result[1] as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo("City not found. Please check the city name.")
    }

    @Test
    fun `when getWeatherByCity returns 401, returns Error result for invalid API key`() = runTest {
        // Given
        val cityName = "London"
        coEvery { weatherApi.getCurrentWeather(cityName = cityName) } returns Response.error(
            401,
            "Unauthorized".toResponseBody()
        )

        // When
        val result = repository.getWeatherByCity(cityName).toList()

        // Then
        assertThat(result).hasSize(2) // Loading + Error
        assertThat(result[1]).isInstanceOf(NetworkResult.Error::class.java)

        val errorResult = result[1] as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo("API key is invalid")
    }

    @Test
    fun `when getWeatherByCity returns 403, returns Error result for quota exceeded`() = runTest {
        // Given
        val cityName = "London"
        coEvery { weatherApi.getCurrentWeather(cityName = cityName) } returns Response.error(
            403,
            "Forbidden".toResponseBody()
        )

        // When
        val result = repository.getWeatherByCity(cityName).toList()

        // Then
        assertThat(result).hasSize(2) // Loading + Error
        assertThat(result[1]).isInstanceOf(NetworkResult.Error::class.java)

        val errorResult = result[1] as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo("API key exceeded quota or is disabled")
    }

    @Test
    fun `when getWeatherByCity throws exception, returns Error result`() = runTest {
        // Given
        val cityName = "London"
        coEvery { weatherApi.getCurrentWeather(cityName = cityName) } throws Exception("Network error")

        // When
        val result = repository.getWeatherByCity(cityName).toList()

        // Then
        assertThat(result).hasSize(2) // Loading + Error
        assertThat(result[1]).isInstanceOf(NetworkResult.Error::class.java)

        val errorResult = result[1] as NetworkResult.Error
        assertThat(errorResult.message).contains("Something went wrong")
    }

    @Test
    fun `when getWeatherByCity throws timeout exception, returns appropriate error`() = runTest {
        // Given
        val cityName = "London"
        coEvery { weatherApi.getCurrentWeather(cityName = cityName) } throws Exception("timeout")

        // When
        val result = repository.getWeatherByCity(cityName).toList()

        // Then
        assertThat(result).hasSize(2) // Loading + Error
        assertThat(result[1]).isInstanceOf(NetworkResult.Error::class.java)

        val errorResult = result[1] as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo("Request timeout. Please try again.")
    }

    @Test
    fun `when getWeatherByCity throws network exception, returns appropriate error`() = runTest {
        // Given
        val cityName = "London"
        coEvery { weatherApi.getCurrentWeather(cityName = cityName) } throws Exception("Unable to resolve host")

        // When
        val result = repository.getWeatherByCity(cityName).toList()

        // Then
        assertThat(result).hasSize(2) // Loading + Error
        assertThat(result[1]).isInstanceOf(NetworkResult.Error::class.java)

        val errorResult = result[1] as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo("No internet connection. Please check your network.")
    }

    @Test
    fun `when API returns empty body, returns Error result`() = runTest {
        // Given
        val cityName = "London"
        coEvery { weatherApi.getCurrentWeather(cityName = cityName) } returns Response.success(null)

        // When
        val result = repository.getWeatherByCity(cityName).toList()

        // Then
        assertThat(result).hasSize(2) // Loading + Error
        assertThat(result[1]).isInstanceOf(NetworkResult.Error::class.java)

        val errorResult = result[1] as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo("No data received")
    }
}
