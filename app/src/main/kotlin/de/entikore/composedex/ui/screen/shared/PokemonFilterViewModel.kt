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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.pokemon.PokemonShape
import de.entikore.composedex.ui.component.FilterOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber

/**
 * Base ViewModel for all View Models that manage a list of Pokemon to provide filter options.
 */
abstract class PokemonFilterViewModel : ViewModel() {

    private val _filterOptions =
        MutableStateFlow(PokemonFilterOptions())

    val filterOptions: StateFlow<PokemonFilterOptions> =
        _filterOptions.stateIn(viewModelScope, SharingStarted.Eagerly, _filterOptions.value)

    fun onNameFilterChange(text: String) {
        Timber.d("Name filter changed to $text")
        _filterOptions.update { oldValue ->
            oldValue.copy(nameFilter = text)
        }
    }

    fun onCheckBoxFilterChange(option: String, newValue: Boolean) {
        Timber.d("Checkbox filter changed to $newValue for $option")
        _filterOptions.update { oldValue ->
            oldValue.copy(checkBoxFilter = oldValue.checkBoxFilter.update(option, newValue))
        }
    }

    fun onShapeFilterChange(shape: PokemonShape) {
        Timber.d("Shape filter changed to $shape")
        _filterOptions.update { oldValue ->
            oldValue.copy(
                shapeFilter = oldValue.shapeFilter.copy(
                    second = shape
                )
            )
        }
    }
}

/**
 * Data class representing options for filtering a list of Pokémon.
 *
 * This class encapsulates various filtering criteria that can be applied
 * to a list of Pokémon to obtain a filtered subset.
 *
 * @property nameFilter     A string used to filter Pokémon by name.
 * @property checkBoxFilter An instance of `FilterOptions` containing boolean properties for
 *                          filtering by categories.
 * @property shapeFilter    A `Pair` where the first element is a list of all available Pokémon
 *                          shapes, and the second element is the selected shape for filtering.
 */
data class PokemonFilterOptions(
    val nameFilter: String = "",
    val checkBoxFilter: FilterOptions = FilterOptions(),
    val shapeFilter: Pair<List<PokemonShape>, PokemonShape> = Pair(
        PokemonShape.getShapes(),
        PokemonShape.UNDEFINED
    ),
) {
    fun getFilteredList(pokemon: List<Pokemon>): List<Pokemon> {
        return pokemon
            .asSequence()
            .filter {
                if (nameFilter.isNotBlank()) {
                    it.name.contains(nameFilter, ignoreCase = true)
                } else {
                    true
                }
            }
            .filter { if (checkBoxFilter.baby.second) it.pokemonLabel.baby.second else true }
            .filter { if (checkBoxFilter.legendary.second) it.pokemonLabel.legendary.second else true }
            .filter { if (checkBoxFilter.mystical.second) it.pokemonLabel.mystical.second else true }
            .filter { if (checkBoxFilter.favourite.second) it.isFavourite else true }
            .filter { if (shapeFilter.second != PokemonShape.UNDEFINED) it.shape == shapeFilter.second else true }
            .toList()
    }
}
