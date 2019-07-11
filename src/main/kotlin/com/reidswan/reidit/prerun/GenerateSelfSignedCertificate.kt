package com.reidswan.reidit.prerun

import io.ktor.network.tls.certificates.generateCertificate
import java.io.File

@Suppress("unused")
object GenerateSelfSignedCertificate {
    @JvmStatic
    fun main(args: Array<String>) {
        val cert = File("build/tempCert.jks").apply {
            parentFile.mkdirs()
        }

        if (!cert.exists()) {
            generateCertificate(cert,
                keyAlias = "tempKey",
                jksPassword = "thisIsADevKey",
                keyPassword = "thisShouldNotBeUsedIRL")
        }
    }
}