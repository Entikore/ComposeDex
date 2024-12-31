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
package de.entikore.composedex.ui.screen.pokemon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.pokemon.PokemonLabels
import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.ui.component.AutoSizeText
import de.entikore.composedex.ui.component.BorderedLabel
import de.entikore.composedex.ui.component.ErrorMessage
import de.entikore.composedex.ui.component.HorizontalPageIndicator
import de.entikore.composedex.ui.component.LazyTypeRowWithTopLabel
import de.entikore.composedex.ui.component.LoadingAnimation
import de.entikore.composedex.ui.component.Searchbar
import de.entikore.composedex.ui.component.TopBarWithSearchbar
import de.entikore.composedex.ui.component.TypeRow
import de.entikore.composedex.ui.component.VerticalPageIndicator
import de.entikore.composedex.ui.component.cutCornerShapeBackgroundWithBorder

/**
 * The top level composable for the Pokemon screen.
 */
@Composable
fun PokemonScreen(
    openDrawer: () -> Unit,
    navigateToTypes: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PokemonViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsState()
    val lookUpPokemon = viewModel::lookUpPokemon
    val selectVariety = viewModel::selectVariety
    val selectType = viewModel::selectType
    val updateFavourite = viewModel::updateFavourite
    val playSound = viewModel::playSound
    val speakTextEntry = viewModel::speakTextEntry
    val changeEvolutionText = viewModel::changeDisplayedEvolutionText

    PokemonScreen(
        screenState,
        openDrawer,
        navigateToTypes,
        lookUpPokemon,
        selectVariety,
        selectType,
        changeEvolutionText,
        updateFavourite,
        playSound,
        speakTextEntry,
        modifier
    )
}

@Composable
fun PokemonScreen(
    screenState: PokemonScreenState,
    openDrawer: () -> Unit,
    navigateToTypes: (String) -> Unit,
    lookUpPokemon: (String) -> Unit,
    selectVariety: (Int) -> Unit,
    selectType: (String) -> Unit,
    changeEvolutionText: (String) -> Unit,
    updateFavourite: (Int, Boolean) -> Unit,
    playSound: (String?) -> Unit,
    speakTextEntry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        TopBarWithSearchbar(
            title = stringResource(R.string.pokemon_screen_top_bar),
            openDrawer = openDrawer,
            searchOnClick = lookUpPokemon
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            when (screenState) {
                is PokemonScreenState.Error -> {
                    ErrorMessage(
                        errorMessage = screenState.errorMessage,
                        modifier = Modifier.testTag(stringResource(R.string.test_tag_pokemonscreenstate_error))
                    )
                }

                is PokemonScreenState.Loading -> {
                    LoadingAnimation(
                        modifier = Modifier
                            .testTag(stringResource(R.string.test_tag_pokemonscreenstate_loading))
                            .fillMaxSize()
                            .padding(dimensionResource(id = R.dimen.large_padding))
                    )
                }

                is PokemonScreenState.NoPokemonSelected -> {
                    NoPokemonSelected(
                        searchPokemon = lookUpPokemon,
                        modifier = Modifier.testTag(
                            stringResource(R.string.test_tag_pokemonscreenstate_nopokemonselected)
                        )
                    )
                }

                is PokemonScreenState.Success -> {
                    PokemonInformation(
                        screenState.selectedPokemon,
                        screenState.displayedEvolution,
                        screenState.selectedType,
                        screenState.varieties,
                        screenState.evolvesFrom,
                        screenState.evolvesTo,
                        lookUpPokemon,
                        selectVariety,
                        selectType,
                        navigateToTypes,
                        changeEvolutionText,
                        updateFavourite,
                        playSound,
                        speakTextEntry,
                        Modifier.testTag(stringResource(R.string.test_tag_pokemonscreenstate_success))
                    )
                }
            }
        }
    }
}

@Composable
private fun NoPokemonSelected(searchPokemon: (id: String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.composedex),
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.standard_padding))
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.standard_padding))
                .aspectRatio(1f)
                .clip(shape = CircleShape)
                .border(
                    width = dimensionResource(id = R.dimen.standard_padding),
                    Color.Black,
                    shape = CircleShape
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(colorResource(id = R.color.pokeball_top))
                )

                Searchbar(
                    searchPokemon,
                    modifier = Modifier
                        .border(
                            width = dimensionResource(id = R.dimen.medium_padding),
                            Color.Black
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(colorResource(id = R.color.pokeball_bottom))
                )
            }
        }
    }
}

@Composable
private fun PokemonInformation(
    selectedPokemon: Pokemon,
    displayedEvolution: String,
    selectedType: Type,
    varieties: List<Pokemon>,
    evolvesFrom: PokemonPreview?,
    evolvesTo: List<PokemonPreview>,
    lookUpPokemon: (String) -> Unit,
    selectVariety: (Int) -> Unit,
    selectType: (String) -> Unit,
    navigateToTypes: (String) -> Unit,
    changeEvolutionText: (String) -> Unit,
    updateFavourite: (Int, Boolean) -> Unit,
    playSound: (String?) -> Unit,
    speakTextEntry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        PokemonHeader(
            selectedPokemon,
            displayedEvolution,
            evolvesFrom,
            evolvesTo,
            selectedType.name,
            lookUpPokemon,
            navigateToTypes,
            selectType,
            changeEvolutionText,
            Modifier
                .testTag(stringResource(R.string.test_tag_pokemonheader))
                .weight(0.15f)
                .padding(
                    top = dimensionResource(id = R.dimen.standard_padding),
                    start = dimensionResource(id = R.dimen.medium_padding),
                    end = dimensionResource(id = R.dimen.medium_padding)
                )
        )

        PokemonPicture(
            selectedPokemon,
            varieties,
            selectVariety,
            speakTextEntry,
            modifier = Modifier
                .testTag(stringResource(R.string.test_tag_pokemonpicture))
                .weight(0.4f)
        )

        PokemonInformation(
            selectedPokemon,
            updateFavourite,
            playSound,
            speakTextEntry,
            modifier = Modifier
                .testTag(stringResource(R.string.test_tag_pokemoninfo))
                .weight(0.3f)
        )

        PokemonWeaknessAndResistance(
            modifier = Modifier
                .testTag(stringResource(R.string.test_tag_pokemonweaknessandresistance))
                .weight(0.15f)
                .padding(
                    start = dimensionResource(id = R.dimen.medium_padding),
                    end = dimensionResource(id = R.dimen.medium_padding),
                    bottom = dimensionResource(id = R.dimen.standard_padding)
                ),
            types = selectedPokemon.types,
            selectedType = selectedType,
            navigateToTypes = navigateToTypes
        )
    }
}

@Composable
private fun PokemonInformation(
    selectedPokemon: Pokemon,
    updateFavourite: (Int, Boolean) -> Unit,
    playSound: (String?) -> Unit,
    speakTextEntry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.standard_padding))
    ) {
        val pagerState = rememberPagerState(pageCount = { selectedPokemon.textEntries.size })
        PokemonTextEntries(
            pagerState = pagerState,
            pokemon = selectedPokemon,
            modifier = Modifier.weight(0.8f)
        )
        PokemonButtons(
            pokemon = selectedPokemon,
            textEntry = pagerState.currentPage,
            updateFavourite = updateFavourite,
            playSound = playSound,
            talkPokemon = speakTextEntry,
            modifier = Modifier
                .weight(0.2f)
                .padding(vertical = dimensionResource(id = R.dimen.standard_padding))
        )
    }
}

@Composable
private fun PokemonHeader(
    selectedPokemon: Pokemon,
    displayedEvolution: String,
    evolvesFrom: PokemonPreview?,
    evolvesTo: List<PokemonPreview>,
    typeName: String,
    lookUpPokemon: (String) -> Unit,
    navigateToTypes: (String) -> Unit,
    selectType: (String) -> Unit,
    changeEvolutionText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sharedModifier = Modifier.padding(
        dimensionResource(id = R.dimen.small_padding)
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.weight(0.25f)
        ) {
            EvolutionChainPreview(
                evolvesFrom = evolvesFrom,
                evolvesTo = evolvesTo,
                selectedType = typeName,
                selectPokemon = lookUpPokemon,
                changeEvolutionText,
                modifier = sharedModifier
                    .aspectRatio(1f)
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.weight(0.75f)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                PokemonLabelRow(
                    rank = selectedPokemon.evolutionRank.uiString,
                    pokemonLabel = selectedPokemon.pokemonLabel,
                    modifier = sharedModifier
                        .weight(0.65f)
                )
                TypeRow(
                    typeNames = selectedPokemon.types.map { it.name },
                    onClick = selectType,
                    onLongClick = navigateToTypes,
                    modifier = sharedModifier
                        .weight(0.35f, false)
                )
            }

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.small_padding))
                ) {
                    AutoSizeText(
                        text = selectedPokemon.name.replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    AutoSizeText(
                        text = displayedEvolution,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PokemonLabelRow(
    rank: String,
    pokemonLabel: PokemonLabels,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier

    ) {
        BorderedLabel(labelText = rank)
        if (pokemonLabel.baby.second) {
            BorderedLabel(
                labelText = pokemonLabel.baby.first.uiString
            )
        }
        if (pokemonLabel.legendary.second) {
            BorderedLabel(
                labelText = pokemonLabel.legendary.first.uiString
            )
        }
        if (pokemonLabel.mystical.second) {
            BorderedLabel(
                labelText = pokemonLabel.mystical.first.uiString
            )
        }
    }
}

@Composable
private fun EvolutionChainPreview(
    evolvesFrom: PokemonPreview?,
    evolvesTo: List<PokemonPreview>,
    selectedType: String,
    selectPokemon: (String) -> Unit,
    changeEvolutionText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
    ) {
        val previewSizeModifier = Modifier
            .fillMaxSize()
            .cutCornerShapeBackgroundWithBorder(
                integerResource(id = R.integer.default_cut_corner_shape_percentage),
                color = MaterialTheme.colorScheme.primaryContainer,
                borderWidth = dimensionResource(R.dimen.default_border),
                borderColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        when (evolvesFrom != null && evolvesTo.isNotEmpty()) {
            true -> {
                Stage1Pokemon(
                    evolvesFrom,
                    evolvesTo,
                    selectedType,
                    selectPokemon,
                    changeEvolutionText,
                    previewSizeModifier.testTag(stringResource(R.string.test_tag_stage1pokemon))
                )
            }

            false -> {
                if (evolvesFrom != null) {
                    Stage2Pokemon(
                        evolvesFrom,
                        selectedType,
                        selectPokemon,
                        changeEvolutionText,
                        previewSizeModifier.testTag(stringResource(R.string.test_tag_stage2pokemon))
                    )
                } else if (evolvesTo.isNotEmpty()) {
                    Stage0PokemonWithEvolution(
                        evolvesTo,
                        selectedType,
                        selectPokemon,
                        changeEvolutionText,
                        previewSizeModifier.testTag(stringResource(R.string.test_tag_stage0pokemonwithevolution))
                    )
                } else {
                    Spacer(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun Stage0PokemonWithEvolution(
    evolvesTo: List<PokemonPreview>,
    selectedTypeName: String,
    selectPokemon: (String) -> Unit,
    changeEvolutionText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        EvolvesToPager(
            evolvesTo = evolvesTo,
            selectedTypeName = selectedTypeName,
            selectPokemon = selectPokemon,
            changeEvolutionText,
            modifier = Modifier.fillMaxSize(0.9f)
        )
        Spacer(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.small_indicator_size))
                .padding(vertical = dimensionResource(id = R.dimen.small_padding))
        )
    }
}

@Composable
private fun Stage1Pokemon(
    evolvesFrom: PokemonPreview,
    evolvesTo: List<PokemonPreview>,
    typeName: String,
    selectPokemon: (String) -> Unit,
    changeEvolutionText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState { 2 }
    val sharedModifier = Modifier
        .fillMaxWidth(0.9f)
        .aspectRatio(1f)
        .clip(CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))

    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    val currentChangeEvolutionText by rememberUpdatedState(changeEvolutionText)
    LaunchedEffect(currentPage) {
        if (currentPage == 0) {
            currentChangeEvolutionText(evolvesFrom.evolutionText)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill
        ) { index ->
            when (index) {
                0 -> {
                    if (evolvesFrom.isLoading) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = sharedModifier
                        ) {
                            LoadingAnimation(circleSize = dimensionResource(id = R.dimen.indicator_size))
                        }
                    } else {
                        EvolutionChainPicture(
                            evolvesFromTypes = evolvesFrom.types,
                            selectedTypeName = typeName,
                            evolvesFromName = evolvesFrom.name,
                            evolvesFromSprite = evolvesFrom.sprite,
                            selectPokemon = selectPokemon,
                            modifier = sharedModifier
                        )
                    }
                }

                1 -> {
                    EvolvesToPager(
                        evolvesTo = evolvesTo,
                        selectedTypeName = typeName,
                        selectPokemon = selectPokemon,
                        changeEvolutionText = changeEvolutionText,
                        modifier = sharedModifier
                    )
                }
            }
        }
        HorizontalPageIndicator(pagerState)
    }
}

@Composable
private fun Stage2Pokemon(
    pokemon: PokemonPreview,
    selectedTypeName: String,
    selectPokemon: (String) -> Unit,
    changeEvolutionText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sharedModifier = Modifier
        .fillMaxWidth(0.9f)
        .aspectRatio(1f)
        .clip(CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))
    SideEffect {
        changeEvolutionText(pokemon.evolutionText)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        if (pokemon.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = sharedModifier
            ) {
                LoadingAnimation(circleSize = dimensionResource(id = R.dimen.indicator_size))
            }
        } else {
            EvolutionChainPicture(
                evolvesFromTypes = pokemon.types,
                selectedTypeName = selectedTypeName,
                evolvesFromName = pokemon.name,
                evolvesFromSprite = pokemon.sprite,
                selectPokemon = selectPokemon,
                modifier = sharedModifier
            )
        }
        Spacer(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.small_indicator_size))
                .padding(vertical = dimensionResource(id = R.dimen.small_padding))
        )
    }
}

@Composable
private fun EvolutionChainPicture(
    evolvesFromTypes: List<Type>,
    selectedTypeName: String,
    evolvesFromName: String,
    evolvesFromSprite: String,
    selectPokemon: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var type: Type?
    if (evolvesFromTypes.isNotEmpty()) {
        type = evolvesFromTypes.firstOrNull { it.name == selectedTypeName }
        if (type == null) {
            type = evolvesFromTypes.first()
        }
    }
    AsyncImage(
        model = evolvesFromSprite,
        contentDescription = stringResource(
            R.string.image_description_pokemon_sprite,
            evolvesFromName
        ),
        modifier
            .clickable {
                selectPokemon(evolvesFromName)
            }
    )
}

@Composable
private fun EvolvesToPager(
    evolvesTo: List<PokemonPreview>,
    selectedTypeName: String,
    selectPokemon: (String) -> Unit,
    changeEvolutionText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.testTag(stringResource(R.string.test_tag_evolvestopager))
    ) {
        val pagerState = rememberPagerState { evolvesTo.size }
        val sharedModifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f)
            .clip(CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))

        val currentPage by remember { derivedStateOf { pagerState.currentPage } }
        val currentChangeEvolutionText by rememberUpdatedState(changeEvolutionText)

        LaunchedEffect(currentPage) {
            currentChangeEvolutionText(evolvesTo[currentPage].evolutionText)
        }

        VerticalPager(
            state = pagerState,
            pageSize = PageSize.Fill
        ) { index ->
            if (evolvesTo[index].isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = sharedModifier
                ) {
                    LoadingAnimation(circleSize = 8.dp)
                }
            } else {
                EvolutionChainPicture(
                    evolvesFromTypes = evolvesTo[index].types,
                    selectedTypeName = selectedTypeName,
                    evolvesFromName = evolvesTo[index].name,
                    evolvesFromSprite = evolvesTo[index].sprite,
                    selectPokemon = selectPokemon,
                    modifier = sharedModifier
                )
            }
        }
        VerticalPageIndicator(pagerState = pagerState)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PokemonPicture(
    selectedPokemon: Pokemon,
    varieties: List<Pokemon>,
    selectVariety: (Int) -> Unit,
    speakTextEntry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .zIndex(1f)
            .fillMaxHeight()
            .padding(horizontal = 48.dp, vertical = 8.dp)
            .aspectRatio(1f)
            .cutCornerShapeBackgroundWithBorder(
                integerResource(id = R.integer.medium_cut_corner_shape_percentage),
                color = MaterialTheme.colorScheme.primaryContainer,
                borderWidth = dimensionResource(R.dimen.default_border),
                borderColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
    ) {
        if (varieties.isNotEmpty()) {
            val pagerState = rememberPagerState(initialPage = 0, pageCount = {
                varieties.size
            })

            val currentPage by remember { derivedStateOf { pagerState.currentPage } }
            val currentSelectVariety by rememberUpdatedState(selectVariety)
            LaunchedEffect(currentPage) {
                currentSelectVariety(currentPage)
            }

            if (pagerState.pageCount > 0) {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    modifier =
                    Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                        .padding(
                            vertical = dimensionResource(R.dimen.medium_padding),
                            horizontal = dimensionResource(R.dimen.xl_padding)
                        )
                ) {
                    val number = pagerState.pageCount
                    if (number > 1) {
                        repeat(number) { iteration ->
                            val alpha = if (pagerState.currentPage == iteration) 1f else 0.3f
                            Image(
                                painter = painterResource(id = R.drawable.ic_ball_red),
                                contentDescription = stringResource(R.string.pokemon_screen_variety_indicator),
                                modifier = Modifier
                                    .size(dimensionResource(R.dimen.large_padding))
                                    .padding(horizontal = dimensionResource(R.dimen.small_padding))
                                    .alpha(alpha)
                            )
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    pageSize = PageSize.Fill,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.standard_padding))
                        .aspectRatio(1f)
                ) { index ->
                    AsyncImage(
                        model = varieties[index].artwork,
                        contentDescription = stringResource(
                            R.string.image_description_pokemon_artwork,
                            varieties[index].name
                        ),
                        Modifier
                            .fillMaxSize()
                            .clip(CutCornerShape(integerResource(id = R.integer.medium_cut_corner_shape_percentage)))
                            .clickable { speakTextEntry("${varieties[index].name} the ${varieties[index].genera}") }
                            .padding(
                                dimensionResource(R.dimen.large_padding),
                                bottom = dimensionResource(R.dimen.large_padding)
                            )
                    )
                }
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                    Modifier
                        .padding(
                            start = dimensionResource(R.dimen.standard_padding),
                            end = dimensionResource(R.dimen.standard_padding),
                            bottom = dimensionResource(R.dimen.standard_padding)
                        )
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.pokemon_number, selectedPokemon.id),
                            fontSize = MaterialTheme.typography.labelMedium.fontSize
                        )
                        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)))
                        Text(
                            text =
                            selectedPokemon.genera.split(" ").joinToString(" ") {
                                it.replaceFirstChar { char -> char.uppercase() }
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.labelMedium.fontSize
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.pokemon_height, selectedPokemon.height),
                            fontSize = MaterialTheme.typography.labelSmall.fontSize
                        )
                        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.standard_padding)))
                        Text(
                            text = stringResource(R.string.pokemon_weight, selectedPokemon.weight),
                            fontSize = MaterialTheme.typography.labelSmall.fontSize
                        )
                    }
                }
            }
        } else {
            LoadingAnimation(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun PokemonButtons(
    pokemon: Pokemon,
    textEntry: Int,
    updateFavourite: (Int, Boolean) -> Unit,
    playSound: (String?) -> Unit,
    talkPokemon: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxHeight()
    ) {
        val buttonModifier = Modifier
            .cutCornerShapeBackgroundWithBorder(
                integerResource(id = R.integer.default_cut_corner_shape_percentage),
                color = MaterialTheme.colorScheme.primaryContainer,
                borderWidth = dimensionResource(R.dimen.default_border),
                borderColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            .padding(dimensionResource(id = R.dimen.large_padding))
        Icon(
            painterResource(
                id = if (pokemon.isFavourite) R.drawable.ic_favourite else R.drawable.ic_favourite_border
            ),
            contentDescription = stringResource(R.string.image_description_favourite_indicator),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .clip(shape = CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))
                .clickable {
                    updateFavourite(pokemon.id, !pokemon.isFavourite)
                }
                .then(buttonModifier)
        )
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.small_padding)))
        Icon(
            painterResource(id = R.drawable.ic_info),
            contentDescription = stringResource(R.string.image_description_text_entry_button),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .clip(shape = CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))
                .clickable {
                    talkPokemon(pokemon.textEntries[textEntry])
                }
                .then(buttonModifier)
        )
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.small_padding)))
        Icon(
            painterResource(id = if (pokemon.cry.isNullOrEmpty()) R.drawable.ic_music_off else R.drawable.ic_music),
            contentDescription = stringResource(R.string.image_description_cry_button),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .clip(shape = CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))
                .clickable {
                    playSound(pokemon.cry)
                }
                .then(buttonModifier)
        )
    }
}

@Composable
private fun PokemonTextEntries(
    pokemon: Pokemon,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val textEntryPagerSemantics = stringResource(
        R.string.semantics_displays_the_text_information_of_the_pokemon,
        pokemon.name
    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .cutCornerShapeBackgroundWithBorder(
                shape = integerResource(id = R.integer.small_cut_corner_shape_percentage),
                color = MaterialTheme.colorScheme.primaryContainer,
                borderWidth = dimensionResource(R.dimen.default_border),
                borderColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            .padding(
                dimensionResource(id = R.dimen.medium_padding)
            )
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(0.8f, fill = true)
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                .semantics {
                    contentDescription = textEntryPagerSemantics
                }
        ) { page ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                item {
                    Text(
                        text = pokemon.textEntries[page],
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        modifier = Modifier.weight(1f, fill = true)
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.medium_padding),
                    vertical = dimensionResource(id = R.dimen.medium_padding)
                )
                .fillMaxWidth()
                .weight(0.2f, fill = true)
        ) {
            Text(
                text = stringResource(
                    R.string.pokemon_screen_text_entry_indicator,
                    pagerState.currentPage.plus(1),
                    pokemon.textEntries.size
                ),
                fontSize = MaterialTheme.typography.labelSmall.fontSize
            )
        }
    }
}

@Composable
private fun PokemonWeaknessAndResistance(
    types: List<Type>,
    selectedType: Type,
    navigateToTypes: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            LazyTypeRowWithTopLabel(
                typeNames = types.first { it == selectedType }.doubleDamageFrom,
                labelText = stringResource(R.string.label_weakness),
                onClick = navigateToTypes,
                modifier = Modifier
                    .testTag(stringResource(R.string.test_tag_weakness))
                    .weight(1f)
            )
            LazyTypeRowWithTopLabel(
                typeNames = types.first { it == selectedType }.halfDamageFrom,
                labelText = stringResource(R.string.label_resistance),
                onClick = navigateToTypes,
                modifier = Modifier
                    .testTag(stringResource(R.string.test_tag_resistance))
                    .weight(1f)
            )
        }
    }
}
