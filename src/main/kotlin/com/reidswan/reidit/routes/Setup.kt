package com.reidswan.reidit.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.features.*
import io.ktor.gson.*
import com.reidswan.reidit.config.*
import com.reidswan.reidit.common.*
import com.reidswan.reidit.common.Either.*
import io.ktor.response.respond
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.text.toIntOrNull

val logger: Logger = LoggerFactory.getLogger(Application::class.java)

const val DEFAULT_PAGE_NUMBER = "1"
const val DEFAULT_PAGE_SIZE = "50"

fun getValidPageParams(params: Parameters): Either<PageParameters, Error> {
    val pageNumber = (params["page_number"] ?: DEFAULT_PAGE_NUMBER).toIntOrNull()
    val pageSize = (params["page_size"] ?: DEFAULT_PAGE_SIZE).toIntOrNull()
    return if (pageNumber == null || pageSize == null){
        Right(Error("page_number and page_size params should be valid integers"))
    } else if (pageNumber <= 0 || pageSize <= 0) {
        Right(Error("page_number and page_size params should be positive"))
    } else {
        Left(PageParameters(pageNumber, pageSize))
    }
}

@Suppress("unused")
fun Application.setup() {
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }
    install(ContentNegotiation) {
        gson {
            serializeNulls()
        }
    }
    install(HttpsRedirect) {
        sslPort = Configuration.config.server.sslPort
        permanentRedirect = true
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            logger.error(cause.message)
            call.respond(HttpStatusCode.InternalServerError, "Something went wrong processing the request")
        }
        exception<HttpException> { cause ->
            logger.error("HTTP error (" + cause.statusCode + "): " + cause.message)
            call.respond(cause.statusCode, ErrorResponse(cause.response))
        }
        status(HttpStatusCode.NotFound) {
            logger.info(it.toString())
            call.respond(HttpStatusCode.NotFound, ErrorResponse("The requested path could not be found on this server"))
        }
    }

}
