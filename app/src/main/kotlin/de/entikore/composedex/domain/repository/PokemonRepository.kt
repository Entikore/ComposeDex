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
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [Pokemon] data.
 */
interface PokemonRepository {

    /**
     * Retrieves a specific Pokemon by its name.
     *
     * @param name the name of the pokemon to retrieve.
     * @return a [Flow] emitting the [Pokemon] object with the specified name.
     */
    fun getPokemonByName(name: String): Flow<Pokemon>

    /**
     * Retrieves a specific Pokemon by its id.
     *
     * @param id the name of the pokemon to retrieve.
     * @return a [Flow] emitting the [Pokemon] object with the specified id.
     */
    fun getPokemonById(id: Int): Flow<Pokemon>

    /**
     * Updates the sprite URI for a Pokemon with the given id.
     *
     * @param id the unique id of the Pokemon to update.
     * @param sprite the new sprite URI for the Pokemon.
     */
    suspend fun updatePokemonSprite(id: Int, sprite: String)

    /**
     * Updates the artwork URI for a Pokemon with the given id.
     *
     * @param id the unique id of the Pokemon to update.
     * @param artwork the new artwork URI for the Pokemon.
     */
    suspend fun updatePokemonArtwork(id: Int, artwork: String)

    /**
     * Updates the cry URI for a Pokemon with the given id.
     *
     * @param id the unique id of the Pokemon to update.
     * @param cry the new cry URI for the Pokemon.
     */
    suspend fun updatePokemonCry(id: Int, cry: String)

    /**
     * Updates the variety artwork URI for a Pokemon with the given id.
     *
     * @param name the name of the PokemonVariety to update.
     * @param artwork the new artwork URI for the PokemonVariety.
     */
    suspend fun updateVarietyArtwork(name: String, artwork: String)
}
