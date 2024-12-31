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
package de.entikore.composedex.domain.model.pokemon

import de.entikore.composedex.domain.model.type.Type

/**
 * A [Pokemon] may have different [Variety]s which makes it differ from other Pokémon of the same
 * species, such as base stats, available abilities and typings. A Pokémon Species forms the basis
 * for at least one Pokémon. Attributes of a Pokémon species are shared across all varieties of
 * Pokémon within the species. A good example is Wormadam; Wormadam is the species which can be
 * found in three different varieties, Wormadam-Trash, Wormadam-Sandy and Wormadam-Plant.
 */
data class Pokemon(
    val id: Int,
    val name: String,
    val defaultName: String,
    val height: String,
    val weight: String,
    val types: List<Type>,
    val stats: Map<String, Int>,
    val pokemonLabel: PokemonLabels,
    val textEntries: List<String>,
    val evolutionRank: EvolutionRank,
    val evolvesFrom: String,
    val evolutionChain: Map<Int, List<ChainLink>>,
    val genera: String,
    val varieties: List<Variety>,
    val remoteArtwork: String,
    val artwork: String?,
    val remoteSprite: String,
    val sprite: String?,
    val remoteCry: String?,
    val cry: String?,
    val isFavourite: Boolean,
    val shape: PokemonShape
) {
    fun isDefault() = name == defaultName
}

/**
 * The evolution rank of a [Pokemon], based on the TCG. A Pokemon without any evolutions is [BASIC].
 */
enum class EvolutionRank(val uiString: String) {
    BABY("Baby"),
    BASIC("Basic"),
    STAGE1("Stage 1"),
    STAGE2("Stage 2")
}

fun Pokemon.wasPokemonUpdated(new: Pokemon): Boolean {
    return !(
        this.isFavourite != new.isFavourite ||
            this.artwork != new.artwork ||
            this.cry != new.cry ||
            this.sprite != new.sprite
        )
}
