@file:Suppress("ConstantConditionIf", "unused")

package com.github.gedzeppelin.kotlinutils.util

import android.util.Log
import com.github.gedzeppelin.kotlinutils.Paginatable
import com.github.gedzeppelin.kotlinutils.Response
import kotlinx.coroutines.*
import retrofit2.Response as HttpResponse

const val MAX_RETRIES: Int = 5
const val IS_DEBUG = true

class NullResponseBodyException : Exception("The response body was null")
class SomeFailedException : Exception("One or more request failed")
class RetryFailedException(count: Int) : Exception("The request has failed $count times")

fun HttpResponse<*>.rawBody(): String? = try {
    errorBody()?.string()
} catch (_: Throwable) {
    null
}

fun HttpResponse<*>.debugError(isDebug: Boolean): Response.Error {
    val rawBody = rawBody()
    if (isDebug) Log.i("ERROR RESPONSE BODY", rawBody ?: "The error response body was empty or null")
    return Response.Error(NullResponseBodyException(), rawBody, code())
}

suspend fun <T : Any> makeRequest(
    isDebug: Boolean = IS_DEBUG,
    suspendBlock: suspend () -> HttpResponse<T>
): Response<T> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on isDebug mode.
    if (isDebug) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} HTTP REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        val responseBody = response.body()

        if (responseBody != null) Response.Success(responseBody)
        else response.debugError(isDebug)
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun <T : Any> makeCustomRequest(
    isDebug: Boolean = IS_DEBUG,
    suspendBlock: suspend () -> HttpResponse<T>,
    customHandling: (response: HttpResponse<T>) -> Response<T>
): Response<T> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on isDebug mode.
    if (isDebug) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        customHandling(response)
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun <T : Any, P : Paginatable<T>> makePaginatedRequest(
    isDebug: Boolean = IS_DEBUG,
    suspendBlock: suspend () -> HttpResponse<P>
): Response<List<T>> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on isDebug mode.
    if (isDebug) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} HTTP REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        val responseBody = response.body()

        if (responseBody != null) Response.Success(responseBody.results)
        else response.debugError(isDebug)
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun <T : Any, P : Paginatable<T>> makeCustomPaginatedRequest(
    isDebug: Boolean = IS_DEBUG,
    suspendBlock: suspend () -> HttpResponse<P>,
    customHandling: (response: HttpResponse<P>) -> Response<List<T>>
): Response<List<T>> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on isDebug mode.
    if (isDebug) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        customHandling(response)
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun <T : Any, P : Paginatable<T>> makePaginatedFirstRequest(
    isDebug: Boolean = IS_DEBUG,
    suspendBlock: suspend () -> HttpResponse<P>
): Response<T?> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on isDebug mode.
    if (isDebug) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} HTTP REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        val responseBody = response.body()

        if (responseBody != null) {
            val results = responseBody.results
            val payload = if (results.isEmpty()) null else results[0]
            Response.Success(payload)
        } else {
            response.debugError(isDebug)
        }
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun <T : Any, P : Paginatable<T>> makeCustomPaginatedFirstRequest(
    isDebug: Boolean = IS_DEBUG,
    suspendBlock: suspend () -> HttpResponse<P>,
    customHandling: (response: HttpResponse<P>) -> Response<T>
): Response<T> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on isDebug mode.
    if (isDebug) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        customHandling(response)
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun checkRequestSuccess(
    isDebug: Boolean = IS_DEBUG,
    suspendBlock: suspend () -> HttpResponse<*>
): Boolean = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        return@withContext false
    }

    // Show request information when running on isDebug mode.
    if (isDebug) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} HTTP REQUEST", rawRequest.url().toString())
    }

    return@withContext response.isSuccessful
}

suspend fun <T : Any> checkCustomRequestSuccess(
    isDebug: Boolean = IS_DEBUG,
    suspendBlock: suspend () -> HttpResponse<T>,
    customHandling: (response: HttpResponse<T>) -> Boolean
): Boolean = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (isDebug) e.printStackTrace(System.err)
        return@withContext false
    }

    // Show request information when running on isDebug mode.
    if (isDebug) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} REQUEST", rawRequest.url().toString())
    }

    return@withContext customHandling(response)
}

suspend fun <T : Any?> retryRequest(
    count: Int = MAX_RETRIES,
    requestBlock: suspend () -> Response<T>
): Response<T> {
    for (i in 0..count) {
        val request = requestBlock()
        if (request.isSuccess) return request
    }
    return Response.Error(RetryFailedException(count))
}

suspend fun retryBooleanRequest(
    count: Int = MAX_RETRIES,
    requestBlock: suspend () -> Boolean
): Boolean {
    for (i in 0..count) {
        val result = requestBlock()
        if (result) return true
    }
    return false
}

suspend fun <S : Any> retryRequests(
    scope: CoroutineScope,
    identifierList: List<S>,
    count: Int = MAX_RETRIES,
    requestBlock: suspend (S) -> Boolean
): Boolean {
    val requestList = identifierList.toMutableList()
    for (i in 0..count) {
        val deferrals = requestList.map { scope.async { requestBlock(it) } }
        val responses = deferrals.awaitAll()
        val hasFailed = responses.contains(false)
        if (hasFailed) {
            responses.forEachIndexed { idx, b -> if (!b) requestList.removeAt(idx) }
        } else {
            return true
        }
    }
    return false
}