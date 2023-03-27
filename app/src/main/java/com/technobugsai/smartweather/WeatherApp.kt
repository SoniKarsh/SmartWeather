package com.technobugsai.smartweather

import android.app.Application
import com.google.firebase.FirebaseApp
import com.technobugsai.smartweather.modules.activity.activityModule
import com.technobugsai.smartweather.modules.auth.firebaseAuthModule
import com.technobugsai.smartweather.modules.datastore.datastoreModule
import com.technobugsai.smartweather.modules.network.apiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WeatherApp: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(
                apiModule,
                activityModule,
                datastoreModule,
                firebaseAuthModule
            )
        }

    }

}