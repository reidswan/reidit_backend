package com.reidswan.reidit.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.routing
import io.ktor.routing.get
import com.reidswan.reidit.controllers.CommunitiesController
import com.reidswan.reidit.common.Either.*
import io.ktor.response.respond

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
    }
}