package com.github.gedzeppelin.kotlinutils

class DefaultResponseError : Exception("The response execution failed")

sealed class Response<out T : Any?> {
    data class Success<out T : Any?>(val payload: T) : Response<T>()
    data class Error(
        val exception: Throwable = DefaultResponseError(),
        val body: String? = null,
        val statusCode: Int? = null
    ) : Response<Nothing>()

    val isSuccess: Boolean get() = this is Success

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[payload=$payload]"
        is Error -> "Failure=[exception=$exception]"
    }
}

sealed class NullableResponse<out T : Any?> {
    data class Success<out T : Any>(val payload: T) : NullableResponse<T>()
    data class Error(
        val exception: Throwable = DefaultResponseError(),
        val body: String? = null,
        val statusCode: Int? = null
    ) : NullableResponse<Nothing>()

    val isSuccess: Boolean get() = this is Success

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[payload=$payload]"
        is Error -> "Failure=[exception=$exception]"
    }
}