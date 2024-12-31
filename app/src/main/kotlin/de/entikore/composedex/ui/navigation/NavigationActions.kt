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

import androidx.navigation.NavHostController
import de.entikore.composedex.ui.navigation.destination.Pokemon
import de.entikore.composedex.ui.navigation.destination.Type

/**
 * Provides navigation actions for navigating between screens.
 *
 * This class encapsulates navigation logic using a [NavHostController] to
 * navigate to different screens with arguments, such as the Type screen and the Pokémon screen.
 *
 * @param navController The [NavHostController] used for performing navigation.
 */
class NavigationActions(private val navController: NavHostController) {
    fun navigateToTypeScreen(
        typeName: String?
    ) {
        val route = typeName?.let { "${Type.route}?${Type.typeArg}=$it" } ?: Type.route
        navController.navigate(
            route
        )
    }

    fun navigateToPokemonScreen(
        pokemonNameOrId: String?
    ) {
        val route =
            pokemonNameOrId?.let { "${Pokemon.route}?${Pokemon.pokemonArg}=$it" } ?: Pokemon.route
        navController.navigate(
            route
        )
    }
}
