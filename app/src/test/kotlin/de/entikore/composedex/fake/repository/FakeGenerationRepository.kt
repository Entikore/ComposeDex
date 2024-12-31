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

import de.entikore.composedex.domain.model.generation.Generation
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.repository.GenerationRepository
import de.entikore.composedex.util.TestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGenerationRepository : GenerationRepository, FailableFakeRepository() {

    private var availablePokemonList: MutableList<Pokemon> = mutableListOf()
    private var availableGenerationList: MutableList<Generation> = mutableListOf()

    override fun getGenerations(): Flow<List<Generation>> = flow {
        if (shouldReturnError) {
            throw TestException(EXPECTED_TEST_EXCEPTION)
        }
        emit(availableGenerationList)
    }

    override fun getGenerationByName(name: String): Flow<Generation> = flow {
        if (shouldReturnError) {
            throw TestException(EXPECTED_TEST_EXCEPTION)
        }
        emit(
            availableGenerationList.firstOrNull { it.name == name } ?: throw TestException(
                GENERATION_WITH_NAME_NOT_FOUND
            )
        )
    }

    override fun getGenerationById(id: Int): Flow<Generation> = flow {
        if (shouldReturnError) {
            throw TestException(EXPECTED_TEST_EXCEPTION)
        }
        emit(
            availableGenerationList.firstOrNull { it.id == id } ?: throw TestException(
                GENERATION_WITH_ID_NOT_FOUND
            )
        )
    }

    override fun getPokemonOfGenerationByName(name: String): Flow<List<Pokemon>> = flow {
        if (shouldReturnError) {
            throw TestException(EXPECTED_TEST_EXCEPTION)
        }
        if (!(availableGenerationList.any { it.name == name })) {
            throw TestException(GENERATION_WITH_NAME_NOT_FOUND)
        }
        emit(
            availablePokemonList.filter { pokemon ->
                pokemon.name in availableGenerationList.first {
                        generation ->
                    generation.name == name
                }.pokemonInGeneration
            }
        )
    }

    override fun getPokemonOfGenerationById(id: Int): Flow<List<Pokemon>> = flow {
        if (shouldReturnError) {
            throw TestException(EXPECTED_TEST_EXCEPTION)
        }
        if (!(availableGenerationList.any { it.id == id })) {
            throw TestException(GENERATION_WITH_ID_NOT_FOUND)
        }
        emit(
            availablePokemonList.filter { pokemon ->
                pokemon.name in availableGenerationList.first {
                        generation ->
                    generation.id == id
                }.pokemonInGeneration
            }
        )
    }

    fun addPokemon(vararg pokemon: Pokemon) {
        availablePokemonList.addAll(pokemon)
    }

    fun addGenerations(vararg generations: Generation) {
        availableGenerationList.addAll(generations)
    }

    companion object {
        const val GENERATION_WITH_NAME_NOT_FOUND =
            "Generation by name not found in FakeGenerationRepository"
        const val GENERATION_WITH_ID_NOT_FOUND =
            "Generation by id not found in FakeGenerationRepository"
    }
}
