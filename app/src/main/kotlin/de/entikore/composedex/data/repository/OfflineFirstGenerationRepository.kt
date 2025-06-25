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

import de.entikore.composedex.data.local.datasource.GenerationLocalDataSource
import de.entikore.composedex.data.local.entity.generation.GenerationEntity
import de.entikore.composedex.data.local.entity.generation.asExternalModel
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.ApiResponse
import de.entikore.composedex.data.remote.RemoteDataSource
import de.entikore.composedex.data.remote.model.generation.toEntity
import de.entikore.composedex.data.remote.model.pokemon.toEntity
import de.entikore.composedex.data.remote.model.species.toEntity
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.composedex.data.util.RETRY_COUNT
import de.entikore.composedex.domain.model.generation.Generation
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.repository.GenerationRepository
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
 * Offline-first implementation of [GenerationRepository]. Tries to retrieve data from a local
 * data source, fetching it from a remote data source if necessary.
 */
class OfflineFirstGenerationRepository(
    private val localDataSource: GenerationLocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : GenerationRepository {
    override fun getGenerations(): Flow<List<Generation>> =
        localDataSource.getGenerationOverview()
            .combine(localDataSource.getAllGenerations()) { overview, generations ->
                if (generations.isNotEmpty() && generations.size == overview!!.names.size) {
                    generations.map { it.asExternalModel() }
                } else {
                    throw LocalDataException("Not all generations in database")
                }
            }.retryWhen { cause, attempt ->
                if ((cause is LocalDataException || cause is NullPointerException) && attempt < RETRY_COUNT) {
                    Timber.d("Attempt $attempt of $RETRY_COUNT to fetch generations, failed previously because: $cause")
                    val remoteGenerations = mutableListOf<GenerationEntity>()
                    when (val generations = remoteDataSource.getGenerations()) {
                        is ApiResponse.Success -> {
                            localDataSource.insertGenerationOverview(generations.data.toEntity())
                            generations.data.results.forEach {
                                when (val generation = remoteDataSource.getGenerationByName(it.name)) {
                                    is ApiResponse.Success -> {
                                        remoteGenerations.add(generation.data.toEntity())
                                    }

                                    is ApiResponse.Error -> {
                                        Timber.d("Error fetching generation ${it.name} ${generation.exception}")
                                        throw generation.exception
                                    }
                                }
                            }
                            for (generation in remoteGenerations) {
                                localDataSource.insertGeneration(generation)
                            }
                        }

                        is ApiResponse.Error -> {
                            Timber.d("Error fetching generations ${generations.exception}")
                            throw generations.exception
                        }
                    }
                    true
                } else {
                    Timber.d("Could not fetch generations: $cause")
                    false
                }
            }

    override fun getGenerationByName(name: String): Flow<Generation> {
        return localDataSource
            .getGenerationByName(name)
            .map { generationEntity -> generationEntity!!.asExternalModel() }
            .retryWhen { cause, attempt ->
                if (cause is NullPointerException && attempt < RETRY_COUNT) {
                    Timber.d(
                        "Attempt $attempt of $RETRY_COUNT to fetch generation $name, failed previously because: $cause"
                    )
                    when (val generation = remoteDataSource.getGenerationByName(name)) {
                        is ApiResponse.Error -> throw generation.exception
                        is ApiResponse.Success -> {
                            localDataSource.insertGeneration(generation.data.toEntity())
                        }
                    }
                    true
                } else {
                    Timber.d("Could not fetch generation by name $name: $cause")
                    false
                }
            }
    }

    override fun getGenerationById(id: Int): Flow<Generation> {
        return localDataSource
            .getGenerationById(id)
            .map { generationEntity -> generationEntity!!.asExternalModel() }
            .retryWhen { cause, attempt ->
                if (cause is NullPointerException && attempt < RETRY_COUNT) {
                    Timber.d(
                        "Attempt $attempt of $RETRY_COUNT to fetch generation $id, failed previously because: $cause"
                    )
                    when (val generation = remoteDataSource.getGenerationById(id)) {
                        is ApiResponse.Error -> throw generation.exception
                        is ApiResponse.Success -> {
                            localDataSource.insertGeneration(generation.data.toEntity())
                        }
                    }
                    true
                } else {
                    Timber.d("Could not fetch generation by id $id: $cause")
                    false
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPokemonOfGenerationByName(name: String): Flow<List<Pokemon>> {
        val remoteFlow =
            flow<List<Pokemon>> {
                when (val generation = remoteDataSource.getGenerationByName(name)) {
                    is ApiResponse.Error -> {
                        Timber.d("Fetch remote pokemon is ${generation.exception}")
                    }
                    is ApiResponse.Success -> {
                        val generationEntity = generation.data.toEntity()
                        val localPokemon = localDataSource.getPokemonOfGenerationByName(
                            generationEntity.generationName
                        ).first()
                        val resultPokemon =
                            mutableListOf<Pokemon>().apply {
                                addAll(localPokemon.asExternalModel())
                            }
                        val pokemonInGeneration =
                            generation.data.pokemonSpecies
                                .map { it.name }
                                .filter { name ->
                                    !localPokemon.map { it.pokemon.pokemonName }.contains(name)
                                }
                        for (pokemonName in pokemonInGeneration) {
                            val remotePokemon = getRemotePokemonByName(pokemonName)
                            remotePokemon?.let {
                                localDataSource.insertPokemonForGeneration(
                                    generation = generationEntity,
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
        val localFlow = localDataSource.getPokemonOfGenerationByName(name).map { it.asExternalModel() }
        return flowOf(remoteFlow, localFlow).flattenMerge()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPokemonOfGenerationById(id: Int): Flow<List<Pokemon>> {
        val remoteFlow =
            flow<List<Pokemon>> {
                when (val generation = remoteDataSource.getGenerationById(id)) {
                    is ApiResponse.Error -> {
                        Timber.d("Fetch remote pokemon is ${generation.exception}")
                    }
                    is ApiResponse.Success -> {
                        val generationEntity = generation.data.toEntity()
                        val localPokemon = localDataSource.getPokemonOfGenerationByName(
                            generationEntity.generationName
                        ).first()
                        val resultPokemon =
                            mutableListOf<Pokemon>().apply {
                                addAll(localPokemon.asExternalModel())
                            }
                        val pokemonInGeneration =
                            generation.data.pokemonSpecies
                                .map { it.name }
                                .filter { name ->
                                    !localPokemon.map { it.pokemon.pokemonName }.contains(name)
                                }
                        for (pokemonName in pokemonInGeneration) {
                            val remotePokemon = getRemotePokemonByName(pokemonName)
                            remotePokemon?.let {
                                localDataSource.insertPokemonForGeneration(
                                    generation = generationEntity,
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
        val localFlow = localDataSource.getPokemonOfGenerationById(id).map { it.asExternalModel() }
        return flowOf(remoteFlow, localFlow).flattenMerge()
    }

    private suspend fun getRemotePokemonByName(name: String): PokemonWithSpeciesTypesAndVarieties? {
        return when (val pokemon = remoteDataSource.getPokemonInfoRemoteBySpeciesName(name)) {
            is ApiResponse.Success -> {
                PokemonWithSpeciesTypesAndVarieties(
                    pokemon.data.pokemon.toEntity(),
                    pokemon.data.species.toEntity(pokemon.data.evolutionChain),
                    pokemon.data.types.toEntity(),
                    pokemon.data.species.varieties.map { it.toEntity() }
                )
            }
            is ApiResponse.Error -> {
                Timber.d("getPokemonInfoRemoteBySpeciesName was not successful: ${pokemon.exception}")
                null
            }
        }
    }
}
