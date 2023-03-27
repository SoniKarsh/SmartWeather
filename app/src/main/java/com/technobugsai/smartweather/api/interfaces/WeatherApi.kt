package com.technobugsai.smartweather.api.interfaces

import com.technobugsai.smartweather.api.ApiConstants
import com.technobugsai.smartweather.model.weather.ResCurrentWeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET(ApiConstants.FORECAST)
    suspend fun getCurrentWeather(@Query("id") id: String, @Query("appid") appid: String
                                  , @Query("units") units: String = "metric"): ResCurrentWeatherModel

}