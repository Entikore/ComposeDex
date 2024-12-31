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
package de.entikore.composedex.data.local.datasource

import de.entikore.composedex.data.local.dao.PokemonDao
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.local.entity.pokemon.update.FavouriteUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Local data source for [PokemonWithSpeciesTypesAndVarieties] that are marked as favourites.
 */
class FavouriteLocalDataSource(
    private val pokemonDao: PokemonDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun updateIsFavourite(pokemonId: Int, isFavourite: Boolean) =
        withContext(dispatcher) {
            return@withContext pokemonDao.updateFavourite(FavouriteUpdate(pokemonId, isFavourite))
        }

    fun getAllFavourites(): Flow<List<PokemonWithSpeciesTypesAndVarieties>> =
        pokemonDao.getAllFavourites()
}
