package com.technobugsai.smartweather.appview.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.technobugsai.smartweather.api.ApiConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.technobugsai.smartweather.R
import com.technobugsai.smartweather.api.interfaces.WeatherApi
import com.technobugsai.smartweather.db.DbConstants
import com.technobugsai.smartweather.db.SharedPreferenceDataStore
import com.technobugsai.smartweather.model.UserProfileModel
import com.technobugsai.smartweather.model.weather.ResCityModel
import com.technobugsai.smartweather.model.weather.ResCurrentWeatherModel
import com.technobugsai.smartweather.utils.AppUtils.toObject
import com.technobugsai.smartweather.utils.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val application: Application,
    private val firebaseAuth: FirebaseAuth,
    private val dataStore: SharedPreferenceDataStore,
    private val api: WeatherApi
): ViewModel() {

    var userModel = MutableStateFlow<UserProfileModel?>(null)
    private var firebaseStorage: FirebaseStorage? = null
    private var firebaseFireStore: FirebaseFirestore? = null
    private var storageRef: StorageReference? = null
    private var defaultCity = ResCityModel(
        id = 1279233,
        name = "Ahmedabad",
        state = "",
        coord = ResCityModel.Coord(
            23.033333, 72.616669
        ),
        country = "IN"
    )
    var selectedModel = MutableStateFlow<ResCityModel?>(defaultCity)
    var weatherModel = MutableStateFlow<ResCurrentWeatherModel?>(null)
    var progressBar = MutableStateFlow<Boolean>(false)
    var snackBar = MutableStateFlow<String>("")
    var weatherItem = MutableStateFlow<ResCurrentWeatherModel.WeatherItem?>(null)

    init {
        firebaseStorage = Firebase.storage
        storageRef = firebaseStorage?.reference
        firebaseFireStore = Firebase.firestore
        getUserModel()
    }

    fun emitWeatherItem(item: ResCurrentWeatherModel.WeatherItem) {
        weatherItem.tryEmit(item)
    }

    fun getWeatherData(city: ResCityModel){
        progressBar.tryEmit(true)
        viewModelScope.launch {
            try {
                val currentWeather = api.getCurrentWeather(city.id.toString(), ApiConstants.API_KEY)
                progressBar.tryEmit(false)
                weatherModel.tryEmit(currentWeather)
            } catch (e: Exception) {
                snackBar.tryEmit(e.message ?: e.localizedMessage ?: application.getString(R.string.error_something_went_wrong))
            }
        }
    }

    fun getStorageRef(id: String): StorageReference? {
        return storageRef
            ?.child(DbConstants.USER_IMAGE)
            ?.child(id)
            ?.child(DbConstants.DEFAULT_IMG_PATH)
    }

    private fun getUserModel(){
        viewModelScope.launch {
            dataStore.getString(DbConstants.USER_PROFILE).collectLatest {
                userModel.emit(it?.toObject<UserProfileModel>())
            }
        }
    }

}