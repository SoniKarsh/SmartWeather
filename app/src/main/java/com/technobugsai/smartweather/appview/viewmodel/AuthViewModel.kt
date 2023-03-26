package com.technobugsai.smartweather.appview.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.technobugsai.smartweather.utils.Resource
import com.technobugsai.smartweather.utils.validation.ValidationResult
import com.technobugsai.smartweather.utils.validation.ValidationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val application: Application): ViewModel() {

    var emailError = MutableStateFlow("")
    var pwdError = MutableStateFlow("")

    fun signInUser(email: String, pwd: String) {
        viewModelScope.launch {
            if (!isValidationSuccessful(email, pwd)) {}
        }
//        viewModelScope.launch {
//            signInUseCase(email, password).onEach { event ->
//                when (event) {
//                    is Resource.Loading -> showProgressBar(true)
//                    is Resource.Success -> {
//                        synchronizeNotesUseCase()
//                        showProgressBar(false)
//                        navigateToNoteListScreen()
//                    }
//                    is Resource.Error -> {
//                        event.error?.let { errorMessage ->
//                            showSnackbar(UiText.DynamicString(errorMessage))
//                        } ?: showSnackbar(UiText.StringResource(R.string.unexpected_error))
//                        showProgressBar(false)
//                    }
//                }
//            }.launchIn(this)
//        }
    }

    private fun signUp(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())

//        try {
//            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
//            userSessionStorage.saveUserSessionId(result.user?.uid ?: "")
//            emit(Resource.Success(result))
//        } catch (e: FirebaseAuthUserCollisionException) {
//            emit(Resource.Error(error = e.message))
//        } catch (e: FirebaseAuthWeakPasswordException) {
//            emit(Resource.Error(error = e.message))
//        } catch (e: FirebaseAuthInvalidCredentialsException) {
//            emit(Resource.Error(error = e.message))
//        } catch (e: Exception) {
//            emit(Resource.Error(error = e.message))
//        }
    }

    fun signIn(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())

//        try {
//            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
//            userSessionStorage.saveUserSessionId(result.user?.uid ?: "")
//            emit(Resource.Success(result))
//        } catch (e: Exception) {
//            emit(Resource.Error(error = e.message))
//        }
    }

    fun isValidationSuccessful(email: String, pwd: String): Boolean {
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