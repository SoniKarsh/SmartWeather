package com.technobugsai.smartweather.utils.validation

data class ValidationResult(
    val successful: Boolean,
    var errorMessage: String? = null,
    var errorId: Int? = null
)