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
package de.entikore.composedex.domain.usecase

import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.domain.repository.TypeRepository
import de.entikore.composedex.domain.usecase.base.BaseFetchUseCase
import de.entikore.composedex.domain.util.asResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * This use case returns the latest list of all [Type].
 */
class FetchTypesUseCase @Inject constructor(
    private val repository: TypeRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseFetchUseCase<Unit, List<Type>>(dispatcher) {
    override fun execute(params: Unit) =
        repository.getTypes().distinctUntilChanged().map {
            it.filter { processedType -> !Type.isUnsupportedType(processedType.name) }
        }.asResult()
}
