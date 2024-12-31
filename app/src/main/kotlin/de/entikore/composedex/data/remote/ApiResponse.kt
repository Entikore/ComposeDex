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

/**
 * Represents the result of an network operation that can either succeed or fail.
 *
 * It provides two possible states:
 * - **Success:** Indicated by the `Success` data class, containing the successful result data.
 * - **Error:** Indicated by the `Error` data class, containing a user-friendly error message and
 * the underlying exception.
 *
 * @param T The type of data returned in the success case.
 */
sealed interface ApiResponse<T> {

    data class Success<T>(val data: T) : ApiResponse<T>

    data class Error<T>(val userMessage: String, val exception: Throwable) : ApiResponse<T>

    companion object {
        fun <T> success(data: T): ApiResponse<T> = Success(data)

        fun <T> error(userErrorMsg: String, exception: Throwable): ApiResponse<T> =
            Error(userErrorMsg, exception)
    }
}
