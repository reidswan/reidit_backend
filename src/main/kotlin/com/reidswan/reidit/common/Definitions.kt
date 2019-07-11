package com.reidswan.reidit.common

data class Error(val error: String)

sealed class Either<out A, out B> {
    class Left<A>(val left: A): Either<A, Nothing>()
    class Right<B>(val right: B): Either<Nothing, B>()
}

class PageParameters (val pageNumber: Int, val pageSize: Int) {
    val from = (pageNumber - 1) * pageSize
    val size = pageSize
}