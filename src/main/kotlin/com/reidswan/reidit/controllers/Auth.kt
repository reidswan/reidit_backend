package com.reidswan.reidit.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.reidswan.reidit.common.AccountResult
import com.reidswan.reidit.config.Configuration
import java.security.MessageDigest
import kotlin.random.Random


const val SALT_LENGTH = 16
const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

object AuthController {
    fun generateSalt(): String {
        return (0 until SALT_LENGTH).fold("") {acc, _ -> acc + ALPHABET[Random.nextInt(ALPHABET.length)] }.toString()
    }

    fun hashPassword(src: String, salt: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-512")
            .digest((src + salt).toByteArray(Charsets.UTF_16))
        return bytes.fold("") {acc, byte -> acc + "%02X".format(byte)}.toString()
    }

    fun generateJWT(account: AccountResult): String = JWT.create()
        .withIssuer(Configuration.config.jwt.issuer)
        .withClaim("username", account.username)
        .withClaim("accountId", account.accountId)
        .sign(Algorithm.HMAC256(Configuration.config.jwt.secret))

    val jwtDecoder: JWTVerifier by lazy {
        JWT.require(Algorithm.HMAC256(Configuration.config.jwt.secret))
            .withIssuer(Configuration.config.jwt.issuer)
            .build()
    }
}