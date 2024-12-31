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
package de.entikore.composedex.data.remote.model.generation

import com.squareup.moshi.Json
import de.entikore.composedex.data.local.entity.generation.GenerationEntity
import de.entikore.composedex.data.remote.model.common.NamedApiResource

/**
 * A generation is a grouping of the Pokémon games that separates them based on the Pokémon they
 * include. In each generation, a new set of Pokémon, Moves, Abilities and Types that did not exist
 * in the previous generation are released.
 *
 * @property id The identifier for this resource.
 * @property mainRegion The main region travelled in this generation.
 * @property name The name for this resource.
 * @property pokemonSpecies A list of Pokémon species that were introduced in this generation.
 */
data class GenerationRemote(
    val id: Int,
    @Json(name = "main_region") val mainRegion: NamedApiResource,
    val name: String,
    @Json(name = "pokemon_species") val pokemonSpecies: List<NamedApiResource>
)

/**
 * Converts a [GenerationRemote] to a [GenerationEntity].
 */
fun GenerationRemote.toEntity() =
    GenerationEntity(
        generationId = id,
        generationName = name,
        pokemonInGeneration = pokemonSpecies.map { it.name }
    )
