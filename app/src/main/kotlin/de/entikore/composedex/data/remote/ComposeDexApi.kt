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

import de.entikore.composedex.data.remote.model.evolution.EvolutionChainRemote
import de.entikore.composedex.data.remote.model.generation.GenerationListRemote
import de.entikore.composedex.data.remote.model.generation.GenerationRemote
import de.entikore.composedex.data.remote.model.pokemon.PokemonRemote
import de.entikore.composedex.data.remote.model.species.PokemonSpeciesRemote
import de.entikore.composedex.data.remote.model.type.TypeListRemote
import de.entikore.composedex.data.remote.model.type.TypeRemote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API declaration for PokeApi.
 */
interface ComposeDexApi {

    @GET("pokemon/{id}/")
    suspend fun getPokemonById(@Path("id") id: Int): Response<PokemonRemote>

    @GET("pokemon/{name}/")
    suspend fun getPokemonByName(@Path("name") name: String): Response<PokemonRemote>

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpeciesByName(@Path("name") name: String): Response<PokemonSpeciesRemote>

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChain(@Path("id") id: String): Response<EvolutionChainRemote>

    @GET("type/")
    suspend fun getPokemonTypes(): Response<TypeListRemote>

    @GET("type/{name}")
    suspend fun getPokemonTypeByName(@Path("name") name: String): Response<TypeRemote>

    @GET("generation/")
    suspend fun getGenerations(): Response<GenerationListRemote>

    @GET("generation/{name}")
    suspend fun getGenerationByName(@Path("name") name: String): Response<GenerationRemote>

    @GET("generation/{id}")
    suspend fun getGenerationById(@Path("id") id: String): Response<GenerationRemote>

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
}
