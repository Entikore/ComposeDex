/*
 * Copyright 2025 Entikore
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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.entikore.composedex.domain.WorkResult
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.domain.usecase.GetPokemonOfTypeUseCase
import de.entikore.composedex.domain.usecase.GetTypeUseCase
import de.entikore.composedex.domain.usecase.GetTypesUseCase
import de.entikore.composedex.domain.usecase.SaveImageData
import de.entikore.composedex.domain.usecase.SetFavouriteData
import de.entikore.composedex.domain.usecase.base.ParamsSuspendUseCase
import de.entikore.composedex.ui.screen.shared.PokemonFilterOptions
import de.entikore.composedex.ui.screen.shared.PokemonFilterViewModel
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.composedex.ui.screen.shared.stillLoading
import de.entikore.composedex.ui.screen.type.TypeScreenUiState.Success
import de.entikore.composedex.ui.screen.util.SUFFIX_SPRITE
import de.entikore.composedex.ui.screen.util.retrieveAsset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages application state for the [TypeScreen].
 */
@HiltViewModel
class TypeViewModel @Inject constructor(
    getTypesUseCase: GetTypesUseCase,
    getTypeUseCase: GetTypeUseCase,
    getPokemonOfTypeUseCase: GetPokemonOfTypeUseCase,
    private val saveRemoteImageUseCase: @JvmSuppressWildcards ParamsSuspendUseCase<SaveImageData, String>,
    private val setAsFavouriteUseCase: @JvmSuppressWildcards ParamsSuspendUseCase<SetFavouriteData, Unit>
) : PokemonFilterViewModel() {

    private val _selectedTypeFlow = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String> = _selectedTypeFlow.map { it ?: "" }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ""
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState =
        combine(
            getTypesUseCase(),
            _selectedTypeFlow.flatMapLatest { selectedType ->
                selectedType?.let {
                    getTypeUseCase(it).combine(
                        getPokemonOfTypeUseCase(it),
                        ::buildSelectedTypeUiState
                    )
                } ?: flowOf(SelectedTypeUiState.NoTypeSelected)
            },
            ::buildTypeScreenUiState
        ).combine(filterOptions) { uiState: TypeScreenUiState, filterSettings: PokemonFilterOptions ->
            uiState.withFilteredPokemonList(
                uiState.getPokemonList()?.let {
                    filterSettings.getFilteredList(it)
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            Success()
        )

    fun fetchType(typeName: String) {
        Timber.d("Search for type $typeName")
        _selectedTypeFlow.value = typeName
    }

    fun updateFavourite(id: Int, isFavourite: Boolean) {
        Timber.d("Update favourite status for $id to $isFavourite")
        viewModelScope.launch { setAsFavouriteUseCase(SetFavouriteData(id, isFavourite)) }
    }

    private fun buildSelectedTypeUiState(
        type: WorkResult<Type>,
        pokemon: WorkResult<List<Pokemon>>
    ): SelectedTypeUiState {
        return when (type) {
            WorkResult.Loading -> SelectedTypeUiState.Loading
            is WorkResult.Error -> SelectedTypeUiState.Error
            is WorkResult.Success -> {
                val pokemonUiState = when (pokemon) {
                    is WorkResult.Error -> PokemonUiState.Error
                    WorkResult.Loading -> PokemonUiState.Loading
                    is WorkResult.Success -> {
                        PokemonUiState.Success(
                            pokemon.data.sortedBy { it.id }.also { pokemonList ->
                                viewModelScope.launch {
                                    pokemonList.forEach {
                                        retrieveAsset(
                                            it.id,
                                            buildString {
                                                append(it.name)
                                                append(SUFFIX_SPRITE)
                                            },
                                            it.sprite,
                                            it.remoteSprite,
                                            saveAssetUseCase = { id, url, fileName ->
                                                saveRemoteImageUseCase(SaveImageData(id, url, fileName, true))
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
                SelectedTypeUiState.Success(
                    selectedType = type.data,
                    pokemonState = pokemonUiState,
                    showLoadingItem = pokemonUiState.stillLoading(type.data.pokemonOfType.size)
                )
            }
        }
    }

    private fun buildTypeScreenUiState(
        types: WorkResult<List<Type>>,
        selectedTypeUiState: SelectedTypeUiState
    ): TypeScreenUiState {
        return when (types) {
            is WorkResult.Error -> TypeScreenUiState.Error
            WorkResult.Loading -> TypeScreenUiState.Loading
            is WorkResult.Success -> {
                Success(
                    types = types.data,
                    selectedType = selectedTypeUiState
                )
            }
        }
    }
}
