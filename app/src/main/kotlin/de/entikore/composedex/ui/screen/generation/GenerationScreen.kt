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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.generation.Generation
import de.entikore.composedex.domain.model.pokemon.PokemonShape
import de.entikore.composedex.ui.component.ClickableColumnItem
import de.entikore.composedex.ui.component.ErrorMessage
import de.entikore.composedex.ui.component.LoadingAnimation
import de.entikore.composedex.ui.component.PokemonColumnItem
import de.entikore.composedex.ui.component.TopBarWithSearchbarAndFilter
import de.entikore.composedex.ui.component.asCheckBoxItems
import de.entikore.composedex.ui.component.createGradientBrush
import de.entikore.composedex.ui.component.getTypeBackgroundColor
import de.entikore.composedex.ui.component.getTypeBorderColor
import de.entikore.composedex.ui.component.getTypePrimaryColor
import de.entikore.composedex.ui.screen.shared.PokemonFilterOptions
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_BORDER

/**
 * The top level composable for the Generation screen.
 */
@Composable
fun GenerationScreen(
    openDrawer: () -> Unit,
    navigateToPokemon: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GenerationViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val searchGeneration = viewModel::searchForGeneration
    val onNameFilterChange = viewModel::onNameFilterChange
    val onCheckBoxFilterChange = viewModel::onCheckBoxFilterChange
    val onShapeFilterChange = viewModel::onShapeFilterChange
    val updateFavourite = viewModel::updateFavourite

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        GenerationScreenTopBar(
            filterOptions,
            openDrawer,
            searchGeneration,
            onNameFilterChange,
            onCheckBoxFilterChange,
            onShapeFilterChange
        )
        GenerationScreenContent(
            screenState,
            searchGeneration,
            updateFavourite,
            navigateToPokemon,
        )
    }
}

@Composable
private fun GenerationScreenTopBar(
    filterOptions: PokemonFilterOptions,
    openDrawer: () -> Unit,
    searchGeneration: (generationId: String) -> Unit,
    onNameFilterChange: (text: String) -> Unit,
    onCheckBoxFilterChange: (option: String, newValue: Boolean) -> Unit,
    onShapeFilterChange: (shape: PokemonShape) -> Unit,
    modifier: Modifier = Modifier
) {
    TopBarWithSearchbarAndFilter(
        title = stringResource(R.string.generation_screen_top_bar),
        openDrawer = openDrawer,
        searchOnClick = searchGeneration,
        searchText = filterOptions.nameFilter,
        changeSearchText = onNameFilterChange,
        checkBoxItems = filterOptions.checkBoxFilter.asCheckBoxItems(),
        changeFilter = onCheckBoxFilterChange,
        shapeFilter = filterOptions.shapeFilter,
        changeShapeFilter = onShapeFilterChange,
        modifier = modifier
    )
}

@Composable
fun GenerationScreenContent(
    screenState: GenerationScreenUiState,
    searchGeneration: (String) -> Unit,
    updateFavourite: (id: Int, isFavourite: Boolean) -> Unit,
    navigateToPokemon: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        when (screenState) {
            GenerationScreenUiState.Error -> ErrorMessage(
                errorMessage = stringResource(R.string.error_fetching_generations),
                modifier = Modifier.testTag(
                    stringResource(R.string.test_tag_generation_screen_error)
                )
            )

            GenerationScreenUiState.Loading -> LoadingAnimation(
                modifier = Modifier.testTag(
                    stringResource(R.string.test_tag_generation_screen_loading)
                )
            )

            is GenerationScreenUiState.Success -> {
                when (screenState.selectedGeneration) {
                    SelectedGenerationUiState.NoGenerationSelected -> {
                        GenerationOverview(
                            screenState.generations,
                            searchGeneration,
                            Modifier.testTag(
                                stringResource(R.string.test_tag_generation_screen_overview)
                            )
                        )
                    }

                    else -> {
                        GenerationDetail(
                            screenState.selectedGeneration,
                            navigateToPokemon,
                            updateFavourite,
                            Modifier.testTag(stringResource(R.string.test_tag_generation_screen_detail))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerationOverview(
    generations: List<Generation>,
    searchGeneration: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxSize()
    ) {
        items(generations) { generation ->
            ClickableColumnItem(
                text = generation.name.replaceFirstChar { it.uppercaseChar() },
                backgroundColor = TYPE_TCG_COLORLESS_BACKGROUND,
                borderColor = TYPE_TCG_COLORLESS_BORDER,
                textColor = TYPE_TCG_COLORLESS_BORDER,
                onClick = searchGeneration,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.medium_padding),
                    vertical = dimensionResource(id = R.dimen.small_padding)
                )
            )
        }
    }
}

@Composable
private fun GenerationDetail(
    selectedGenerationUiState: SelectedGenerationUiState,
    navigateToPokemon: (String) -> Unit,
    updateFavourite: (id: Int, isFavourite: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        when (selectedGenerationUiState) {
            SelectedGenerationUiState.Loading -> LoadingAnimation(
                Modifier.testTag(stringResource(R.string.test_tag_generation_detail_loading))
            )

            is SelectedGenerationUiState.Success -> {
                GenerationDetailSuccess(
                    selectedGenerationUiState.selectedGeneration,
                    selectedGenerationUiState.pokemonState,
                    selectedGenerationUiState.showLoadingItem,
                    navigateToPokemon,
                    updateFavourite,
                    Modifier.testTag(stringResource(R.string.test_tag_generation_detail_success))
                )
            }

            else -> ErrorMessage(errorMessage = stringResource(R.string.error_fetching_generation))
        }
    }
}

@Composable
private fun GenerationDetailSuccess(
    selectedGeneration: Generation,
    pokemonState: PokemonUiState,
    showLoadingItem: Boolean,
    navigateToPokemon: (String) -> Unit,
    updateFavourite: (id: Int, isFavourite: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        GenerationHeader(selectedGeneration.name, Modifier.weight(0.1f))
        GenerationBody(
            selectedGeneration,
            pokemonState,
            showLoadingItem,
            navigateToPokemon,
            updateFavourite,
            modifier = Modifier.weight(0.9f)
        )
    }
}

@Composable
private fun GenerationHeader(
    generationName: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.standard_padding))
            .clip(CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))
            .border(
                dimensionResource(id = R.dimen.default_border),
                TYPE_TCG_COLORLESS_BORDER,
                CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage))
            )
            .background(
                TYPE_TCG_COLORLESS_BACKGROUND,
                CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage))
            )
    ) {
        Text(
            text = generationName.replaceFirstChar { it.uppercaseChar() },
            color = TYPE_TCG_COLORLESS_BORDER
        )
    }
}

@Composable
private fun GenerationBody(
    selectedGeneration: Generation,
    pokemonState: PokemonUiState,
    showLoadingItem: Boolean,
    navigateToPokemon: (String) -> Unit,
    updateFavourite: (id: Int, isFavourite: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    when (pokemonState) {
        is PokemonUiState.Success -> {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                modifier = modifier.testTag(
                    stringResource(R.string.test_tag_generation_detail_pokemon_success)
                )
            ) {
                items(pokemonState.pokemon) {
                    PokemonColumnItem(
                        pokemon = it,
                        backgroundBrush = createGradientBrush(
                            it.types.map { type ->
                                getTypeBackgroundColor(type.name)
                            }
                        ),
                        borderBrush = createGradientBrush(
                            it.types.map { type ->
                                getTypeBorderColor(type.name)
                            }
                        ),
                        textColor = getTypePrimaryColor(it.types.first().name),
                        navigateToPokemon = navigateToPokemon,
                        updateFavourite = updateFavourite,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.preview_size))
                            .padding(
                                horizontal = dimensionResource(id = R.dimen.standard_padding),
                                vertical = dimensionResource(id = R.dimen.medium_padding)
                            )
                    )
                }
                if (showLoadingItem) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensionResource(id = R.dimen.preview_size))
                                .padding(
                                    horizontal = dimensionResource(id = R.dimen.standard_padding),
                                    vertical = dimensionResource(id = R.dimen.medium_padding)
                                )
                                .clip(
                                    CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage))
                                )
                                .border(
                                    dimensionResource(id = R.dimen.default_border),
                                    TYPE_TCG_COLORLESS_BORDER,
                                    CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage))
                                )
                                .background(
                                    TYPE_TCG_COLORLESS_BACKGROUND,
                                    CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage))
                                )
                        ) {
                            LoadingAnimation(
                                modifier = Modifier.testTag(stringResource(R.string.test_tag_loading_animation))
                            )
                        }
                    }
                }
            }
        }

        PokemonUiState.Loading -> Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.testTag(stringResource(R.string.test_tag_generation_detail_pokemon_loading))
        ) { LoadingAnimation() }

        PokemonUiState.Error -> Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.testTag(stringResource(R.string.test_tag_generation_detail_pokemon_error))
        ) {
            ErrorMessage(
                errorMessage = stringResource(
                    R.string.error_loading_pokemon_for_generation,
                    selectedGeneration.name
                )
            )
        }
    }
}
