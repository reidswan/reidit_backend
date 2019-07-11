package com.reidswan.reidit

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.engine.commandLineEnvironment

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val server = embeddedServer(Netty, commandLineEnvironment(args))
        server.start(wait = true)
    }
}