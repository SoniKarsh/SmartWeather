package com.technobugsai.smartweather

import android.app.Application
import com.technobugsai.smartweather.modules.activity.activityModule
import com.technobugsai.smartweather.modules.network.apiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WeatherApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(
                apiModule,
                activityModule
            )
        }

    }

}