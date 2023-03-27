package com.technobugsai.smartweather.model.weather

data class ResCurrentWeatherModel (
    val city_name: String,
    val clouds: Clouds,
    val dt: Int,
    val dt_iso: String,
    val lat: Double,
    val lon: Double,
    val main: Main,
    val rain: Rain,
    val timezone: Int,
    val weather: List<Weather>,
    val wind: Wind
) {
    data class Weather(
        val description: String,
        val icon: String,
        val id: Int,
        val main: String
    )
    data class Wind(
        val deg: Int,
        val speed: Double
    )
    data class Rain(
        val `1h`: Double,
        val `3h`: Double
    )
    data class Main(
        val feels_like: Double,
        val humidity: Int,
        val pressure: Int,
        val temp: Double,
        val temp_max: Double,
        val temp_min: Double
    )
    data class Clouds(
        val all: Int
    )
}