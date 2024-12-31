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
import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.domain.repository.TypeRepository
import de.entikore.composedex.util.TestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTypeRepository : TypeRepository, FailableFakeRepository() {
    private var availablePokemonList: MutableList<Pokemon> = mutableListOf()
    private var availableTypeList: MutableList<Type> = mutableListOf()

    override fun getTypes(): Flow<List<Type>> = flow {
        if (shouldReturnError) {
            throw TestException(EXPECTED_TEST_EXCEPTION)
        }
        emit(availableTypeList)
    }

    override fun getTypeByName(name: String): Flow<Type> =
        flow {
            if (shouldReturnError) {
                throw TestException(EXPECTED_TEST_EXCEPTION)
            }
            emit(
                availableTypeList.firstOrNull { it.name == name } ?: throw TestException(
                    TYPE_WITH_NAME_NOT_FOUND
                )
            )
        }

    override fun getPokemonOfType(name: String): Flow<List<Pokemon>> =
        flow {
            if (shouldReturnError) {
                throw TestException(EXPECTED_TEST_EXCEPTION)
            }
            if (!(availableTypeList.any { it.name == name })) {
                throw TestException(TYPE_WITH_NAME_NOT_FOUND)
            }
            emit(availablePokemonList.filter { it.types.any { type -> type.name == name } })
        }

    fun addPokemon(vararg pokemon: Pokemon) {
        availablePokemonList.addAll(pokemon)
    }

    fun addTypes(vararg types: Type) {
        availableTypeList.addAll(types)
    }

    companion object {
        const val TYPE_WITH_NAME_NOT_FOUND = "Type by name not found in FakeTypeRepository"
    }
}
