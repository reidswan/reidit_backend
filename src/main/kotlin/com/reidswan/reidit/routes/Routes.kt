package com.reidswan.reidit.routes

import com.reidswan.reidit.common.AccountRequest
import com.reidswan.reidit.common.Either.Left
import com.reidswan.reidit.common.Either.Right
import com.reidswan.reidit.common.HttpException
import com.reidswan.reidit.common.LoginRequest
import com.reidswan.reidit.common.SuccessResponse
import com.reidswan.reidit.common.UserPrincipal
import com.reidswan.reidit.controllers.AccountsController
import com.reidswan.reidit.controllers.CommunitiesController
import com.reidswan.reidit.data.queries.AccountsQueries
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

@Suppress("unused")
fun Application.routes() {
    val accountsController = AccountsController(AccountsQueries)
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
            val account = accountsController.getAccountByUsername(username) ?:
                throw HttpException("No user found for username $username", HttpStatusCode.NotFound)
            call.respond(account.toPublic())
        }

        post("/account") {
            val accountRequest = call.receive<AccountRequest>()
            accountsController.createAccount(accountRequest.username, accountRequest.emailAddress, accountRequest.password)
            call.respond(HttpStatusCode.OK, SuccessResponse("Successfully created account ${accountRequest.username}"))
        }

        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val response = accountsController.login(loginRequest.loginIdentifier, loginRequest.password) ?:
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