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

import de.entikore.composedex.domain.model.generation.Generation
import de.entikore.composedex.domain.model.pokemon.Pokemon
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [Generation] data.
 */
interface GenerationRepository {
    /**
     * Retrieves all generation from the repository.
     *
     * @return a [Flow] emitting a list of [Generation] objects.
     *         The flow will emit updates whenever the data changes.
     */
    fun getGenerations(): Flow<List<Generation>>

    /**
     * Retrieves a specific generation by its name.
     *
     * @param name the name of the generation to retrieve.
     * @return a [Flow] emitting the [Generation] object with the specified name.
     */
    fun getGenerationByName(name: String): Flow<Generation>

    /**
     * Retrieves a specific generation by its name.
     *
     * @param id the id of the generation to retrieve.
     * @return a [Flow] emitting the [Generation] object with the specified id.
     */
    fun getGenerationById(id: Int): Flow<Generation>

    /**
     * Retrieves a list of Pokemon by their generation.
     *
     * @param name the name of the generation to filter Pokemon by.
     * @return a [Flow] emitting a list of [Pokemon] objects that match the specified generation.
     */
    fun getPokemonOfGenerationByName(name: String): Flow<List<Pokemon>>

    /**
     * Retrieves a list of Pokemon by their generation.
     *
     * @param id the id of the generation to filter Pokemon by.
     * @return a [Flow] emitting a list of [Pokemon] objects that match the specified generation.
     */
    fun getPokemonOfGenerationById(id: Int): Flow<List<Pokemon>>
}
