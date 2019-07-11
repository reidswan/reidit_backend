package com.reidswan.reidit.config

import org.jetbrains.exposed.sql.*
import com.google.gson.Gson

data class DBConfig(val dbName: String, val userName: String, val password: String, val hostPort: String)
data class ServerConfig(val port: Int, val sslPort: Int)
data class Config(val database: DBConfig, val server: ServerConfig)
data class Dependencies(val database: Database)

object Configuration {
    private val configString by lazy {
        val configFile = "/config/${(System.getenv("NAMESPACE") ?: "local").toLowerCase()}.json"
        Config::class.java.getResource(configFile).readText()
    }

    val config: Config by lazy {
        Gson().fromJson(configString, Config::class.java)
    }

    private val dbConnection by lazy {
        Database.connect(
            url = "jdbc:postgresql://${config.database.hostPort}/${config.database.dbName}",
            driver = "org.postgresql.Driver",
            user = config.database.userName,
            password = config.database.password)
    }

    val dependencies by lazy {
        Dependencies(dbConnection)
    }
}