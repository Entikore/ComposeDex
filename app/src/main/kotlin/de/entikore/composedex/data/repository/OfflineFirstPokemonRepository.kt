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

import de.entikore.composedex.data.local.datasource.PokemonLocalDataSource
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.ApiResponse
import de.entikore.composedex.data.remote.RemoteDataSource
import de.entikore.composedex.data.remote.model.PokemonInfoRemote
import de.entikore.composedex.data.remote.model.pokemon.toEntity
import de.entikore.composedex.data.remote.model.species.toEntity
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.composedex.data.util.RETRY_COUNT
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import timber.log.Timber

/**
 * Offline-first implementation of [PokemonRepository]. Tries to retrieve data from a local
 * data source, fetching it from a remote data source if necessary.
 */
class OfflineFirstPokemonRepository(
    private val localDataSource: PokemonLocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : PokemonRepository {
    override fun getPokemonByName(name: String): Flow<Pokemon> {
        return localDataSource.getPokemonWithSpeciesTypesAndVarietiesByName(name).map {
            it!!.asExternalModel()
        }.retryWhen { cause, attempt ->
            if (cause is NullPointerException && attempt < RETRY_COUNT) {
                Timber.d("Attempt $attempt of $RETRY_COUNT to fetch pokemon $name, failed previously because: $cause")
                when (val remotePokemon = remoteDataSource.getPokemonInfoRemoteByName(name)) {
                    is ApiResponse.Error<PokemonInfoRemote> -> {
                    }
                    is ApiResponse.Success<PokemonInfoRemote> -> {
                        val entityModel = PokemonWithSpeciesTypesAndVarieties(
                            remotePokemon.data.pokemon.toEntity(),
                            remotePokemon.data.species.toEntity(
                                remotePokemon.data.evolutionChain
                            ),
                            remotePokemon.data.types.toEntity(),
                            remotePokemon.data.species.varieties.toEntity()
                        )
                        localDataSource.insertPokemonWithSpeciesTypesAndVarieties(
                            entityModel
                        )
                    }
                }
                true
            } else {
                Timber.d("Could not fetch pokemon by name $name: $cause")
                false
            }
        }
    }

    override fun getPokemonById(id: Int): Flow<Pokemon> {
        return localDataSource.getPokemonWithSpeciesTypesAndVarietiesById(id).map {
            it!!.asExternalModel()
        }.retryWhen { cause, attempt ->
            if (cause is NullPointerException && attempt < RETRY_COUNT) {
                Timber.d(
                    "Attempt $attempt of $RETRY_COUNT to fetch pokemon with id $id, failed previously because: $cause"
                )
                when (val remotePokemon = remoteDataSource.getPokemonInfoRemoteById(id)) {
                    is ApiResponse.Error<PokemonInfoRemote> -> {
                    }
                    is ApiResponse.Success<PokemonInfoRemote> -> {
                        val entityModel = PokemonWithSpeciesTypesAndVarieties(
                            remotePokemon.data.pokemon.toEntity(),
                            remotePokemon.data.species.toEntity(
                                remotePokemon.data.evolutionChain
                            ),
                            remotePokemon.data.types.toEntity(),
                            remotePokemon.data.species.varieties.toEntity()
                        )
                        localDataSource.insertPokemonWithSpeciesTypesAndVarieties(
                            entityModel
                        )
                    }
                }
                true
            } else {
                Timber.d("Could not fetch pokemon by id $id: $cause")
                false
            }
        }
    }

    override suspend fun updatePokemonSprite(id: Int, sprite: String) {
        Timber.d("Update pokemon sprite with id $id to new value: $sprite")
        localDataSource.updatePokemonSprite(id, sprite)
    }

    override suspend fun updatePokemonArtwork(id: Int, artwork: String) {
        Timber.d("Update pokemon artwork with id $id to new value: $artwork")
        localDataSource.updatePokemonArtwork(id, artwork)
    }

    override suspend fun updatePokemonCry(id: Int, cry: String) {
        Timber.d("Update pokemon cry with id $id to new value: $cry")
        localDataSource.updatePokemonCry(id, cry)
    }

    override suspend fun updateVarietyArtwork(name: String, artwork: String) {
        Timber.d("Update variety artwork with name $name to new value: $artwork")
        localDataSource.updateVarietyArtwork(name, artwork)
    }
}
