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
package de.entikore.composedex.ui.screen.type

import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.ui.screen.shared.PokemonUiState

/**
 * Models state of the type overview screen.
 */
sealed interface TypeScreenUiState {

    data class Success(
        val types: List<Type> = emptyList(),
        val selectedType: SelectedTypeUiState = SelectedTypeUiState.NoTypeSelected
    ) : TypeScreenUiState

    data object Error : TypeScreenUiState

    data object Loading : TypeScreenUiState
}

/**
 * Models state of the selected type screen.
 */
sealed interface SelectedTypeUiState {
    data object NoTypeSelected : SelectedTypeUiState
    data object Loading : SelectedTypeUiState
    data object Error : SelectedTypeUiState

    data class Success(
        val selectedType: Type,
        val pokemonState: PokemonUiState = PokemonUiState.Loading,
        val showLoadingItem: Boolean = false
    ) : SelectedTypeUiState
}

fun TypeScreenUiState.getPokemonList(): List<Pokemon>? {
    return when (this) {
        is TypeScreenUiState.Success -> {
            when (this.selectedType) {
                is SelectedTypeUiState.Success -> {
                    when (val pokemonUiState = this.selectedType.pokemonState) {
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

fun TypeScreenUiState.withFilteredPokemonList(filteredList: List<Pokemon>?): TypeScreenUiState {
    if (filteredList == null || this !is TypeScreenUiState.Success ||
        this.selectedType !is SelectedTypeUiState.Success ||
        this.selectedType.pokemonState !is PokemonUiState.Success
    ) {
        return this
    }

    return this.copy(
        selectedType = this.selectedType.copy(
            pokemonState = this.selectedType.pokemonState.copy(filteredList)
        )
    )
}
