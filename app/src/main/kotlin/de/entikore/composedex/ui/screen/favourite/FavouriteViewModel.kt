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
package de.entikore.composedex.ui.screen.favourite

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.entikore.composedex.domain.WorkResult
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.usecase.GetFavouritesUseCase
import de.entikore.composedex.domain.usecase.SetFavouriteData
import de.entikore.composedex.domain.usecase.base.ParamsSuspendUseCase
import de.entikore.composedex.ui.screen.shared.PokemonFilterOptions
import de.entikore.composedex.ui.screen.shared.PokemonFilterViewModel
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages application state for the [FavouriteScreen].
 */
@HiltViewModel
class FavouriteViewModel @Inject constructor(
    getFavourites: GetFavouritesUseCase,
    private val setAsFavouriteUseCase: @JvmSuppressWildcards ParamsSuspendUseCase<SetFavouriteData, Unit>
) : PokemonFilterViewModel() {

    val screenState =
        getFavourites().combine(
            filterOptions
        ) { favourites: WorkResult<List<Pokemon>>, filterSettings: PokemonFilterOptions ->
            when (favourites) {
                is WorkResult.Error -> PokemonUiState.Error
                WorkResult.Loading -> PokemonUiState.Loading
                is WorkResult.Success -> PokemonUiState.Success(
                    filterSettings.getFilteredList(
                        favourites.data
                    )
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            PokemonUiState.Success(emptyList())
        )

    fun updateFavourite(id: Int, isFavourite: Boolean) {
        Timber.d("Updating favourite for $id to $isFavourite")
        viewModelScope.launch { setAsFavouriteUseCase(SetFavouriteData(id, isFavourite)) }
    }
}
