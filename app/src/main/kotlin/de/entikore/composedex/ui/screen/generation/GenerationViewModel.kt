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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.entikore.composedex.domain.WorkResult
import de.entikore.composedex.domain.model.generation.Generation
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.usecase.GetGenerationUseCase
import de.entikore.composedex.domain.usecase.GetGenerationsUseCase
import de.entikore.composedex.domain.usecase.GetPokemonOfGenerationUseCase
import de.entikore.composedex.domain.usecase.SaveImageData
import de.entikore.composedex.domain.usecase.SetFavouriteData
import de.entikore.composedex.domain.usecase.base.ParamsSuspendUseCase
import de.entikore.composedex.ui.screen.shared.PokemonFilterOptions
import de.entikore.composedex.ui.screen.shared.PokemonFilterViewModel
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.composedex.ui.screen.shared.stillLoading
import de.entikore.composedex.ui.screen.util.SUFFIX_SPRITE
import de.entikore.composedex.ui.screen.util.retrieveAsset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages application state for the [GenerationScreen].
 */
@HiltViewModel
class GenerationViewModel @Inject constructor(
    getGenerationsUseCase: GetGenerationsUseCase,
    getGenerationUseCase: GetGenerationUseCase,
    getPokemonOfGenerationUseCase: GetPokemonOfGenerationUseCase,
    private val saveRemoteImageUseCase: @JvmSuppressWildcards ParamsSuspendUseCase<SaveImageData, String>,
    private val setAsFavouriteUseCase: @JvmSuppressWildcards ParamsSuspendUseCase<SetFavouriteData, Unit>
) : PokemonFilterViewModel() {

    private val _selectedGenerationFlow = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState = combine(
        getGenerationsUseCase(),
        _selectedGenerationFlow.flatMapLatest { selectedGeneration ->
            selectedGeneration?.let {
                getGenerationUseCase(it).combine(
                    getPokemonOfGenerationUseCase(it),
                    ::buildSelectedGenerationUiState
                )
            } ?: flowOf(SelectedGenerationUiState.NoGenerationSelected)
        },
        ::buildGenerationScreenUiState
    ).combine(filterOptions) { uiState: GenerationScreenUiState, filterSettings: PokemonFilterOptions ->
        uiState.withFilteredPokemonList(
            uiState.getPokemonList()?.let {
                filterSettings.getFilteredList(it)
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GenerationScreenUiState.Success()
    )

    fun searchForGeneration(generationId: String) {
        Timber.d("Search for generation $generationId")
        _selectedGenerationFlow.value = generationId
    }

    fun updateFavourite(id: Int, isFavourite: Boolean) {
        Timber.d("Update favourite for $id to $isFavourite")
        viewModelScope.launch { setAsFavouriteUseCase(SetFavouriteData(id, isFavourite)) }
    }

    private fun buildSelectedGenerationUiState(
        generation: WorkResult<Generation>,
        pokemon: WorkResult<List<Pokemon>>
    ): SelectedGenerationUiState {
        return when (generation) {
            WorkResult.Loading -> SelectedGenerationUiState.Loading
            is WorkResult.Error -> SelectedGenerationUiState.Error
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
                SelectedGenerationUiState.Success(
                    selectedGeneration = generation.data,
                    pokemonState = pokemonUiState,
                    showLoadingItem = pokemonUiState.stillLoading(generation.data.numberOfPokemon)
                )
            }
        }
    }

    private fun buildGenerationScreenUiState(
        generations: WorkResult<List<Generation>>,
        selectedGenerationUiState: SelectedGenerationUiState
    ): GenerationScreenUiState {
        return when (generations) {
            is WorkResult.Error -> GenerationScreenUiState.Error
            WorkResult.Loading -> GenerationScreenUiState.Loading
            is WorkResult.Success -> {
                GenerationScreenUiState.Success(
                    generations = generations.data,
                    selectedGeneration = selectedGenerationUiState
                )
            }
        }
    }
}
