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
package de.entikore.composedex.fake.repository

import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.repository.PokemonRepository
import de.entikore.composedex.domain.util.replace
import de.entikore.composedex.util.TestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePokemonRepository : PokemonRepository, FailableFakeRepository() {

    private var availablePokemonList: MutableList<Pokemon> = mutableListOf()

    override fun getPokemonByName(name: String): Flow<Pokemon> = flow {
        if (shouldReturnError) {
            throw TestException(EXPECTED_TEST_EXCEPTION)
        }
        emit(
            availablePokemonList.firstOrNull { it.name == name } ?: throw TestException(
                POKEMON_WITH_NAME_NOT_FOUND
            )
        )
    }

    override fun getPokemonById(id: Int): Flow<Pokemon> = flow {
        if (shouldReturnError) {
            throw TestException(EXPECTED_TEST_EXCEPTION)
        }
        emit(
            availablePokemonList.firstOrNull { it.id == id } ?: throw TestException(
                POKEMON_WITH_ID_NOT_FOUND
            )
        )
    }

    override suspend fun updatePokemonSprite(id: Int, sprite: String) {
        if (shouldReturnError) {
            throw TestException(EXPECTED_TEST_EXCEPTION)
        }
        availablePokemonList.replace(availablePokemonList.first { it.id == id }.copy(sprite = sprite)) {
            it.id == id
        }
    }

    override suspend fun updatePokemonArtwork(id: Int, artwork: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePokemonCry(id: Int, cry: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateVarietyArtwork(name: String, artwork: String) {
        TODO("Not yet implemented")
    }

    fun addPokemon(vararg pokemon: Pokemon) {
        availablePokemonList.addAll(pokemon)
    }

    companion object {
        const val POKEMON_WITH_NAME_NOT_FOUND = "Pokemon by name not found in FakePokemonRepository"
        const val POKEMON_WITH_ID_NOT_FOUND = "Pokemon by id not found in FakePokemonRepository"
    }
}
