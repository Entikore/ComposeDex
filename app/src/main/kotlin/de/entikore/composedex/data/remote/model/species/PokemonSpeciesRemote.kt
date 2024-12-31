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
package de.entikore.composedex.data.remote.model.species

import com.squareup.moshi.Json
import de.entikore.composedex.data.local.entity.species.SpeciesEntity
import de.entikore.composedex.data.remote.model.common.ApiResource
import de.entikore.composedex.data.remote.model.common.Name
import de.entikore.composedex.data.remote.model.common.NamedApiResource
import de.entikore.composedex.data.remote.model.pokemon.PokemonRemote
import de.entikore.composedex.domain.model.pokemon.ChainLink

/**
 * A Pokémon Species forms the basis for at least one [PokemonRemote]. Attributes of a Pokémon
 * species are shared across all [VarietyRemote] of Pokémon within the species. A good example is
 * Wormadam; Wormadam is the species which can be found in three different varieties,
 * Wormadam-Trash, Wormadam-Sandy and Wormadam-Plant.
 */
data class PokemonSpeciesRemote(
    val color: NamedApiResource,
    @Json(name = "evolution_chain") val evolutionChain: ApiResource?,
    @Json(name = "evolves_from_species") val evolvesFromSpecies: NamedApiResource?,
    @Json(name = "flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryRemote>,
    val genera: List<GeneraRemote>,
    val generation: NamedApiResource,
    val habitat: NamedApiResource?,
    @Json(name = "has_gender_differences") val hasGenderDifferences: Boolean,
    val id: Int,
    @Json(name = "is_baby") val isBaby: Boolean,
    @Json(name = "is_legendary") val isLegendary: Boolean,
    @Json(name = "is_mythical") val isMythical: Boolean,
    val name: String,
    val names: List<Name>,
    val order: Int,
    val shape: NamedApiResource?,
    val varieties: List<VarietyRemote>
)

/**
 * Converts a [PokemonSpeciesRemote] to a [SpeciesEntity].
 */
fun PokemonSpeciesRemote.toEntity(evolutionChain: Map<Int, List<ChainLink>> = emptyMap()) =
    SpeciesEntity(
        speciesId = id,
        name = name,
        isBaby = isBaby,
        isLegendary = isLegendary,
        isMythical = isMythical,
        flavorTextEntries =
        flavorTextEntries.filter { it.language.name == "en" }.map { it.flavorText },
        evolvesFrom = evolvesFromSpecies?.name.orEmpty(),
        evolutionChain = evolutionChain,
        genera = checkGeneraExistence(genera),
        shape = shape?.name.orEmpty()
    )

// not every PokemonSpeciesRemote has a genera specified
private fun checkGeneraExistence(genera: List<GeneraRemote>): String {
    val filtered = genera.filter { it.language.name == "en" }.map { it.genus }
    return if (filtered.isNotEmpty()) filtered.first() else ""
}
