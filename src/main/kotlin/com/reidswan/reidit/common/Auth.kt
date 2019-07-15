package com.reidswan.reidit.common

import io.ktor.auth.Principal

data class LoginRequest(val loginIdentifier: String, val password: String)
data class LoginResult(val publicAccountResult: PublicAccountResult, val jwt: String)
data class UserPrincipal(val username: String, val accountId: Int): Principal