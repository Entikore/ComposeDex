/*
 * Copyright 2025 Entikore
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

import de.entikore.composedex.data.local.datasource.TypeLocalDataSource
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.local.entity.type.TypeEntity
import de.entikore.composedex.data.local.entity.type.asExternalModel
import de.entikore.composedex.data.remote.ApiResponse
import de.entikore.composedex.data.remote.RemoteDataSource
import de.entikore.composedex.data.remote.model.pokemon.toEntity
import de.entikore.composedex.data.remote.model.species.toEntity
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.composedex.data.util.RETRY_COUNT
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.domain.repository.TypeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import timber.log.Timber

/**
 * Offline-first implementation of [TypeRepository]. Tries to retrieve data from a local
 * data source, fetching it from a remote data source if necessary.
 */
class OfflineFirstTypeRepository(
    private val localDataSource: TypeLocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : TypeRepository {
    override fun getTypes(): Flow<List<Type>> =
        localDataSource.getTypeOverview()
            .combine(localDataSource.getAllTypes()) { overview, types ->
                if (types.isNotEmpty() && types.size == overview?.names?.size) {
                    types.map { it.asExternalModel() }
                } else {
                    throw LocalDataException("Not all types in database")
                }
            }.retryWhen { cause, attempt ->
                if ((cause is LocalDataException || cause is NullPointerException) && attempt < RETRY_COUNT) {
                    val remoteTypes = mutableListOf<TypeEntity>()
                    when (val types = remoteDataSource.getPokemonTypes()) {
                        is ApiResponse.Success -> {
                            localDataSource.insertTypeOverview(types.data.toEntity())
                            types.data.results.forEach {
                                when (val type = remoteDataSource.getPokemonTypeByName(it.name)) {
                                    is ApiResponse.Success -> {
                                        remoteTypes.add(type.data.toEntity())
                                    }

                                    is ApiResponse.Error -> {
                                        Timber.d("Error fetching type ${it.name} ${type.exception}")
                                        throw type.exception
                                    }
                                }
                            }
                            for (type in remoteTypes) {
                                localDataSource.insertType(type)
                            }
                        }

                        is ApiResponse.Error -> {
                            Timber.d("Error fetching types ${types.exception}")
                            throw types.exception
                        }
                    }
                    true
                } else {
                    Timber.d("Could not fetch types: $cause")
                    false
                }
            }

    override fun getTypeByName(name: String): Flow<Type> =
        localDataSource.getTypeByName(name).map { it!!.asExternalModel() }
            .retryWhen { cause, attempt ->
                if (cause is NullPointerException && attempt < RETRY_COUNT) {
                    Timber.d("Attempt $attempt of $RETRY_COUNT to fetch type $name, failed previously because: $cause")
                    when (val remoteType = remoteDataSource.getPokemonTypeByName(name)) {
                        is ApiResponse.Error -> {
                            Timber.d("Error fetching type $name failed with ${remoteType.exception}")
                            throw remoteType.exception
                        }

                        is ApiResponse.Success -> {
                            localDataSource.insertType(remoteType.data.toEntity())
                        }
                    }
                    true
                } else {
                    Timber.d("Could not fetch type by name $name: $cause")
                    false
                }
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPokemonOfType(name: String): Flow<List<Pokemon>> {
        val remoteFlow =
            flow<List<Pokemon>> {
                when (val type = remoteDataSource.getPokemonTypeByName(name)) {
                    is ApiResponse.Error -> {
                        Timber.d("Error fetching type ${type.exception}")
                    }
                    is ApiResponse.Success -> {
                        val typeEntity = type.data.toEntity()
                        val localPokemon = localDataSource.getPokemonOfType(name).first()
                        val resultPokemon =
                            mutableListOf<Pokemon>().apply {
                                addAll(localPokemon.asExternalModel())
                            }
                        val pokemonOfType =
                            typeEntity.pokemonOfType
                                .filter { name ->
                                    !localPokemon.map { it.pokemon.pokemonName }.contains(name)
                                }
                        for (pokemonName in pokemonOfType) {
                            val remotePokemon = getRemotePokemonByName(pokemonName)
                            remotePokemon?.let {
                                localDataSource.insertPokemonForType(
                                    type = typeEntity,
                                    it
                                )
                                val externalPokemon = it.asExternalModel()
                                if (!resultPokemon.contains(externalPokemon)) {
                                    resultPokemon.add(externalPokemon)
                                }
                            }
                        }
                    }
                }
            }
        val localFlow = localDataSource.getPokemonOfType(name).map { it.asExternalModel() }
        return flowOf(remoteFlow, localFlow).flattenMerge()
    }

    private suspend fun getRemotePokemonByName(name: String): PokemonWithSpeciesTypesAndVarieties? {
        return when (val pokemon = remoteDataSource.getPokemonInfoRemoteByName(name)) {
            is ApiResponse.Success -> {
                PokemonWithSpeciesTypesAndVarieties(
                    pokemon.data.pokemon.toEntity(),
                    pokemon.data.species.toEntity(pokemon.data.evolutionChain),
                    pokemon.data.types.toEntity(),
                    pokemon.data.species.varieties.toEntity()
                )
            }
            is ApiResponse.Error -> {
                Timber.d("Error fetching pokemon by name $name failed with ${pokemon.exception}")
                null
            }
        }
    }
}
