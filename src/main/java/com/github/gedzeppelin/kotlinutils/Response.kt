package com.github.gedzeppelin.kotlinutils

sealed class Response<out T : Any> {
    data class Success<out T : Any>(val payload: T) : Response<T>()
    data class Error(val exception: Throwable, val body: String? = null, val statusCode: Int? = null) : Response<Nothing>()

    val isSuccess: Boolean get() = this is Success

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[payload=$payload]"
        is Error -> "Failure=[exception=$exception]"
    }
}