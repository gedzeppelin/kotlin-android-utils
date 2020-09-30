@file:Suppress("ConstantConditionIf", "unused")

package com.github.gedzeppelin.kotlinutils.util

import android.util.Log
import com.github.gedzeppelin.kotlinutils.Paginatable
import com.github.gedzeppelin.kotlinutils.Response
import kotlinx.coroutines.*
import retrofit2.Response as HttpResponse

const val IS_DEBUG: Boolean = true
const val MAX_RETRIES: Int = 5

class NullResponseBodyException : Exception("The response body was null")

fun <T : Any> HttpResponse<T>.rawBody(): String? = try {
    errorBody()?.string()
} catch (_: Throwable) {
    null
}

suspend fun <T : Any> makeRequest(
    suspendBlock: suspend () -> HttpResponse<T>
): Response<T> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on debug mode.
    if (IS_DEBUG) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} HTTP REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        val responseBody = response.body()

        if (responseBody != null) Response.Success(responseBody)
        else {
            val rawBody = response.rawBody()
            Log.i("REQUEST BODY", rawBody ?: "Empty body")
            Response.Error(NullResponseBodyException(), rawBody, response.code())
        }
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun <T : Any> makeCustomRequest(
    suspendBlock: suspend () -> HttpResponse<T>,
    customHandling: (response: HttpResponse<T>) -> Response<T>
): Response<T> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on debug mode.
    if (IS_DEBUG) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        if (response.isSuccessful) {
            val responseBody = response.body()

            if (responseBody != null) Response.Success(responseBody)
            else Response.Error(NullResponseBodyException(), response.rawBody(), response.code())
        } else {
            customHandling(response)
        }
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun <T : Any, P : Paginatable<T>> makePaginatedRequest(
    suspendBlock: suspend () -> HttpResponse<P>
): Response<List<T>> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on debug mode.
    if (IS_DEBUG) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} HTTP REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        val responseBody = response.body()

        if (responseBody != null) Response.Success(responseBody.results)
        else Response.Error(NullResponseBodyException(), response.rawBody(), response.code())
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun <T : Any, P : Paginatable<T>, S : Any> makeCustomPaginatedRequest(
    suspendBlock: suspend () -> HttpResponse<P>,
    customHandling: (response: P) -> S
): Response<S> = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        return@withContext Response.Error(e)
    }

    // Show request information when running on debug mode.
    if (IS_DEBUG) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} REQUEST", rawRequest.url().toString())
    }

    return@withContext try {
        val responseBody = response.body()

        if (responseBody != null) Response.Success(customHandling(responseBody))
        else Response.Error(NullResponseBodyException(), response.rawBody(), response.code())
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        Response.Error(e, response.rawBody(), response.code())
    }
}

suspend fun checkRequestSuccess(
    suspendBlock: suspend () -> HttpResponse<*>
): Boolean = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        return@withContext false
    }

    // Show request information when running on debug mode.
    if (IS_DEBUG) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} HTTP REQUEST", rawRequest.url().toString())
    }

    return@withContext response.isSuccessful
}

suspend fun <T : Any> checkCustomRequestSuccess(
    suspendBlock: suspend () -> HttpResponse<T>,
    customHandling: (response: HttpResponse<T>) -> Boolean
): Boolean = withContext(Dispatchers.IO) {
    val response = try {
        suspendBlock()
    } catch (e: Exception) {
        if (IS_DEBUG) e.printStackTrace(System.err)
        return@withContext false
    }

    // Show request information when running on debug mode.
    if (IS_DEBUG) {
        val rawRequest = response.raw().request()
        Log.i("${rawRequest.method()} REQUEST", rawRequest.url().toString())
    }

    return@withContext customHandling(response)
}

suspend fun retryRequest(
    count: Int = MAX_RETRIES,
    requestBlock: suspend () -> Boolean
): Boolean {
    for (i in 0..count) {
        val delete = requestBlock()
        if (delete) return true
    }
    return false
}

suspend fun <S : Any> retryRequests(
    scope: CoroutineScope,
    identifierList: List<S>,
    count: Int = MAX_RETRIES,
    requestBlock: suspend (S) -> Boolean
): Boolean {
    val toDelete = identifierList.toMutableList()
    for (i in 0..count) {
        val deferrals = toDelete.map { scope.async { requestBlock(it) } }
        val responses = deferrals.awaitAll()
        val hasFailed = responses.contains(false)
        if (hasFailed) {
            responses.forEachIndexed { idx, b -> if (!b) toDelete.removeAt(idx) }
        } else {
            return true
        }
    }
    return false
}