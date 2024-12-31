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
import de.entikore.composedex.domain.repository.FavouriteRepository
import de.entikore.composedex.util.TestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class FakeFavouriteRepository : FavouriteRepository, FailableFakeRepository() {

    private val favouritePokemon = MutableStateFlow<List<Pokemon>>(emptyList())

    override fun getFavourites(): Flow<List<Pokemon>> =
        if (shouldReturnError) {
            flow {
                throw TestException(EXPECTED_TEST_EXCEPTION)
            }
        } else {
            favouritePokemon.asStateFlow()
        }

    override suspend fun updateIsFavourite(id: Int, isFavourite: Boolean) {
        val currentFavourites = favouritePokemon.value.toMutableList()
        currentFavourites.removeIf { !isFavourite && it.id == id }
        favouritePokemon.value = currentFavourites
    }

    fun addPokemon(vararg pokemon: Pokemon) {
        val favourites = favouritePokemon.value.toMutableList().apply {
            addAll(pokemon)
        }
        favouritePokemon.value = favourites
    }
}
