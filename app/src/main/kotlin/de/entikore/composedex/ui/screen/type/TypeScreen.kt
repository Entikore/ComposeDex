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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.pokemon.PokemonShape
import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.ui.component.ClickableColumnItemWithIcon
import de.entikore.composedex.ui.component.ErrorMessage
import de.entikore.composedex.ui.component.LazyTypeRowWithTopLabel
import de.entikore.composedex.ui.component.LoadingAnimation
import de.entikore.composedex.ui.component.TopBarWithSearchbarAndFilter
import de.entikore.composedex.ui.component.TypeIcon
import de.entikore.composedex.ui.component.asCheckBoxItems
import de.entikore.composedex.ui.component.getTypeBackgroundColor
import de.entikore.composedex.ui.component.getTypeBorderColor
import de.entikore.composedex.ui.component.getTypeIcon
import de.entikore.composedex.ui.component.getTypePrimaryColor
import de.entikore.composedex.ui.screen.shared.PokemonFilterOptions
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.composedex.ui.theme.HeartShape

/**
 * The top level composable for the Type screen.
 */
@Composable
fun TypeScreen(
    openDrawer: () -> Unit,
    navigateToPokemon: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TypeViewModel = hiltViewModel()
) {
    val selectedType by viewModel.selectedType.collectAsState()
    val screenState by viewModel.screenState.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val searchType = viewModel::fetchType
    val onNameFilterChange = viewModel::onNameFilterChange
    val onCheckBoxFilterChange = viewModel::onCheckBoxFilterChange
    val onShapeFilterChange = viewModel::onShapeFilterChange
    val updateFavourite = viewModel::updateFavourite

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.background(
            getTypeBackgroundColor(type = selectedType)
        )
    ) {
        TypeScreenTopBar(
            filterOptions,
            openDrawer,
            searchType,
            onNameFilterChange,
            onCheckBoxFilterChange,
            onShapeFilterChange,
            Modifier.background(getTypePrimaryColor(selectedType))
        )
        TypeScreenContent(
            screenState,
            searchType,
            updateFavourite,
            navigateToPokemon
        )
    }
}

@Composable
private fun TypeScreenTopBar(
    filterOptions: PokemonFilterOptions,
    openDrawer: () -> Unit,
    searchType: (typeName: String) -> Unit,
    onNameFilterChange: (text: String) -> Unit,
    onCheckBoxFilterChange: (option: String, newValue: Boolean) -> Unit,
    onShapeFilterChange: (shape: PokemonShape) -> Unit,
    modifier: Modifier = Modifier
) {
    TopBarWithSearchbarAndFilter(
        title = stringResource(R.string.type_screen_top_bar),
        openDrawer = openDrawer,
        searchOnClick = searchType,
        searchText = filterOptions.nameFilter,
        changeSearchText = onNameFilterChange,
        checkBoxItems = filterOptions.checkBoxFilter.asCheckBoxItems(),
        changeFilter = onCheckBoxFilterChange,
        modifier = modifier,
        shapeFilter = filterOptions.shapeFilter,
        changeShapeFilter = onShapeFilterChange
    )
}

@Composable
fun TypeScreenContent(
    screenState: TypeScreenUiState,
    searchType: (String) -> Unit,
    updateFavourite: (Int, Boolean) -> Unit,
    navigateToPokemon: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        when (screenState) {
            TypeScreenUiState.Error -> {
                ErrorMessage(
                    errorMessage = stringResource(R.string.error_fetching_types),
                    Modifier.testTag(
                        stringResource(
                            R.string.test_tag_typescreen_error
                        )
                    )
                )
            }

            TypeScreenUiState.Loading -> {
                LoadingAnimation(modifier = Modifier.testTag(stringResource(R.string.test_tag_typescreen_loading)))
            }

            is TypeScreenUiState.Success -> {
                when (screenState.selectedType) {
                    SelectedTypeUiState.NoTypeSelected -> {
                        TypeOverview(
                            screenState.types,
                            searchType,
                            Modifier.testTag(
                                stringResource(
                                    R.string.test_tag_typescreen_success_overview
                                )
                            )
                        )
                    }

                    else -> {
                        TypeDetail(
                            selectedTypeUiState = screenState.selectedType,
                            searchType = searchType,
                            navigateToPokemon = navigateToPokemon,
                            updateFavourite = updateFavourite
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeOverview(types: List<Type>, searchType: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxSize()
    ) {
        items(types) {
            ClickableColumnItemWithIcon(
                text = it.name,
                backgroundColor = getTypeBackgroundColor(it.name),
                borderColor = getTypeBorderColor(it.name),
                textColor = getTypePrimaryColor(it.name),
                onClick = searchType,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.medium_padding),
                    vertical = dimensionResource(id = R.dimen.small_padding)
                )
            ) {
                TypeIcon(
                    it.name,
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.large_padding))
                )
            }
        }
    }
}

@Composable
private fun TypeDetail(
    selectedTypeUiState: SelectedTypeUiState,
    searchType: (String) -> Unit,
    navigateToPokemon: (String) -> Unit,
    updateFavourite: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        when (selectedTypeUiState) {
            SelectedTypeUiState.Loading -> LoadingAnimation(
                modifier = Modifier.testTag(
                    stringResource(R.string.test_tag_typescreen_success_detail_loading)
                )
            )

            is SelectedTypeUiState.Success -> {
                TypeDetailSuccess(
                    type = selectedTypeUiState.selectedType,
                    pokemonState = selectedTypeUiState.pokemonState,
                    showLoading = selectedTypeUiState.showLoadingItem,
                    searchType = searchType,
                    navigateToPokemon = navigateToPokemon,
                    updateFavourite = updateFavourite,
                    modifier = Modifier.testTag(stringResource(R.string.test_tag_typescreen_success_detail_success))
                )
            }

            else -> ErrorMessage(
                errorMessage = stringResource(R.string.error_fetching_type),
                Modifier.testTag(
                    stringResource(R.string.test_tag_typescreen_success_detail_error)
                )
            )
        }
    }
}

@Composable
private fun TypeDetailSuccess(
    type: Type,
    pokemonState: PokemonUiState,
    showLoading: Boolean,
    searchType: (String) -> Unit,
    navigateToPokemon: (String) -> Unit,
    updateFavourite: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        PokemonTypeHeader(type, searchType, modifier = Modifier.weight(0.4f))
        PokemonTypeBody(
            type,
            pokemonState,
            navigateToPokemon,
            updateFavourite,
            showLoading,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun PokemonTypeHeader(
    type: Type,
    searchType: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.medium_padding))
    ) {
        Box(
            modifier = Modifier
                .weight(0.2f)
                .clip(CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))
                .background(getTypePrimaryColor(type.name))
                .border(
                    border = BorderStroke(
                        width = dimensionResource(id = R.dimen.default_border),
                        color = getTypeBorderColor(type.name)
                    ),
                    shape = CutCornerShape(integerResource(R.integer.default_cut_corner_shape_percentage))
                )
                .padding(dimensionResource(id = R.dimen.standard_padding))
        ) {
            AsyncImage(
                model = Icon(
                    painter = getTypeIcon(icon = type.name),
                    contentDescription = null,
                    tint = getTypeBackgroundColor(type.name),
                    modifier = Modifier
                        .zIndex(1f)
                        .align(Alignment.Center)
                        .aspectRatio(1f)
                        .alpha(0.2f)
                ),
                contentDescription = stringResource(R.string.icon_description_type_icon, type.name),
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.medium_padding))
            )
            Text(
                text = type.name.replaceFirstChar { it.uppercaseChar() },
                fontWeight = FontWeight.Bold,
                color = getTypeBorderColor(type.name),
                modifier = Modifier
                    .zIndex(2f)
                    .align(Alignment.Center)
            )
        }

        val list: List<Pair<String, List<String>>> =
            listOf(
                stringResource(R.string.type_info_double_damage_from) to type.doubleDamageFrom,
                stringResource(R.string.type_info_double_damage_to) to type.doubleDamageTo,
                stringResource(R.string.type_info_half_damage_from) to type.halfDamageFrom,
                stringResource(R.string.type_info_half_damage_to) to type.halfDamageTo,
                stringResource(R.string.type_info_no_damage_from) to type.noDamageFrom,
                stringResource(R.string.type_info_no_damage_to) to type.noDamageTo
            )

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(0.8f)
                .padding(
                    dimensionResource(id = R.dimen.standard_padding)
                )
        ) {
            items(list) {
                if (it.second.isNotEmpty()) {
                    LazyTypeRowWithTopLabel(
                        typeNames = it.second,
                        labelText = it.first,
                        onClick = searchType
                    )
                }
            }
        }
    }
}

@Composable
private fun PokemonTypeBody(
    type: Type,
    pokemonState: PokemonUiState,
    navigateToPokemon: (String) -> Unit,
    updateFavourite: (Int, Boolean) -> Unit,
    showLoading: Boolean,
    modifier: Modifier = Modifier
) {
    when (pokemonState) {
        is PokemonUiState.Success -> {
            val gridData = pokemonState.pokemon
            LazyVerticalGrid(
                columns = GridCells.Adaptive(dimensionResource(R.dimen.grid_cell_size)),
                modifier = modifier.testTag(
                    stringResource(R.string.test_tag_typescreen_success_detail_sucess_pokemon_success)
                )
            ) {
                items(gridData) {
                    TypePokemonEntry(
                        pokemon = it,
                        selectedType = type.name,
                        updateFavourite = updateFavourite,
                        navigateToPokemon = navigateToPokemon
                    )
                }
                if (showLoading) {
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier =
                            Modifier
                                .padding(
                                    horizontal = dimensionResource(R.dimen.standard_padding),
                                    vertical = dimensionResource(R.dimen.standard_padding)
                                )
                                .aspectRatio(1f)
                                .background(
                                    getTypeBackgroundColor(type.name),
                                    shape = CutCornerShape(
                                        integerResource(id = R.integer.medium_cut_corner_shape_percentage)
                                    )
                                )
                                .border(
                                    width = dimensionResource(id = R.dimen.default_border),
                                    getTypeBorderColor(type.name),
                                    shape = CutCornerShape(
                                        integerResource(id = R.integer.medium_cut_corner_shape_percentage)
                                    )
                                )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                LoadingAnimation(
                                    circleSize = 12.dp,
                                    travelDistance = 10.dp
                                )
                            }
                        }
                    }
                }
            }
        }

        PokemonUiState.Loading ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.testTag(
                    stringResource(R.string.test_tag_typescreen_success_detail_sucess_pokemon_loading)
                )
            ) { LoadingAnimation() }

        PokemonUiState.Error ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.testTag(
                    stringResource(R.string.test_tag_typescreen_success_detail_sucess_pokemon_error)
                )
            ) {
                ErrorMessage(
                    errorMessage = stringResource(
                        R.string.error_loading_pokemon_for_type,
                        type.name
                    )
                )
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TypePokemonEntry(
    pokemon: Pokemon,
    selectedType: String,
    updateFavourite: (Int, Boolean) -> Unit,
    navigateToPokemon: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val favouriteModifier: Modifier =
        modifier
            .padding(
                horizontal = dimensionResource(R.dimen.standard_padding),
                vertical = dimensionResource(R.dimen.standard_padding)
            )
            .aspectRatio(1f)
            .background(
                getTypePrimaryColor(selectedType),
                shape = if (pokemon.isFavourite) {
                    HeartShape()
                } else {
                    CutCornerShape(
                        integerResource(R.integer.medium_cut_corner_shape_percentage)
                    )
                }
            )
            .border(
                width = dimensionResource(R.dimen.default_border),
                getTypeBorderColor(selectedType),
                shape = if (pokemon.isFavourite) {
                    HeartShape()
                } else {
                    CutCornerShape(
                        integerResource(R.integer.medium_cut_corner_shape_percentage)
                    )
                }
            )
            .clip(
                shape = if (pokemon.isFavourite) {
                    HeartShape()
                } else {
                    CutCornerShape(
                        integerResource(R.integer.medium_cut_corner_shape_percentage)
                    )
                }
            )

    Box(
        contentAlignment = Alignment.Center,
        modifier =
        favouriteModifier
            .combinedClickable(
                onLongClick = {
                    updateFavourite(pokemon.id, !pokemon.isFavourite)
                }
            ) {
                navigateToPokemon(pokemon.name)
            }
    ) {
        if (!pokemon.sprite.isNullOrEmpty()) {
            AsyncImage(
                model = pokemon.sprite,
                contentDescription = stringResource(R.string.image_description_sprite, pokemon.name),
                Modifier
                    .padding(dimensionResource(R.dimen.standard_padding))
                    .fillMaxSize()
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = pokemon.name,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.small_padding))
                )
                LoadingAnimation(modifier = Modifier.padding(dimensionResource(id = R.dimen.small_padding)))
            }
        }
    }
}

@Composable
@Preview
private fun TypeScreenPreview() {
    TypeScreenContent(screenState = TypeScreenUiState.Loading, {}, { _, _ -> }, {})
}

@Composable
@Preview
private fun TypeScreenPreview2() {
    TypeScreenContent(screenState = TypeScreenUiState.Error, {}, { _, _ -> }, {})
}
