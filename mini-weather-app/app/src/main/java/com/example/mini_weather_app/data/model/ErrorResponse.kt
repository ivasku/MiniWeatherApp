package com.example.mini_weather_app.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error")
    val error: ErrorDetail
)

data class ErrorDetail(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)