package com.reidswan.reidit.controllers

import java.security.SecureRandom
import java.security.MessageDigest

const val SALT_LENGTH = 16
const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

object AuthController {
    fun generateSalt(): String {
        val random = SecureRandom.getInstanceStrong()
        return (0 until SALT_LENGTH).fold("") {acc, _ -> acc + ALPHABET[random.nextInt(ALPHABET.length)] }.toString()
    }

    fun String.hashPassword(salt: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-512")
            .digest((this + salt).toByteArray(Charsets.UTF_16))
        return bytes.fold("") {acc, byte -> acc + "%02X".format(byte)}.toString()
    }

}