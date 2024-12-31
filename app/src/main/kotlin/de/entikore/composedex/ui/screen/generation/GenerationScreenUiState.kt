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
package de.entikore.composedex.ui.screen.generation

import de.entikore.composedex.domain.model.generation.Generation
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.ui.screen.shared.PokemonUiState

/**
 * Models state of the generations screen.
 */
sealed interface GenerationScreenUiState {

    data class Success(
        val generations: List<Generation> = emptyList(),
        val selectedGeneration: SelectedGenerationUiState = SelectedGenerationUiState.NoGenerationSelected
    ) : GenerationScreenUiState
    data object Error : GenerationScreenUiState
    data object Loading : GenerationScreenUiState
}

/**
 * Models state of the selected generation screen.
 */
sealed interface SelectedGenerationUiState {
    data object NoGenerationSelected : SelectedGenerationUiState
    data object Loading : SelectedGenerationUiState
    data object Error : SelectedGenerationUiState

    data class Success(
        val selectedGeneration: Generation,
        val pokemonState: PokemonUiState = PokemonUiState.Loading,
        val showLoadingItem: Boolean = false
    ) : SelectedGenerationUiState
}

fun GenerationScreenUiState.getPokemonList(): List<Pokemon>? {
    return when (this) {
        is GenerationScreenUiState.Success -> {
            when (this.selectedGeneration) {
                is SelectedGenerationUiState.Success -> {
                    when (val pokemonUiState = this.selectedGeneration.pokemonState) {
                        is PokemonUiState.Success -> {
                            pokemonUiState.pokemon
                        }

                        else -> null
                    }
                }

                else -> null
            }
        }

        else -> null
    }
}

fun GenerationScreenUiState.withFilteredPokemonList(filteredList: List<Pokemon>?): GenerationScreenUiState {
    if (filteredList == null || this !is GenerationScreenUiState.Success ||
        this.selectedGeneration !is SelectedGenerationUiState.Success ||
        this.selectedGeneration.pokemonState !is PokemonUiState.Success
    ) {
        return this
    }

    return this.copy(
        selectedGeneration = this.selectedGeneration.copy(
            pokemonState = this.selectedGeneration.pokemonState.copy(filteredList)
        )
    )
}
