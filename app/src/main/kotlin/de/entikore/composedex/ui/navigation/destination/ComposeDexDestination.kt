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
package de.entikore.composedex.ui.navigation.destination

import androidx.navigation3.runtime.NavKey
import de.entikore.composedex.R
import kotlinx.serialization.Serializable

/**
 * Represents a destination within the ComposeDex application.
 *
 * Each destination is characterized by an icon, a route for navigation, and a user-friendly name.
 */
sealed interface ComposeDexDestination : NavKey {
    val icon: Int
    val uiName: String
}

/**
 * Destination for [de.entikore.composedex.ui.screen.pokemon.PokemonScreen].
 */
@Serializable
data class PokemonDestination(
    override val icon: Int = R.drawable.ic_jiggly_pixel,
    override val uiName: String = "Pokemon",
    val pokemonName: String? = null
) : ComposeDexDestination, NavKey

/**
 * Destination for [de.entikore.composedex.ui.screen.favourite.FavouriteDestination].
 */
@Serializable
data class FavouriteDestination(
    override val icon: Int = R.drawable.ic_star_pixel,
    override val uiName: String = "Favourites"
) : ComposeDexDestination

/**
 * Destination for [de.entikore.composedex.ui.screen.generation.GenerationScreen].
 */
@Serializable
data class GenerationDestination(
    override val icon: Int = R.drawable.ic_balls_pixel,
    override val uiName: String = "Generations"
) : ComposeDexDestination

/**
 * Destination for [de.entikore.composedex.ui.screen.type.TypeScreen].
 */
@Serializable
data class TypeDestination(
    override val icon: Int = R.drawable.ic_eevee_pixel,
    override val uiName: String = "Types",
    val typeName: String? = null
) : ComposeDexDestination

/**
 * Destination for [de.entikore.composedex.ui.screen.setting.SettingsScreen].
 */
@Serializable
data class SettingsDestination(
    override val icon: Int = R.drawable.ic_settings_pixel,
    override val uiName: String = "Settings"
) : ComposeDexDestination

val drawerScreens: List<ComposeDexDestination> =
    listOf(
        PokemonDestination(),
        FavouriteDestination(),
        GenerationDestination(),
        TypeDestination(),
        SettingsDestination()
    )
