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
package de.entikore.composedex.ui.screen.shared

import de.entikore.composedex.domain.model.pokemon.Pokemon

/**
 * Models state of a list of Pokemon.
 */
sealed interface PokemonUiState {
    data class Success(val pokemon: List<Pokemon>) : PokemonUiState
    data object Error : PokemonUiState
    data object Loading : PokemonUiState
}

fun PokemonUiState.stillLoading(allPokemon: Int): Boolean {
    return when (this) {
        is PokemonUiState.Success -> {
            this.pokemon.size != allPokemon
        }

        else -> true
    }
}
