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
package de.entikore.composedex.domain.repository

import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.type.Type
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [Type] data.
 */
interface TypeRepository {
    /**
     * Retrieves all types from the repository.
     *
     * @return a [Flow] emitting a list of [Type] objects.
     *         The flow will emit updates whenever the data changes.
     */
    fun getTypes(): Flow<List<Type>>

    /**
     * Retrieves a specific type by its name.
     *
     * @param name the name of the type to retrieve.
     * @return a [Flow] emitting the [Type] object with the specified name.
     */
    fun getTypeByName(name: String): Flow<Type>

    /**
     * Retrieves a list of Pokemon by their type.
     *
     * @param name the name of the type to filter Pokemon by.
     * @return a [Flow] emitting a list of [Pokemon] objects that match the specified type.
     */
    fun getPokemonOfType(name: String): Flow<List<Pokemon>>
}
