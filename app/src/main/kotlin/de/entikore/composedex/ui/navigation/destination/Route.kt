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
package de.entikore.composedex.ui.navigation.destination

import androidx.navigation.NavType
import androidx.navigation.navArgument
import de.entikore.composedex.R

/**
 * Represents a destination within the ComposeDex application.
 *
 * Each destination is characterized by an icon, a route for navigation, and a user-friendly name.
 */
sealed interface ComposeDexDestination {
    val icon: Int
    val route: String
    val uiName: String
}

data object Pokemon : ComposeDexDestination {
    override val icon: Int
        get() = R.drawable.ic_jiggly_pixel
    override val route: String
        get() = "pokemon"
    override val uiName: String
        get() = "Pokemon"

    const val pokemonArg = "pokemon_name_or_id"
    val routeWithArgs = "$route?$pokemonArg={$pokemonArg}"
    val arguments = listOf(
        navArgument(pokemonArg) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        }
    )
}

data object Favourite : ComposeDexDestination {
    override val icon: Int
        get() = R.drawable.ic_star_pixel
    override val route: String
        get() = "favourite"
    override val uiName: String
        get() = "Favourites"
}

data object Generation : ComposeDexDestination {
    override val icon: Int
        get() = R.drawable.ic_balls_pixel
    override val route: String
        get() = "generation"
    override val uiName: String
        get() = "Generations"
}

data object Type : ComposeDexDestination {
    override val icon: Int
        get() = R.drawable.ic_eevee_pixel
    override val route: String
        get() = "type"
    override val uiName: String
        get() = "Types"

    const val typeArg = "type_name"
    val routeWithArgs = "$route?$typeArg={$typeArg}"
    val arguments = listOf(
        navArgument(typeArg) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        }
    )
}

data object Settings : ComposeDexDestination {
    override val icon: Int
        get() = R.drawable.ic_settings_pixel
    override val route: String
        get() = "setting"
    override val uiName: String
        get() = "Settings"
}

val drawerScreens = listOf(Pokemon, Favourite, Generation, Type, Settings)
