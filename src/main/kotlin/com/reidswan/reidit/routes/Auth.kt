package com.reidswan.reidit.routes

import com.reidswan.reidit.common.UserPrincipal
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.controllers.AuthController
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.http.HttpStatusCode
import com.reidswan.reidit.data.queries.AccountsQueries
import com.reidswan.reidit.controllers.AccountsController
import com.reidswan.reidit.common.*

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

    val accountsController = AccountsController(AccountsQueries(Configuration.dependencies.database))
    routing {
         post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val response = accountsController.login(loginRequest.loginIdentifier, loginRequest.password) ?:
                throw HttpException("Failed to login with the provided details", HttpStatusCode.Unauthorized)
            call.respond(HttpStatusCode.OK, response)
        }
    }
}