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
package de.entikore.composedex.ui.screen.pokemon

import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.type.Type

/**
 * Models state of the Pokemon screen.
 */
sealed class PokemonScreenState {
    data object NoPokemonSelected : PokemonScreenState()
    data class Success(
        val selectedPokemon: Pokemon,
        val displayedEvolution: String = "",
        val selectedType: Type = selectedPokemon.types.first(),
        val evolvesFrom: PokemonPreview? = null,
        val evolvesTo: List<PokemonPreview> = emptyList(),
        val varieties: List<Pokemon> = emptyList()
    ) : PokemonScreenState()
    data class Error(val errorMessage: String) : PokemonScreenState()
    data object Loading : PokemonScreenState()
}

/**
 * A shorter representation of a [Pokemon], since not all information is necessary for preview.
 */
data class PokemonPreview(
    val name: String,
    val sprite: String = "",
    val types: List<Type> = emptyList(),
    val url: String = "",
    val evolutionText: String = "",
    val isLoading: Boolean = false
)

/**
 * Converts a [Pokemon] to a [PokemonPreview].
 */
fun Pokemon.toPokemonPreview(evolvesTo: Boolean) = PokemonPreview(
    name = name,
    sprite = "",
    types = types,
    url = remoteSprite,
    evolutionText = if (evolvesTo) {
        "Evolves to ${name.replaceFirstChar { char -> char.uppercaseChar() }}"
    } else {
        "Evolves from ${name.replaceFirstChar { char -> char.uppercaseChar() }}"
    },
    isLoading = true
)
