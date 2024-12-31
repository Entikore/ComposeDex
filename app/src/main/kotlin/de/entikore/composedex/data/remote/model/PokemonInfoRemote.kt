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
package de.entikore.composedex.data.remote.model

import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.remote.model.pokemon.PokemonRemote
import de.entikore.composedex.data.remote.model.pokemon.toEntity
import de.entikore.composedex.data.remote.model.species.PokemonSpeciesRemote
import de.entikore.composedex.data.remote.model.species.toEntity
import de.entikore.composedex.data.remote.model.type.TypeRemote
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.composedex.domain.model.pokemon.ChainLink

/**
 * Container class for associated [PokemonRemote], [PokemonSpeciesRemote], [TypeRemote]s and
 * the evolution chain of the [PokemonRemote].
 */
data class PokemonInfoRemote(
    val pokemon: PokemonRemote,
    val species: PokemonSpeciesRemote,
    val types: List<TypeRemote>,
    var evolutionChain: Map<Int, List<ChainLink>> = emptyMap()
)

/**
 * Converts a [PokemonInfoRemote] to a [PokemonWithSpeciesTypesAndVarieties].
 */
fun PokemonInfoRemote.toEntity() = PokemonWithSpeciesTypesAndVarieties(
    pokemon = pokemon.toEntity(),
    species = species.toEntity(evolutionChain),
    types = types.map { it.toEntity() },
    varieties = species.varieties.map { it.toEntity() }
)
