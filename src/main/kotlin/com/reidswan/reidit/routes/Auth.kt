package com.reidswan.reidit.routes

import com.reidswan.reidit.common.UserPrincipal
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.controllers.AuthController
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt

@Suppress("unused")
fun Application.auth() {
    install(Authentication) {
        jwt {
            realm = Configuration.config.jwt.realm
            verifier(AuthController.jwtDecoder)
            validate {credential ->
                UserPrincipal(credential.payload.getClaim("username").asString(),
                              credential.payload.getClaim("accountId").asInt())
            }
        }
    }
}