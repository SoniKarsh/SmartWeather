package com.technobugsai.smartweather.db

import kotlinx.coroutines.flow.Flow

interface IDataStore {
    suspend fun putString(key: String, value: String)
    fun getString(key: String): Flow<String?>
    suspend fun clear()
}