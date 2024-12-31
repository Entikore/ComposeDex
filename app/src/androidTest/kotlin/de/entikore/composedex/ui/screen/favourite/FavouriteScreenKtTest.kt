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

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import de.entikore.composedex.R
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.hasContentDescriptionStringId
import de.entikore.composedex.onNodeWithTagStringId
import de.entikore.composedex.onNodeWithTextStringId
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.sharedtestcode.POKEMON_BELLOSSOM_NAME
import de.entikore.sharedtestcode.POKEMON_GLOOM_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.POKEMON_VILEPLUME_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import org.junit.Rule
import org.junit.Test

class FavouriteScreenKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun test_FavouriteScreen_displays_SuccessNoFavourites() {
        val fakeScreenState = PokemonUiState.Success(emptyList())

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_no_favourites).assertIsDisplayed()
        composeTestRule.onNodeWithTextStringId(R.string.no_favourite_pokemon_selected)
            .assertIsDisplayed()
    }

    @Test
    fun test_FavouriteScreen_displays_SuccessWithFavourites() {
        val oddish =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
        val gloom =
            getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
        val vileplume =
            getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity().asExternalModel()
        val bellossom =
            getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity().asExternalModel()
        val favourites =
            listOf(oddish, gloom, vileplume, bellossom).map { it.copy(isFavourite = true) }

        val fakeScreenState = PokemonUiState.Success(favourites)

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_favourite_screen_success)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_favourite_screen_success)
            .onChildren().assertCountEquals(favourites.size)
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_favourite_screen_success)
            .onChildren().assertAll(
                hasClickAction()
            )
        for (pokemon in favourites) {
            val node =
                composeTestRule.onNodeWithTagStringId(R.string.test_tag_favourite_screen_success)
                    .onChildren()
                    .assertAny(
                        composeTestRule.hasContentDescriptionStringId(
                            id = R.string.cD_display_image_of, substring = false,
                            ignoreCase = false, pokemon.name
                        )
                    )
            for (pokemonType in pokemon.types) {
                node.assertAny(
                    composeTestRule.hasContentDescriptionStringId(
                        id = R.string.cD_type_icon_of, substring = false,
                        ignoreCase = false, pokemonType.name
                    )
                )
            }
        }
    }

    @Test
    fun test_FavouriteScreen_displays_Loading() {
        val fakeScreenState = PokemonUiState.Loading

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_favourite_screen_loading)
            .assertIsDisplayed()
    }

    @Test
    fun test_FavouriteScreen_displays_Error() {
        val fakeScreenState = PokemonUiState.Error

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_favourite_screen_error)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTextStringId(R.string.error_fetching_favourite_pokemon)
            .assertIsDisplayed()
    }

    private fun setupComposeTestRule(screenState: PokemonUiState) {
        composeTestRule.setContent {
            FavouriteScreenContent(
                screenState = screenState,
                updateFavourite = { _, _ -> },
                navigateToPokemon = { _ -> },
            )
        }
    }
}
