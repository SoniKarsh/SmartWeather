package com.technobugsai.smartweather.utils

import android.util.Log
import com.technobugsai.smartweather.BuildConfig

object AppLogger {

    fun log(
        print: String? = "",
        throwable: Throwable? = Throwable(""),
        tag: String? = null
    ) {
        if (BuildConfig.DEBUG) {
            if (throwable?.message?.isNotEmpty() == true) {
                Log.e(tag ?: AppLogger::class.simpleName, print, throwable)
            } else {
                Log.e(tag ?: AppLogger::class.simpleName, "$print")
            }
        }
    }

}
