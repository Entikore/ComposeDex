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
package de.entikore.composedex.data.remote

import de.entikore.composedex.data.remote.model.PokemonInfoRemote
import de.entikore.composedex.data.remote.model.evolution.ChainRemote
import de.entikore.composedex.data.remote.model.evolution.EvolutionChainRemote
import de.entikore.composedex.data.remote.model.generation.GenerationListRemote
import de.entikore.composedex.data.remote.model.generation.GenerationRemote
import de.entikore.composedex.data.remote.model.pokemon.PokemonRemote
import de.entikore.composedex.data.remote.model.type.TypeListRemote
import de.entikore.composedex.data.remote.model.type.TypeRemote
import de.entikore.composedex.domain.model.pokemon.ChainLink
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Retrofit backed [ComposeDexApi].
 */
class RemoteDataSource(
    private val api: ComposeDexApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ApiHandler {
    suspend fun getPokemonInfoRemoteBySpeciesName(name: String): ApiResponse<PokemonInfoRemote> =
        withContext(dispatcher) {
            return@withContext try {
                val pokemonWithSpeciesAndTypesRemote = getSpeciesWithPokemonTypeAndVarieties(name)
                ApiResponse.success(
                    getPokemonInfoRemoteByName(pokemonWithSpeciesAndTypesRemote)
                )
            } catch (exception: RemoteDataSourceException) {
                ApiResponse.error(
                    ERROR_POKEMON_NOT_FOUND.format(name),
                    exception
                )
            }
        }

    suspend fun getPokemonInfoRemoteByName(name: String): ApiResponse<PokemonInfoRemote> =
        withContext(dispatcher) {
            return@withContext try {
                val pokemonWithSpeciesAndTypesRemote = getPokemonWithSpeciesAndType(name)
                ApiResponse.success(
                    getPokemonInfoRemoteByName(pokemonWithSpeciesAndTypesRemote)
                )
            } catch (exception: RemoteDataSourceException) {
                ApiResponse.error(
                    ERROR_POKEMON_NOT_FOUND.format(name),
                    exception
                )
            }
        }

    suspend fun getPokemonInfoRemoteById(id: Int): ApiResponse<PokemonInfoRemote> =
        withContext(dispatcher) {
            return@withContext try {
                val pokemonWithSpeciesAndTypesRemote = getPokemonWithSpeciesAndType(id)
                ApiResponse.success(
                    getPokemonInfoRemoteByName(pokemonWithSpeciesAndTypesRemote)
                )
            } catch (exception: RemoteDataSourceException) {
                ApiResponse.error(ERROR_POKEMON_ID_NOT_FOUND.format(id), exception)
            }
        }

    suspend fun getPokemonTypes(): ApiResponse<TypeListRemote> = withContext(dispatcher) {
        return@withContext try {
            val typeList = handleApi { api.getPokemonTypes() }.getSuccessOrThrow()
            ApiResponse.success(typeList)
        } catch (exception: RemoteDataSourceException) {
            ApiResponse.error(ERROR_TYPES, exception)
        }
    }

    suspend fun getPokemonTypeByName(name: String): ApiResponse<TypeRemote> =
        withContext(dispatcher) {
            return@withContext try {
                val type = handleApi { api.getPokemonTypeByName(name) }.getSuccessOrThrow()
                ApiResponse.success(type)
            } catch (exception: RemoteDataSourceException) {
                ApiResponse.error(ERROR_TYPE_NOT_FOUND.format(name), exception)
            }
        }

    suspend fun getGenerations(): ApiResponse<GenerationListRemote> = withContext(dispatcher) {
        return@withContext try {
            val generationList = handleApi { api.getGenerations() }.getSuccessOrThrow()
            ApiResponse.success(generationList)
        } catch (exception: RemoteDataSourceException) {
            ApiResponse.error(ERROR_GENERATIONS, exception)
        }
    }

    suspend fun getGenerationByName(name: String): ApiResponse<GenerationRemote> =
        withContext(dispatcher) {
            return@withContext try {
                val generation = handleApi { api.getGenerationByName(name) }.getSuccessOrThrow()
                ApiResponse.success(generation)
            } catch (exception: RemoteDataSourceException) {
                ApiResponse.error(
                    ERROR_GENERATION_NOT_FOUND.format(name),
                    exception
                )
            }
        }

    suspend fun getGenerationById(id: Int): ApiResponse<GenerationRemote> =
        withContext(dispatcher) {
            return@withContext try {
                val generation =
                    handleApi { api.getGenerationById(id.toString()) }.getSuccessOrThrow()
                ApiResponse.success(generation)
            } catch (exception: RemoteDataSourceException) {
                ApiResponse.error(
                    ERROR_GENERATION_ID_NOT_FOUND.format(id),
                    exception
                )
            }
        }

    private suspend fun getPokemonWithSpeciesAndType(id: Int): PokemonInfoRemote {
        val pokemon = handleApi { api.getPokemonById(id) }.getSuccessOrThrow(
            ERROR_POKEMON_ID_NOT_FOUND.format(id)
        )
        return getSpeciesAndTypes(pokemon)
    }

    private suspend fun getSpeciesWithPokemonTypeAndVarieties(name: String): PokemonInfoRemote {
        val species = handleApi { api.getPokemonSpeciesByName(name) }.getSuccessOrThrow()
        val pokemon =
            handleApi {
                api.getPokemonByName(
                    species.varieties.first { it.isDefault }.pokemon.name
                )
            }.getSuccessOrThrow()
        val types = mutableListOf<TypeRemote>().apply {
            for (type in pokemon.types) {
                add(handleApi { api.getPokemonTypeByName(type.type.name) }.getSuccessOrThrow())
            }
        }
        return PokemonInfoRemote(pokemon, species, types)
    }

    private suspend fun getPokemonWithSpeciesAndType(
        name: String
    ): PokemonInfoRemote {
        val pokemon = handleApi { api.getPokemonByName(name) }.getSuccessOrThrow()
        return getSpeciesAndTypes(pokemon)
    }

    private suspend fun getSpeciesAndTypes(
        pokemon: PokemonRemote
    ): PokemonInfoRemote {
        val species =
            handleApi { api.getPokemonSpeciesByName(pokemon.species.name) }.getSuccessOrThrow()
        val types = mutableListOf<TypeRemote>().apply {
            for (type in pokemon.types) {
                add(handleApi { api.getPokemonTypeByName(type.type.name) }.getSuccessOrThrow())
            }
        }
        return PokemonInfoRemote(pokemon, species, types)
    }

    private suspend fun getPokemonInfoRemoteByName(
        pokemonWithSpeciesAndTypesRemote: PokemonInfoRemote
    ): PokemonInfoRemote {
        var chain: EvolutionChainRemote? = null
        pokemonWithSpeciesAndTypesRemote.species.evolutionChain?.let {
            chain = handleApi {
                api.getEvolutionChain(
                    getUrlPath(pokemonWithSpeciesAndTypesRemote.species.evolutionChain.url)
                )
            }.getSuccessOrThrow()
        }
        return pokemonWithSpeciesAndTypesRemote.copy(evolutionChain = processChain(chain = chain?.chain))
    }

    companion object {
        const val ERROR_POKEMON_NOT_FOUND = "Pokemon with name %s not found"
        const val ERROR_POKEMON_ID_NOT_FOUND = "Pokemon with id %s not found"
        const val ERROR_TYPE_NOT_FOUND = "Type %s not found"
        const val ERROR_TYPES = "Could not fetch types"
        const val ERROR_GENERATIONS = "Could not fetch generations"
        const val ERROR_GENERATION_NOT_FOUND = "Generation with name %s not found"
        const val ERROR_GENERATION_ID_NOT_FOUND = "Generation with id %s not found"

        private fun processChain(
            map: Map<Int, List<ChainLink>> = mutableMapOf(),
            chain: ChainRemote?,
            rank: Int = 0
        ): Map<Int, List<ChainLink>> {
            val chainList = map.toMutableMap()
            if (chain == null) return chainList
            val oldList = chainList[rank].orEmpty().toMutableList().apply {
                add(ChainLink(chain.species.name, getUrlPath(chain.species.url), chain.isBaby))
            }
            chainList[rank] = oldList
            for (link in chain.evolvesTo) {
                chainList.putAll(processChain(chainList, link, rank + 1))
            }
            return chainList
        }
    }
}
