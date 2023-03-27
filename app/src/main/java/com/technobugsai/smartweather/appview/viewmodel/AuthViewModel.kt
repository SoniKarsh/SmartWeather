package com.technobugsai.smartweather.appview.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.technobugsai.smartweather.db.DbConstants
import com.technobugsai.smartweather.db.SharedPreferenceDataStore
import com.technobugsai.smartweather.model.UserProfileModel
import com.technobugsai.smartweather.utils.Resource
import com.technobugsai.smartweather.utils.validation.ValidationResult
import com.technobugsai.smartweather.utils.validation.ValidationUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.technobugsai.smartweather.R
import com.technobugsai.smartweather.utils.AppUtils
import com.technobugsai.smartweather.utils.AppUtils.toJson
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthViewModel(private val application: Application,
                    private val firebaseAuth: FirebaseAuth,
                    private val dataStore: SharedPreferenceDataStore
): ViewModel() {
    var emailError = MutableSharedFlow<String>(replay = 1)
    var pwdError = MutableSharedFlow<String>(replay = 1)
    var conPwdError = MutableSharedFlow<String>(replay = 1)
    var uNameError = MutableSharedFlow<String>(replay = 1)
    var aBioError = MutableSharedFlow<String>(replay = 1)
    var progressBar = MutableSharedFlow<Boolean>(replay = 1)
    var snackBar = MutableSharedFlow<String>(replay = 1)
    var logInSuccess = MutableSharedFlow<Boolean>(replay = 1)

    private var firebaseStorage: FirebaseStorage? = null
    private var firebaseFireStore: FirebaseFirestore? = null
    private var storageRef: StorageReference? = null
    var toAuth = true
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        firebaseStorage = Firebase.storage
        storageRef = firebaseStorage?.reference
        firebaseFireStore = Firebase.firestore
        checkIfLoggedIn()
    }

    suspend fun clearDb() {
        dataStore.clear()
    }

    private fun checkIfLoggedIn() {
        _isLoading.tryEmit(true)
        viewModelScope.launch {
            dataStore.getString(DbConstants.USER_ID).collectLatest {
                toAuth = it.isNullOrEmpty()
                _isLoading.emit(false)
            }
        }
    }

    fun signInUser(email: String, pwd: String) {
        if (!isValidationSuccessful(email, pwd)) return
        viewModelScope.launch {
            signIn(email, pwd).onEach { event ->
                when (event) {
                    is Resource.Loading -> progressBar.tryEmit(true)
                    is Resource.Success -> {
                        logInSuccess.tryEmit(true)
                        progressBar.tryEmit(false)
                    }
                    is Resource.Error -> {
                        event.error?.let { errorMessage ->
                            snackBar.tryEmit(errorMessage)
                        } ?: snackBar.tryEmit(application.getString(R.string.error_something_went_wrong))
                        progressBar.tryEmit(false)
                    }
                }
            }.launchIn(this)
        }
    }

    private suspend fun getUserProfileData(id: String) : Resource<UserProfileModel> = suspendCoroutine { continuation ->
        firebaseFireStore?.collection(DbConstants.DB_USER)
            ?.document(id)
            ?.get()
            ?.addOnSuccessListener {
                if (it != null && it.exists()) {
                    val userProfile = it.toObject(UserProfileModel::class.java)
                    viewModelScope.launch {
                        dataStore.putString(DbConstants.USER_PROFILE, userProfile.toJson())
                    }
                    continuation.resume(Resource.Success(data = userProfile))
                } else {
                    continuation.resume(Resource.Error(error = null))
                }
            }?.addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }

    fun signUpUser(userProfileModel: UserProfileModel) {
        if (!isValidationSignUpSuccessful(userProfileModel)) {
            return
        }
        viewModelScope.launch {
            signUp(userProfileModel).onEach { event ->
                when (event) {
                    is Resource.Error -> {
                        event.error?.let { errorMessage ->
                            snackBar.tryEmit(errorMessage)
                        } ?: snackBar.tryEmit(application.getString(R.string.error_something_went_wrong))
                        progressBar.tryEmit(false)
                    }
                    is Resource.Loading -> progressBar.tryEmit(true)
                    is Resource.Success -> {
                        logInSuccess.tryEmit(true)
                        progressBar.tryEmit(false)
                    }
                }
            }.launchIn(this)
        }
    }

    private suspend fun uploadProfile(metadata: StorageMetadata, storeReference: StorageReference?, profile: UserProfileModel): UploadTask? {
        val task = storeReference?.putBytes(AppUtils.getBytes(profile.userProfile ?: ""), metadata)
        task?.await()
        return task
    }

    private suspend fun uploadUserData(profile: UserProfileModel): Task<Void>? {
        val task = firebaseFireStore?.collection(DbConstants.DB_USER)
            ?.document(profile.id ?: "")
            ?.set(profile)
        task?.await()
        return task
    }

    private fun signUp(profile: UserProfileModel): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val result =
                firebaseAuth.createUserWithEmailAndPassword(profile.emailId ?: "", profile.password ?: "")
                    .await()
            val storeMetadata = StorageMetadata.Builder()
                .setContentType(DbConstants.IMG_CONTENT_TYPE)
                .build()
            val imageRef = storageRef
                ?.child(DbConstants.USER_IMAGE)
                ?.child(result.user?.uid ?: "")
                ?.child(DbConstants.DEFAULT_IMG_PATH)
            val uploadTask = uploadProfile(storeMetadata, imageRef, profile)
            if (uploadTask?.isSuccessful == true) {
                val downTask = imageRef?.downloadUrl
                downTask?.await()
                if (downTask?.isSuccessful == true) {
                    profile.userProfile = downTask.result.toString()
                    profile.id = result.user?.uid ?: ""
                    val userTask = uploadUserData(profile)
                    if (userTask?.isSuccessful == true) {
                        dataStore.putString(
                            DbConstants.USER_ID,
                            result.user?.uid ?: ""
                        )
                        dataStore.putString(DbConstants.USER_PROFILE, profile.toJson())
                        emit(Resource.Success(result))
                    } else {
                        userTask?.exception?.let {
                            emit(Resource.Error(error = it.message ?: it.localizedMessage ?: application.getString(R.string.error_something_went_wrong)))
                        }
                    }
                } else {
                    downTask?.exception?.let {
                        emit(Resource.Error(error = it.message ?: it.localizedMessage ?: application.getString(R.string.error_something_went_wrong)))
                    }
                }
            } else {
                uploadTask?.exception?.let {
                    emit(Resource.Error(error = it.message ?: it.localizedMessage ?: application.getString(R.string.error_something_went_wrong)))
                }
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            emit(Resource.Error(error = e.message))
        } catch (e: FirebaseAuthWeakPasswordException) {
            emit(Resource.Error(error = e.message))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error(error = e.message))
        } catch (e: Exception) {
            emit(Resource.Error(error = e.message))
        }
    }

    private fun signIn(email: String, password: String): Flow<Resource<UserProfileModel>> = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            dataStore.putString(DbConstants.USER_ID, result.user?.uid ?: "")
            emit(getUserProfileData(result.user?.uid ?: ""))
        } catch (e: Exception) {
            emit(Resource.Error(error = e.message))
        }
    }

    private fun isValidationSuccessful(email: String, pwd: String): Boolean {
        val emailResult = ValidationUtils.isEmailValid(email)
        if (!emailResult.successful) {
            emailError.tryEmit(returnString(emailResult))
            return false
        }
        emailError.tryEmit("")
        val passwordResult = ValidationUtils.isPasswordValid(pwd)
        if (!passwordResult.successful) {
            pwdError.tryEmit(returnString(passwordResult))
            return false
        }
        pwdError.tryEmit("")
        return true
    }

    private fun isValidationSignUpSuccessful(userProfileModel: UserProfileModel): Boolean {
        if (isValidationSuccessful(userProfileModel.emailId ?: "", userProfileModel.password ?: "").not()) return false
        val conPasswordResult = ValidationUtils.isConfirmPasswordValid(userProfileModel.password ?: "", userProfileModel.confirmPassword ?: "")
        if (!conPasswordResult.successful) {
            conPwdError.tryEmit(returnString(conPasswordResult))
            return false
        }
        conPwdError.tryEmit("")
        val uNameResult = ValidationUtils.isUserNameValid(userProfileModel.userName ?: "")
        if (!uNameResult.successful) {
            uNameError.tryEmit(returnString(uNameResult))
            return false
        }
        uNameError.tryEmit("")
        val bioResult = ValidationUtils.isBioValid(userProfileModel.shortBio ?: "")
        if (!bioResult.successful) {
            aBioError.tryEmit(returnString(bioResult))
            return false
        }
        aBioError.tryEmit("")
        if (userProfileModel.userProfile.isNullOrEmpty()) {
            snackBar.tryEmit(application.getString(R.string.error_not_valid_profile))
            return false
        }
        return true
    }

    private fun returnString(result: ValidationResult): String {
        result.run {
            if (errorMessage.isNullOrEmpty().not()) {
                return errorMessage!!
            } else if (errorId != null) {
                return application.applicationContext.getString(errorId!!)
            } else {
                return ""
            }
        }
    }

}