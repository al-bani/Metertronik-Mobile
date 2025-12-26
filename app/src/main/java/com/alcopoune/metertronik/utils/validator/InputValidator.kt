package com.alcopoune.metertronik.utils.validator

import java.util.regex.Pattern

class InputValidator {
    companion object {
        private val EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        )
        
        /**
         * Validates email format
         * @param email The email string to validate
         * @return true if email format is valid, false otherwise
         */
        fun isValidEmail(email: String): Boolean {
            if (email.isBlank()) return false
            return EMAIL_PATTERN.matcher(email).matches()
        }
        
        /**
         * Validates password requirements:
         * - Minimum 8 characters
         * - Contains at least one number
         * @param password The password string to validate
         * @return true if password meets requirements, false otherwise
         */
        fun isValidPassword(password: String): Boolean {
            if (password.length < 8) return false
            return password.any { it.isDigit() }
        }
        
        /**
         * Gets error message for email validation
         */
        fun getEmailErrorMessage(email: String): String? {
            return if (email.isNotBlank() && !isValidEmail(email)) {
                "Please enter a valid email address"
            } else null
        }
        
        /**
         * Gets error message for password validation
         */
        fun getPasswordErrorMessage(password: String): String? {
            if (password.isBlank()) return null
            if (password.length < 8) {
                return "Password must be at least 8 characters"
            }
            if (!password.any { it.isDigit() }) {
                return "Password must contain at least one number"
            }
            return null
        }
    }
}