/*
 * Copyright 2025 Entikore
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
package de.entikore.composedex.domain.usecase.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Base class for Use Cases that fetch domain data.
 * It standardizes use case execution by automatically:
 *  - Running the core logic on a specified [CoroutineDispatcher].
 *  - Providing a top-level error catch mechanism that emits [Result].
 *
 * This class is designed to be implemented by concrete use cases.
 */
abstract class BaseFetchUseCase<in P, out R>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected abstract fun execute(params: P): Flow<Result<R>>

    operator fun invoke(params: P): Flow<Result<R>> {
        return flow {
            emitAll(execute(params))
        }.flowOn(
            dispatcher
        )
    }

    operator fun invoke(): Flow<Result<R>> {
        @Suppress("UNCHECKED_CAST")
        return invoke(Unit as P)
    }
}
