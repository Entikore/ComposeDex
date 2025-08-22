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
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.domain.usecase.FetchPokemonOfTypeUseCase
import de.entikore.composedex.domain.usecase.FetchTypeUseCase
import de.entikore.composedex.domain.usecase.FetchTypesUseCase
import de.entikore.composedex.domain.usecase.SaveImageData
import de.entikore.composedex.domain.usecase.SetFavouriteData
import de.entikore.composedex.domain.usecase.base.BaseSuspendUseCase
import de.entikore.composedex.ui.screen.shared.PokemonFilterOptions
import de.entikore.composedex.ui.screen.shared.PokemonFilterViewModel
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.composedex.ui.screen.shared.stillLoading
import de.entikore.composedex.ui.screen.type.TypeScreenUiState.Success
import de.entikore.composedex.ui.screen.util.SUFFIX_SPRITE
import de.entikore.composedex.ui.screen.util.retrieveAsset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages application state for the [TypeScreen].
 */
@HiltViewModel
class TypeViewModel @Inject constructor(
    getTypesUseCase: FetchTypesUseCase,
    private val getTypeUseCase: FetchTypeUseCase,
    private val getPokemonOfTypeUseCase: FetchPokemonOfTypeUseCase,
    private val saveRemoteImageUseCase: @JvmSuppressWildcards BaseSuspendUseCase<SaveImageData, String>,
    private val setAsFavouriteUseCase: @JvmSuppressWildcards BaseSuspendUseCase<SetFavouriteData, Unit>
) : PokemonFilterViewModel() {

    private val _selectedTypeFlow = MutableStateFlow("")
    val selectedType: StateFlow<String> =
        _selectedTypeFlow.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchSelectedTypeDetailsFlow(type: String): Flow<SelectedTypeUiState> {
        return getTypeUseCase(type).combine(
            getPokemonOfTypeUseCase(type),
            ::buildSelectedTypeUiState
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState: StateFlow<TypeScreenUiState> =
        combine(
            getTypesUseCase(),
            _selectedTypeFlow.flatMapLatest { selectedType ->
                if (selectedType.isNotEmpty()) {
                    fetchSelectedTypeDetailsFlow(selectedType)
                } else {
                    flowOf(SelectedTypeUiState.NoTypeSelected)
                }
            },
            ::buildTypeScreenUiState
        ).combine(filterOptions) { uiState: TypeScreenUiState, filterSettings: PokemonFilterOptions ->
            val currentPokemonList = uiState.getPokemonList()
            val filteredList = currentPokemonList?.let { filterSettings.getFilteredList(it) }
            uiState.withFilteredPokemonList(filteredList)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TypeScreenUiState.Loading
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
        type: Result<Type>,
        pokemon: Result<List<Pokemon>>
    ): SelectedTypeUiState {
        return when {
            type.isFailure -> SelectedTypeUiState.Error
            type.isSuccess -> {
                val pokemonUiState = when {
                    pokemon.isFailure -> PokemonUiState.Error
                    pokemon.isSuccess -> {
                        PokemonUiState.Success(
                            pokemon.getOrThrow().sortedBy { it.id }.also { pokemonList ->
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
                                                saveRemoteImageUseCase(
                                                    SaveImageData(
                                                        id,
                                                        url,
                                                        fileName,
                                                        true
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }

                    else -> PokemonUiState.Loading
                }
                SelectedTypeUiState.Success(
                    selectedType = type.getOrThrow(),
                    pokemonState = pokemonUiState,
                    showLoadingItem = pokemonUiState.stillLoading(type.getOrThrow().pokemonOfType.size)
                )
            }

            else -> SelectedTypeUiState.Loading
        }
    }

    private fun buildTypeScreenUiState(
        types: Result<List<Type>>,
        selectedTypeUiState: SelectedTypeUiState
    ): TypeScreenUiState {
        return when {
            types.isFailure -> {
                TypeScreenUiState.Error
            }

            types.isSuccess -> {
                val success = Success(
                    types = types.getOrThrow(),
                    selectedType = selectedTypeUiState
                )
                success
            }

            else -> {
                TypeScreenUiState.Loading
            }
        }
    }
}
