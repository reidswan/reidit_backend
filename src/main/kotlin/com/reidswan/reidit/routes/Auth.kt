package com.reidswan.reidit.routes

import io.ktor.application.*
import io.ktor.auth.Authentication

@Suppress("unused")
fun Application.auth() {
    install(Authentication) {

    }
}