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
package de.entikore.composedex.domain.usecase

import de.entikore.composedex.domain.WorkResult
import de.entikore.composedex.domain.asWorkResult
import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.domain.repository.TypeRepository
import de.entikore.composedex.domain.usecase.base.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * This use case returns the latest list of all [Type].
 */
class GetTypesUseCase @Inject constructor(private val repository: TypeRepository) :
    UseCase<Flow<WorkResult<List<Type>>>>() {
    override operator fun invoke(): Flow<WorkResult<List<Type>>> =
        repository.getTypes().distinctUntilChanged().map {
            it.filter { processedType -> !Type.isUnsupportedType(processedType.name) }
        }.asWorkResult()
}
