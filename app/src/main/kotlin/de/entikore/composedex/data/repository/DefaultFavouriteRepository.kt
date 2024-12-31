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
package de.entikore.composedex.data.repository

import de.entikore.composedex.data.local.datasource.FavouriteLocalDataSource
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Implementation of [FavouriteRepository], since whether a [Pokemon] is a favourite or not is a
 * choice of the user, no remote data source is needed.
 */
class DefaultFavouriteRepository(private val localDataSource: FavouriteLocalDataSource) : FavouriteRepository {
    override fun getFavourites(): Flow<List<Pokemon>> =
        localDataSource.getAllFavourites().map {
            it.map { pokemon -> pokemon.asExternalModel() }
        }

    override suspend fun updateIsFavourite(id: Int, isFavourite: Boolean) {
        Timber.d("Update favourite status for $id to $isFavourite")
        localDataSource.updateIsFavourite(id, isFavourite)
    }
}
