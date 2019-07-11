package com.reidswan.reidit.controllers

import com.reidswan.reidit.common.Either.*
import com.reidswan.reidit.common.Either
import com.reidswan.reidit.data.queries.AccountsQueries
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.data.queries.AccountResult

sealed class ValidationError {
    class Username(val err: UsernameValidationError): ValidationError()
    class Email(val err: EmailValidationError): ValidationError()
    class Password(val err: PasswordValidationError): ValidationError()
}

enum class UsernameValidationError {
    NotAvailable,
    BelowMinLength,
    AboveMaxLength,
    InvalidCharacters
}

enum class EmailValidationError {
    AlreadyInUse,
    InvalidFormat
}

enum class PasswordValidationError {
    BelowMinLength,
    InsufficientComplexity,
    InvalidCharacters
}

const val USERNAME_LENGTH_MIN = 4
const val USERNAME_LENGTH_MAX = 50
const val USERNAME_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstivwxyz0987654321_-"
/*https://emailregex.com*/
const val EMAIL_REGEX = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*" +
        "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
        "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
        "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|" +
        "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
const val MIN_PASSWORD_LENGTH = 8
const val PASSWORD_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstivwxyz0987654321_-!@#$%^&*+=/*-~|?"
const val DIGITS = "0987654321"

object AccountsController {

    fun emailExists(emailAddress: String): Boolean {
        return AccountsQueries.getAccountByEmail(Configuration.dependencies.database, emailAddress) == null
    }


    fun usernameExists(username: String): Boolean {
        return AccountsQueries.getAccountByUsername(Configuration.dependencies.database, username) == null
    }

    fun getAccountByUsername(username: String): AccountResult? {
        return AccountsQueries.getAccountByUsername(Configuration.dependencies.database, username)
    }

    fun getAccountByEmail(emailAddress: String): AccountResult? {
        return AccountsQueries.getAccountByEmail(Configuration.dependencies.database, emailAddress)
    }

    fun createAccount(username: String, emailAddress: String?, password: String) {

    }

    private val usernameValidators = listOf(
        { username: String ->
            when {
                (username.length < USERNAME_LENGTH_MIN) -> UsernameValidationError.BelowMinLength
                (username.length > USERNAME_LENGTH_MAX) -> UsernameValidationError.AboveMaxLength
                else -> null
            }
        },
        { username: String ->
            if (!username.all { USERNAME_ALPHABET.contains(it)}) UsernameValidationError.InvalidCharacters else null
        },
        { username: String ->
            if (usernameExists(username)) UsernameValidationError.NotAvailable else null
        }
    )

    private val emailValidators = listOf(
        { emailAddress: String -> /* https://emailregex.com */
            if (!EMAIL_REGEX.toRegex(RegexOption.IGNORE_CASE).matches(emailAddress)) EmailValidationError.InvalidFormat
            else null
        },
        {emailAddress: String ->
            if (emailExists(emailAddress)) EmailValidationError.AlreadyInUse else null
        }
    )

    private val passwordValidators = listOf(
        { password: String ->
            if (password.length < MIN_PASSWORD_LENGTH) PasswordValidationError.BelowMinLength else null
        },
        { password: String ->
            if (!password.all { PASSWORD_ALPHABET.contains(it)}) PasswordValidationError.InvalidCharacters else null
        },
        { password: String ->
            if (password.toUpperCase() == password
                || password.toLowerCase() == password
                || !password.any{ DIGITS.contains(it) }) PasswordValidationError.InsufficientComplexity
            else null
        }
    )

    private fun<T, U> validate(validators: List<(T) -> U?>, toValidate: T): List<U> = validators.mapNotNull { it(toValidate) }
}