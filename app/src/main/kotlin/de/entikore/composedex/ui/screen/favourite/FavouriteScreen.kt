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

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.pokemon.PokemonShape
import de.entikore.composedex.ui.component.ErrorMessage
import de.entikore.composedex.ui.component.LoadingAnimation
import de.entikore.composedex.ui.component.PokemonColumnItem
import de.entikore.composedex.ui.component.TopBarWithFilter
import de.entikore.composedex.ui.component.asCheckBoxItems
import de.entikore.composedex.ui.component.createGradientBrush
import de.entikore.composedex.ui.component.getTypeBackgroundColor
import de.entikore.composedex.ui.component.getTypeBorderColor
import de.entikore.composedex.ui.component.getTypePrimaryColor
import de.entikore.composedex.ui.screen.shared.PokemonFilterOptions
import de.entikore.composedex.ui.screen.shared.PokemonUiState

@Composable
fun FavouriteScreen(
    openDrawer: () -> Unit,
    navigateToPokemon: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavouriteViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val onNameFilterChange = viewModel::onNameFilterChange
    val onCheckBoxFilterChange = viewModel::onCheckBoxFilterChange
    val onShapeFilterChange = viewModel::onShapeFilterChange
    val updateFavourite = viewModel::updateFavourite

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        FavouriteScreenTopBar(
            filterOptions,
            openDrawer,
            onNameFilterChange,
            onCheckBoxFilterChange,
            onShapeFilterChange
        )
        FavouriteScreenContent(screenState, updateFavourite, navigateToPokemon)
    }
}

@Composable
private fun FavouriteScreenTopBar(
    filterOptions: PokemonFilterOptions,
    openDrawer: () -> Unit,
    onNameFilterChange: (text: String) -> Unit,
    onCheckBoxFilterChange: (option: String, newValue: Boolean) -> Unit,
    onShapeFilterChange: (shape: PokemonShape) -> Unit,
    modifier: Modifier = Modifier
) {
    TopBarWithFilter(
        title = stringResource(R.string.favourites_screen_top_bar),
        openDrawer = openDrawer,
        searchText = filterOptions.nameFilter,
        changeSearchText = onNameFilterChange,
        checkBoxItems = filterOptions.checkBoxFilter.asCheckBoxItems(withFavouriteFilter = false),
        changeFilter = onCheckBoxFilterChange,
        shapeFilter = filterOptions.shapeFilter,
        changeShapeFilter = onShapeFilterChange,
        modifier = modifier
    )
}

@Composable
fun FavouriteScreenContent(
    screenState: PokemonUiState,
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
            PokemonUiState.Error -> ErrorMessage(
                errorMessage = stringResource(R.string.error_fetching_favourite_pokemon),
                modifier = Modifier.testTag(stringResource(R.string.test_tag_favourite_screen_error))
            )

            PokemonUiState.Loading -> LoadingAnimation(
                modifier = Modifier.testTag(stringResource(R.string.test_tag_favourite_screen_loading))
            )

            is PokemonUiState.Success -> {
                if (screenState.pokemon.isEmpty()) {
                    NoFavourites(modifier = Modifier.testTag(stringResource(R.string.test_tag_no_favourites)))
                }
                LazyColumn(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.testTag(
                        stringResource(R.string.test_tag_favourite_screen_success)
                    )
                ) {
                    items(screenState.pokemon) {
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
                }
            }
        }
    }
}

@Composable
private fun NoFavourites(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier =
        modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.standard_padding))
            .clip(shape = CutCornerShape(integerResource(R.integer.default_cut_corner_shape_percentage)))
            .border(
                width = dimensionResource(id = R.dimen.default_border),
                Color.Black,
                shape = CutCornerShape(integerResource(R.integer.default_cut_corner_shape_percentage))
            )
    ) {
        Text(
            stringResource(R.string.no_favourite_pokemon_selected),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    dimensionResource(R.dimen.standard_padding)
                )
        )
    }
}
