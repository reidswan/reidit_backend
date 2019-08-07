package com.reidswan.reidit.controllers

import com.expedia.graphql.annotations.GraphQLIgnore
import com.reidswan.reidit.common.AccountResult
import com.reidswan.reidit.common.HttpException
import com.reidswan.reidit.common.LoginResult
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.data.queries.AccountsQueries
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLIntegrityConstraintViolationException

val logger: Logger = LoggerFactory.getLogger(Application::class.java)

sealed class ValidationError {
    class Username(val err: UsernameValidationError): ValidationError() {
        override fun toString(): String {
            return err.toString()
        }
    }
    class Email(val err: EmailValidationError): ValidationError() {
        override fun toString(): String {
            return err.toString()
        }
    }
    class Password(val err: PasswordValidationError): ValidationError() {
        override fun toString(): String {
            return err.toString()
        }
    }
}

enum class UsernameValidationError {
    NotAvailable,
    BelowMinLength,
    AboveMaxLength,
    InvalidCharacters;

    override fun toString(): String {
        return when (this) {
            NotAvailable -> "This username is already in use"
            InvalidCharacters -> "The username can only contain alphanumeric characters and the symbols '_-'"
            AboveMaxLength -> "The username must contain at most $USERNAME_LENGTH_MAX characters"
            BelowMinLength -> "The username must contain at least $USERNAME_LENGTH_MIN characters"
        }
    }
}

enum class EmailValidationError {
    AlreadyInUse,
    InvalidFormat;

    override fun toString(): String {
        return when (this) {
            AlreadyInUse -> "This email address is already in use"
            InvalidFormat -> "The email address provided is invalid"
        }
    }
}

enum class PasswordValidationError {
    BelowMinLength,
    InsufficientComplexity,
    InvalidCharacters;

    override fun toString(): String {
        return when (this) {
            BelowMinLength -> "Password must be at least $MIN_PASSWORD_LENGTH characters"
            InsufficientComplexity -> "Password must contain at least 1 uppercase, 1 lowercase and 1 digit character"
            InvalidCharacters -> "Password can only contain alphanumeric characters and the symbols _-!@#\$%^&*+=/*-~|?"
        }
    }
}

const val USERNAME_LENGTH_MIN = 4
const val USERNAME_LENGTH_MAX = 50
const val USERNAME_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0987654321_-"
/*https://emailregex.com*/
const val EMAIL_REGEX = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*" +
        "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
        "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
        "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|" +
        "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
const val MIN_PASSWORD_LENGTH = 8
const val PASSWORD_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0987654321_-!@#$%^&*+=/*-~|?"
const val DIGITS = "0987654321"

class AccountsController(private val querySource: AccountsQueries) {

    fun emailExists(emailAddress: String): Boolean {
        return querySource.getAccountByEmail(emailAddress) != null
    }

    fun usernameExists(username: String): Boolean {
        return querySource.getAccountByUsername(username) != null
    }

    fun getAccountByUsername(username: String): AccountResult? {
        return querySource.getAccountByUsername(username)
    }

    fun getAccountByEmail(emailAddress: String): AccountResult? {
        return querySource.getAccountByEmail(emailAddress)
    }

    fun createAccount(username: String, emailAddress: String?, password: String): AccountResult? {
        val validationErrors = (
                validate(usernameValidators, username).map{ ValidationError.Username(it) }
              + validate(passwordValidators, password).map{ ValidationError.Password(it) }
              + if (emailAddress != null) validate(emailValidators, emailAddress).map{ ValidationError.Email(it) }
                else listOf()
        )
        if (validationErrors.isNotEmpty()) {
            throw HttpException(
                validationErrors.joinToString(separator=", ", transform = {it.toString()}),
                HttpStatusCode.BadRequest)
        }

        val salt = AuthController.generateSalt()
        val hash = AuthController.hashPassword(password, salt)
        val result = try {
            querySource.createAccount(username, emailAddress, hash, salt)
        } catch (e: ExposedSQLException) {
            when (e.cause) {
                is SQLIntegrityConstraintViolationException -> {
                    throw HttpException("Username and email address must be unique", HttpStatusCode.Conflict)
                }
                else -> throw e
            }
        }
        // TODO account creation email
        return result
    }

    fun login(loginIdentifier: String, password: String): LoginResult? {
        val account = if (EMAIL_REGEX.toRegex(RegexOption.IGNORE_CASE).matches(loginIdentifier)) {
            getAccountByEmail(loginIdentifier)
        } else {
            getAccountByUsername(loginIdentifier)
        } ?: return null

        val providedPasswordHash = AuthController.hashPassword(password, account.passwordSalt)
        if (providedPasswordHash != account.passwordHash) return null

        return LoginResult(account.toPublic(), AuthController.generateJWT(account))
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

    private fun<T, U: Any> validate(validators: List<(T) -> U?>, toValidate: T): List<U> = validators.mapNotNull { it(toValidate) }

}