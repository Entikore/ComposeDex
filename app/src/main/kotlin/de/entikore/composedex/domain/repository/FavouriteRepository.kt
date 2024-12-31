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
 * Repository interface for managing favourite [Pokemon] data.
 */
interface FavouriteRepository {
    /**
     * Retrieves all favourite Pokemon.
     *
     * @return a [Flow] emitting a list of [Pokemon] that are marked as favourites.
     *         The flow will emit updates whenever the data changes.
     */
    fun getFavourites(): Flow<List<Pokemon>>

    /**
     * Updates the favourite status for a Pokemon with the given id.
     *
     * @param id the unique id of the Pokemon to update.
     * @param isFavourite the new favourite status for the Pokemon.
     */
    suspend fun updateIsFavourite(id: Int, isFavourite: Boolean)
}
