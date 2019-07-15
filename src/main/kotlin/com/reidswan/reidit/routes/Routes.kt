package com.reidswan.reidit.routes

import com.reidswan.reidit.common.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.routing
import io.ktor.routing.get
import com.reidswan.reidit.controllers.CommunitiesController
import com.reidswan.reidit.common.Either.*
import com.reidswan.reidit.controllers.AccountsController
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post

@Suppress("unused")
fun Application.routes() {
    routing {
        get("/communities") {
            val pageParamResults = getValidPageParams(call.request.queryParameters)
            when (pageParamResults) {
                is Right -> call.respond(HttpStatusCode.BadRequest, pageParamResults.right)
                is Left -> {
                    call.respond(CommunitiesController.getCommunities(pageParamResults.left))
                }
            }
        }

        get("/account/{username}") {
            val username = call.parameters["username"] ?:
                throw HttpException("No username specified", HttpStatusCode.BadRequest)
            val account = AccountsController.getAccountByUsername(username) ?:
                throw HttpException("No user found for username $username", HttpStatusCode.NotFound)
            call.respond(account.toPublic())
        }

        post("/account") {
            val accountRequest = call.receive<AccountRequest>()
            AccountsController.createAccount(accountRequest.username, accountRequest.emailAddress, accountRequest.password)
            call.respond(HttpStatusCode.OK, SuccessResponse("Successfully created account ${accountRequest.username}"))
        }

        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val response = AccountsController.login(loginRequest.loginIdentifier, loginRequest.password) ?:
                throw HttpException("Failed to login with the provided details", HttpStatusCode.Unauthorized)
            call.respond(HttpStatusCode.OK, response)
        }

        authenticate {
            // TODO: remove /test endpoint
            get("/test") {
                val user: UserPrincipal = call.authentication.principal<UserPrincipal>()!!
                call.respond(SuccessResponse("Congratulations on getting through security, ${user.username}!"))
            }
        }
    }
}