package com.technobugsai.smartweather.utils.validation

import com.technobugsai.smartweather.R
import java.util.regex.Pattern

object ValidationUtils {

    const val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+"
    const val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{4,}\$"

    fun isEmailValid(email: String): ValidationResult {
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorId = R.string.error_empty_email
            )
        } else if (matcher.matches().not()) {
            return ValidationResult(
                successful = false,
                errorId = R.string.error_not_valid_email
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    fun isPasswordValid(password: String): ValidationResult {
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)
        if (password.isBlank()) {
            return ValidationResult(
                successful = false,
                errorId = R.string.error_empty_password
            )
        } else if (matcher.matches().not()) {
            return ValidationResult(
                successful = false,
                errorId = R.string.error_not_valid_password
            )
        }
        return ValidationResult(
            successful = true
        )
    }

}