package com.example.mini_weather_app.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("location")
    val location: Location,
    @SerializedName("current")
    val current: Current
)

data class Location(
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String
)

data class Current(
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("condition")
    val condition: Condition,
    @SerializedName("humidity")
    val humidity: Int
)

data class Condition(
    @SerializedName("text")
    val text: String,
    @SerializedName("icon")
    val icon: String
)