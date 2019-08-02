package com.reidswan.reidit.routes

import com.reidswan.reidit.common.*
import com.reidswan.reidit.common.Either.*
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.controllers.AccountsController
import com.reidswan.reidit.controllers.CommunitiesController
import com.reidswan.reidit.data.queries.AccountsQueries
import com.reidswan.reidit.data.queries.CommunitiesQueries
import com.reidswan.reidit.data.queries.QuerySource
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
    val querySource = QuerySource(Configuration.dependencies.database)
    val communitiesController = CommunitiesController(querySource.communitiesQueries, querySource.postQueries)
    val accountsController = AccountsController(querySource.accountQueries)
    routing {
        get("/communities") {
            val pageParamResults = getValidPageParams(call.request.queryParameters)
            when (pageParamResults) {
                is Right -> call.respond(HttpStatusCode.BadRequest, pageParamResults.right)
                is Left -> {
                    call.respond(HttpStatusCode.OK, communitiesController.getCommunities(pageParamResults.left))
                }
            }
        }

        get("/account/{username}") {
            val username = call.parameters["username"] ?:
                throw HttpException("No username specified", HttpStatusCode.BadRequest)
            val account = accountsController.getAccountByUsername(username) ?:
                throw HttpException("No user found for username $username", HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, account.toPublic())
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

        get("/community/{communityName}") {
            val communityName = call.parameters["communityName"] ?:
                throw HttpException("No community name specified", HttpStatusCode.BadRequest)
            val pageParamResults = getValidPageParams(call.request.queryParameters)
            when (pageParamResults) {
                is Right -> call.respond(HttpStatusCode.BadRequest, pageParamResults.right)
                is Left -> {
                    val posts = communitiesController.getPostsInCommunity(communityName, pageParamResults.left) ?:
                        throw HttpException("No community found with name '$communityName'", HttpStatusCode.NotFound)
                    call.respond(HttpStatusCode.OK, posts)
                }
            }

        }

        authenticate {
            post("/community/{communityName}") {
                val user: UserPrincipal = call.authentication.principal<UserPrincipal>()!!
                val communityName = call.parameters["communityName"] ?:
                    throw HttpException("No community name specified", HttpStatusCode.BadRequest)
                val createCommunityRequest = call.receive<CreateCommunityRequest>()

            }

            // TODO: remove /test endpoint
            get("/test") {
                val user: UserPrincipal = call.authentication.principal<UserPrincipal>()!!
                call.respond(SuccessResponse("Congratulations on getting through security, ${user.username}!"))
            }
        }
    }
}