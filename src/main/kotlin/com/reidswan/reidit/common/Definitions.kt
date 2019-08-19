package com.reidswan.reidit.common

import io.ktor.http.HttpStatusCode

sealed class Either<out A, out B> {
    class Left<A>(val left: A): Either<A, Nothing>()
    class Right<B>(val right: B): Either<Nothing, B>()
}

class PageParameters (val pageNumber: Int, val pageSize: Int) {
    val from = (pageNumber - 1) * pageSize
    val size = pageSize
}

val SAMPLE_PAGE_PARAMETERS: PageParameters = PageParameters(1, 3)
val DEFAULT_PAGE_PARAMETERS: PageParameters = PageParameters(1, 50)

/** Exceptions **/

data class ErrorResponse(val message: String) {
    val success: Boolean = false
}

class SuccessResponse(val message: String) {
    val success: Boolean = true
}

class HttpException(val response: String, val statusCode: HttpStatusCode): Throwable()