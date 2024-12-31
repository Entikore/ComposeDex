/*
 * Copyright 2024 Entikore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.entikore.composedex.data.remote

import retrofit2.HttpException
import retrofit2.Response

/**
 * Represents the result of a network operation.
 *
 * This sealed class is used to encapsulate the outcome of a network request,
 * providing a structured way to handle success, error, and exception cases.
 *
 * It has three possible states:
 * - [Success] Indicated by the `Success` data class, containing the HTTP status code and the
 *  successful result data.
 * - [Error] Indicated by the `Error` data class, containing the HTTP status code and an optional
 *  error message.
 * - [Exception] Indicated by the `Exception` data class, containing the underlying exception
 *  that occurred during the network operation.
 *
 * @param T The type of data returned in the success case. Must be a non-nullable type.
 */
sealed class NetworkResult<T : Any> {
    data class Success<T : Any>(val code: Int, val data: T) : NetworkResult<T>()
    data class Error<T : Any>(val code: Int, val errorMsg: String?) : NetworkResult<T>()
    data class Exception<T : Any>(val e: Throwable) : NetworkResult<T>()
}

fun <T : Any> NetworkResult<T>.getSuccessOrThrow(): T {
    return when (this) {
        is NetworkResult.Success -> data
        else -> throw RemoteDataSourceException("NetworkResult is not Success")
    }
}

fun <T : Any> NetworkResult<T>.getSuccessOrThrow(message: String): T {
    return when (this) {
        is NetworkResult.Success -> data
        else -> throw RemoteDataSourceException(message)
    }
}

/**
 * An interface for handling network API calls and wrapping the results in a [NetworkResult].
 *
 * This interface provides a centralized and consistent way to make network calls and handle
 * their responses, including success, error, and exception cases. It encapsulates common
 * error handling logic, reducing boilerplate code.
 *
 * The [handleApi] function is designed to be used with suspend functions that represent
 * network API calls. It wraps the API call in a try-catch block and returns a [NetworkResult]
 * object, which can be used to handle different response types in a type-safe way.
 */
@Suppress("TooGenericExceptionCaught")
interface ApiHandler {
    suspend fun <T : Any> handleApi(
        execute: suspend () -> Response<T>
    ): NetworkResult<T> {
        return try {
            val response = execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                NetworkResult.Success(response.code(), body)
            } else {
                NetworkResult.Error(code = response.code(), errorMsg = response.errorBody().toString())
            }
        } catch (e: HttpException) {
            NetworkResult.Error(e.code(), e.message())
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }
}
