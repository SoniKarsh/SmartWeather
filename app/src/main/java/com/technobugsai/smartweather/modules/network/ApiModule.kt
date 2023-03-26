package com.technobugsai.smartweather.modules.network

import com.fitpeo.task.api.ApiConstants
import com.technobugsai.smartweather.api.interfaces.FitPeoPhotosApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {
    single {
        provideOkHttpClient()
    }
    single {
        providePhotosApi(get())
    }
    single {
        provideRetrofit(get())
    }
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(ApiConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient()
        .newBuilder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
}

fun providePhotosApi(retrofit: Retrofit): FitPeoPhotosApi = retrofit.create(FitPeoPhotosApi::class.java)