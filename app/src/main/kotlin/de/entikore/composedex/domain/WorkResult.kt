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
package de.entikore.composedex.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException

private const val RETRY_TIME_IN_MILLIS = 3_000L

/**
 * Represents the result of an operation, typically asynchronous work.
 *
 * This sealed interface encapsulates the three possible states of an operation:
 * - [Success]: The operation completed successfully and returned data.
 * - [Error]: The operation failed, possibly with an exception.
 * - [Loading]: The operation is still in progress.
 */
sealed interface WorkResult<out T> {
    data class Success<T>(val data: T) : WorkResult<T>
    data class Error(val exception: Throwable? = null) : WorkResult<Nothing>
    data object Loading : WorkResult<Nothing>
}

fun <T> Flow<T>.asWorkResult(): Flow<WorkResult<T>> {
    return this
        .map<T, WorkResult<T>> {
            WorkResult.Success(it)
        }
        .onStart { emit(WorkResult.Loading) }
        .retryWhen { cause, _ ->
            if (cause is IOException) {
                emit(WorkResult.Error(cause))
                delay(RETRY_TIME_IN_MILLIS)
                true
            } else {
                false
            }
        }
        .catch {
            emit(WorkResult.Error(it))
        }
}
