package com.technobugsai.smartweather.model.weather

data class ResCurrentWeatherModel (
    val city: City,
    val cnt: Int,
    val cod: String?,
    val list: List<WeatherItem>,
    val message: Int
) {
    data class City(
        val coord: Coord,
        val country: String?,
        val id: Int,
        val name: String?,
        val population: Int,
        val sunrise: Int,
        val sunset: Int,
        val timezone: Int
    ) {
        data class Coord(
            val lat: Double,
            val lon: Double
        )
    }
    data class WeatherItem(
        val city_name: String?,
        val clouds: Clouds,
        val dt: Int,
        val dt_iso: String?,
        val dt_txt: String?,
        val lat: Double,
        val lon: Double,
        val main: Main,
        val rain: Rain,
        val snow: Snow,
        val sys: Sys,
        val timezone: Int,
        val weather: List<Weather>,
        val wind: Wind
    ) {
        data class Snow(
            val `3h`: Double
        )
        data class Sys(
            val pod: String?
        )
        data class Weather(
            val description: String?,
            val icon: String?,
            val id: Int,
            val main: String?
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
            val temp_min: Double,
            val grnd_level: Int,
            val sea_level: Int,
            val temp_kf: Double,
        )
        data class Clouds(
            val all: Int
        )
    }
}