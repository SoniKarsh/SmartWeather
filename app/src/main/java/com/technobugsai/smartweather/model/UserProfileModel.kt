package com.technobugsai.smartweather.model

data class UserProfileModel(
    var id: String? = "",
    var userName: String? = "",
    var emailId: String? = "",
    var password: String? = "",
    var confirmPassword: String? = "",
    var shortBio: String? = "",
    var userProfile: String? = ""
)