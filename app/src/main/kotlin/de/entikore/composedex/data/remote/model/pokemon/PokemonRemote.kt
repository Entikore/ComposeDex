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
package de.entikore.composedex.data.remote.model.pokemon

import com.squareup.moshi.Json
import de.entikore.composedex.data.local.entity.pokemon.PokemonEntity
import de.entikore.composedex.data.remote.model.common.NamedApiResource
import de.entikore.composedex.data.remote.model.pokemon.sprites.SpritesRemote

/**
 * Pokémon are the creatures that inhabit the world of the Pokémon games. They can be caught using
 * Pokéballs and trained by battling with other Pokémon. Each Pokémon belongs to a specific species
 * but may take on a variant which makes it differ from other Pokémon of the same species, such as
 * base stats, available abilities and typings.
 *
 * @property abilities A list of abilities this Pokémon could potentially have.
 * @property baseExperience The base experience gained for defeating this Pokémon.
 * @property cries A set of cries used to depict this Pokémon in the game. A visual representation of
 * the various cries can be found at PokeAPI/cries
 * @property forms A list of resources defining the form of the Pokémon
 * @property height The height of this Pokémon in decimetres.
 * @property id The identifier for this Pokémon.
 * @property isDefault Whether or not this is the default variety of a Pokémon species.
 * @property locationAreaEncounters The locations the Pokémon can be encountered in.
 * @property name The name of this Pokémon.
 * @property order Order for sorting. Almost national order, except families are grouped together.
 * @property species The species resource this Pokémon belongs to
 * @property sprites A set of sprites used to depict this Pokémon in the game.
 * @property stats A list of base stat values for this Pokémon.
 * @property types A list of details showing types this Pokémon has.
 * @property weight The weight of this Pokémon in hectograms.
 */
data class PokemonRemote(
    val abilities: List<AbilityRemote>,
    @Json(name = "base_experience") val baseExperience: Int?,
    val cries: CryRemote,
    val forms: List<NamedApiResource>,
    val height: Int,
    val id: Int,
    @Json(name = "is_default") val isDefault: Boolean,
    @Json(name = "location_area_encounters") val locationAreaEncounters: String,
    val name: String,
    val order: Int,
    val species: NamedApiResource,
    val sprites: SpritesRemote,
    val stats: List<StatRemote>,
    val types: List<PokemonTypeRemote>,
    val weight: Int
)

/**
 * Converts a [PokemonRemote] to a [PokemonEntity].
 */
fun PokemonRemote.toEntity() = PokemonEntity(
    pokemonId = id,
    pokemonName = name,
    cry = cries.latest ?: cries.legacy,
    localCry = null,
    weight = weight,
    height = height,
    stats = stats.sortedBy { it.stat.url }.associate { it.stat.name to it.baseStat },
    artwork = sprites.other.officialArtwork.frontDefault.orEmpty(),
    sprite = sprites.frontDefault.orEmpty(),
    localArtwork = null,
    localSprite = null,
    isFavourite = false
)
