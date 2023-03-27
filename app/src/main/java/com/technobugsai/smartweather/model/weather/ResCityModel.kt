package com.technobugsai.smartweather.model.weather

data class ResCityModel(
    val coord: Coord,
    val country: String,
    val id: Int,
    val name: String,
    val state: String
) {
    data class Coord(
        val lat: Double,
        val lon: Double
    )
}