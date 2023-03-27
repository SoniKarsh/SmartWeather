package com.technobugsai.smartweather.appview.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.technobugsai.smartweather.db.DbConstants
import com.technobugsai.smartweather.db.SharedPreferenceDataStore
import com.technobugsai.smartweather.model.UserProfileModel
import com.technobugsai.smartweather.utils.AppUtils
import com.technobugsai.smartweather.utils.AppUtils.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val application: Application,
    private val firebaseAuth: FirebaseAuth,
    private val dataStore: SharedPreferenceDataStore
): ViewModel() {

    var userModel = MutableStateFlow<UserProfileModel?>(null)
    private var firebaseStorage: FirebaseStorage? = null
    private var firebaseFireStore: FirebaseFirestore? = null
    private var storageRef: StorageReference? = null

    init {
        firebaseStorage = Firebase.storage
        storageRef = firebaseStorage?.reference
        firebaseFireStore = Firebase.firestore
        getUserModel()
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