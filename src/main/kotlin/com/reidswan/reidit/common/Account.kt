package com.reidswan.reidit.common

import com.expedia.graphql.annotations.GraphQLName

data class AccountRequest (
    val username: String,
    val emailAddress: String?,
    val password: String
)

data class AccountResult (
    val accountId: Int,
    val username: String,
    val emailAddress: String?,
    val verified: Boolean,
    val passwordHash: String,
    val passwordSalt: String) {
    fun toPublic(): PublicAccountResult = PublicAccountResult(this.accountId, this.username, this.emailAddress, this.verified)
}

@GraphQLName("Account")
data class PublicAccountResult (
    val accountId: Int,
    val username: String,
    val emailAddress: String?,
    val verified: Boolean
)