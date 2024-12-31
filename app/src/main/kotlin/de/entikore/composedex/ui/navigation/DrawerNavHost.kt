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
package de.entikore.composedex.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import de.entikore.composedex.R
import de.entikore.composedex.ui.navigation.destination.Favourite
import de.entikore.composedex.ui.navigation.destination.Generation
import de.entikore.composedex.ui.navigation.destination.Pokemon
import de.entikore.composedex.ui.navigation.destination.Settings
import de.entikore.composedex.ui.navigation.destination.Type
import de.entikore.composedex.ui.screen.favourite.FavouriteScreen
import de.entikore.composedex.ui.screen.generation.GenerationScreen
import de.entikore.composedex.ui.screen.pokemon.PokemonScreen
import de.entikore.composedex.ui.screen.setting.SettingsScreen
import de.entikore.composedex.ui.screen.type.TypeScreen

@Composable
fun DrawerNavHost(
    navController: NavHostController,
    drawerState: DrawerState,
    snackBarHostState: SnackbarHostState,
    changeDrawerState: () -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    }
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()

    ModalNavigationDrawer(
        drawerContent = {
            ComposeDexDrawer(
                currentlySelected = currentNavBackStackEntry,
                onDestinationClick = { route ->
                    changeDrawerState()
                    navController.navigate(route)
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
            NavHost(navController = navController, startDestination = Pokemon.routeWithArgs) {
                composable(
                    route = Pokemon.routeWithArgs,
                    arguments = Pokemon.arguments
                ) {
                    PokemonScreen(
                        openDrawer = changeDrawerState,
                        navigateToTypes = { type: String ->
                            navActions.navigateToTypeScreen(type)
                        },
                        modifier = Modifier.fillMaxSize()
                            .testTag(stringResource(R.string.test_tag_compose_dex_destination_pokemon))
                            .padding(padding)
                    )
                }
                composable(route = Favourite.route) {
                    FavouriteScreen(
                        navigateToPokemon = { pokemon: String ->
                            navActions.navigateToPokemonScreen(pokemon)
                        },
                        openDrawer = changeDrawerState,
                        modifier = Modifier.fillMaxSize().testTag(
                            stringResource(R.string.test_tag_compose_dex_destination_favourite)
                        )
                    )
                }
                composable(route = Generation.route) {
                    GenerationScreen(
                        navigateToPokemon = { pokemon: String ->
                            navActions.navigateToPokemonScreen(pokemon)
                        },
                        openDrawer = changeDrawerState,
                        modifier = Modifier.fillMaxSize().testTag(
                            stringResource(R.string.test_tag_compose_dex_destination_generation)
                        )
                    )
                }
                composable(
                    route = Type.routeWithArgs,
                    arguments = Type.arguments
                ) {
                    TypeScreen(
                        navigateToPokemon = { pokemon: String ->
                            navActions.navigateToPokemonScreen(pokemon)
                        },
                        openDrawer = changeDrawerState,
                        modifier = Modifier.fillMaxSize().testTag(
                            stringResource(R.string.test_tag_compose_dex_destination_type)
                        )
                    )
                }
                composable(route = Settings.route) {
                    SettingsScreen(
                        openDrawer = changeDrawerState,
                        showSnackbar = showSnackbar,
                        modifier = Modifier.fillMaxSize().testTag(
                            stringResource(R.string.test_tag_compose_dex_destination_settings)
                        )
                    )
                }
            }
        }
    }
}
