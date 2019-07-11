package com.reidswan.reidit.common

data class AccountRequest (
    val username: String,
    val emailAddress: String?,
    val password: String
)

data class AccountResult (
    val username: String,
    val emailAddress: String?,
    val verified: Boolean,
    val passwordHash: String,
    val passwordSalt: String) {
    fun toPublic(): PublicAccountResult = PublicAccountResult(this.username, this.emailAddress, this.verified)
}

data class PublicAccountResult (
    val username: String,
    val emailAddress: String?,
    val verified: Boolean
)