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
package de.entikore.composedex.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import de.entikore.composedex.R
import de.entikore.composedex.ui.navigation.destination.ComposeDexDestination
import de.entikore.composedex.ui.navigation.destination.FavouriteDestination
import de.entikore.composedex.ui.navigation.destination.GenerationDestination
import de.entikore.composedex.ui.navigation.destination.PokemonDestination
import de.entikore.composedex.ui.navigation.destination.SettingsDestination
import de.entikore.composedex.ui.navigation.destination.TypeDestination
import de.entikore.composedex.ui.screen.favourite.FavouriteScreen
import de.entikore.composedex.ui.screen.generation.GenerationScreen
import de.entikore.composedex.ui.screen.pokemon.PokemonScreen
import de.entikore.composedex.ui.screen.pokemon.PokemonViewModel
import de.entikore.composedex.ui.screen.setting.SettingsScreen
import de.entikore.composedex.ui.screen.type.TypeScreen
import de.entikore.composedex.ui.screen.type.TypeViewModel

@Composable
fun DrawerNavHost(
    drawerState: DrawerState,
    snackBarHostState: SnackbarHostState,
    changeDrawerState: () -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backstack = remember { mutableStateListOf<ComposeDexDestination>(PokemonDestination()) }
    val currentScreenObjectForNavDisplay: ComposeDexDestination? = backstack.lastOrNull()

    ModalNavigationDrawer(
        drawerContent = {
            ComposeDexDrawer(
                currentlySelected = currentScreenObjectForNavDisplay,
                onDestinationClick = { route ->
                    changeDrawerState()
                    backstack.add(route)
                }
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackBarHostState)
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = modifier.fillMaxSize()
        ) { padding ->

            NavDisplay(
                entryDecorators = listOf(
                    // Add the default decorators for managing scenes and saving state
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    // Then add the view model store decorator
                    rememberViewModelStoreNavEntryDecorator()
                ),
                backStack = backstack,
                onBack = { backstack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<PokemonDestination> { pokemonDestination ->
                        PokemonScreen(
                            openDrawer = changeDrawerState,
                            navigateToTypes = { type: String ->
                                backstack.add(TypeDestination(typeName = type))
                            },
                            viewModel = hiltViewModel<PokemonViewModel>().also {
                                pokemonDestination.pokemonName?.let { name ->
                                    it.lookUpPokemon(name)
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(stringResource(R.string.test_tag_compose_dex_destination_pokemon))
                                .padding(padding)
                        )
                    }
                    entry<FavouriteDestination> {
                        FavouriteScreen(
                            navigateToPokemon = { pokemon: String ->
                                backstack.add(PokemonDestination(pokemonName = pokemon))
                            },
                            openDrawer = changeDrawerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(
                                    stringResource(R.string.test_tag_compose_dex_destination_favourite)
                                )
                        )
                    }
                    entry<GenerationDestination> {
                        GenerationScreen(
                            navigateToPokemon = { pokemon: String ->
                                backstack.add(PokemonDestination(pokemonName = pokemon))
                            },
                            openDrawer = changeDrawerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(
                                    stringResource(R.string.test_tag_compose_dex_destination_generation)
                                )
                        )
                    }
                    entry<TypeDestination> { typeDestination ->
                        TypeScreen(
                            navigateToPokemon = { pokemon: String ->
                                backstack.add(PokemonDestination(pokemonName = pokemon))
                            },
                            openDrawer = changeDrawerState,
                            viewModel = hiltViewModel<TypeViewModel>().also {
                                typeDestination.typeName?.let { name ->
                                    it.fetchType(name)
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(
                                    stringResource(R.string.test_tag_compose_dex_destination_type)
                                )
                        )
                    }
                    entry<SettingsDestination> {
                        SettingsScreen(
                            openDrawer = changeDrawerState,
                            showSnackbar = showSnackbar,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(
                                    stringResource(R.string.test_tag_compose_dex_destination_settings)
                                )
                        )
                    }
                },
                transitionSpec = {
                    // Slide in from right when navigating forward
                    slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                },
                popTransitionSpec = {
                    // Slide in from left when navigating back
                    slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                },
                predictivePopTransitionSpec = {
                    // Slide in from left when navigating back
                    slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                },
            )
        }
    }
}
