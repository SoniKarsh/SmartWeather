package com.technobugsai.smartweather.modules.datastore

import com.technobugsai.smartweather.db.IDataStore
import com.technobugsai.smartweather.db.SharedPreferenceDataStore
import org.koin.dsl.module

val datastoreModule = module {
    single { SharedPreferenceDataStore(get()) }
}