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

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import de.entikore.composedex.R
import de.entikore.composedex.data.local.entity.generation.asExternalModel
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.model.generation.toEntity
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.onNodeWithTagStringId
import de.entikore.composedex.onNodeWithTextStringId
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.sharedtestcode.GEN_II_FILE
import de.entikore.sharedtestcode.GEN_I_FILE
import de.entikore.sharedtestcode.GEN_VI_FILE
import de.entikore.sharedtestcode.POKEMON_DITTO_NAME
import de.entikore.sharedtestcode.POKEMON_LAPRAS_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getGenerationRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import org.junit.Rule
import org.junit.Test

class GenerationScreenKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun test_GenerationScreen_displays_Error() {
        val fakeScreenState = GenerationScreenUiState.Error

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_screen_error)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTextStringId(R.string.error_fetching_generations)
            .assertIsDisplayed()
    }

    @Test
    fun test_GenerationScreen_displays_Loading() {
        val fakeScreenState = GenerationScreenUiState.Loading

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_screen_loading)
            .assertIsDisplayed()
    }

    @Test
    fun test_GenerationScreen_displays_GenerationOverview() {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        val generations = listOf(generationI, generationII, generationVI)
        val fakeScreenUiState = GenerationScreenUiState.Success(
            generations = generations,
            selectedGeneration = SelectedGenerationUiState.NoGenerationSelected
        )

        setupComposeTestRule(fakeScreenUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_screen_overview)
            .assertIsDisplayed().onChildren().assertAll(hasClickAction())
    }

    @Test
    fun test_GenerationScreen_displays_GenerationDetail_Loading() {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        val generations = listOf(generationI, generationII, generationVI)

        val fakeSelectedGenerationUiState = SelectedGenerationUiState.Loading

        val fakeScreenUiState = GenerationScreenUiState.Success(
            generations = generations,
            selectedGeneration = fakeSelectedGenerationUiState
        )

        setupComposeTestRule(fakeScreenUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_screen_detail)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_detail_loading)
            .assertIsDisplayed()
    }

    @Test
    fun test_GenerationScreen_displays_GenerationDetail_Success_Pokemon_Loading() {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        val generations = listOf(generationI, generationII, generationVI)

        val fakeSelectedGenerationUiState = SelectedGenerationUiState.Success(
            selectedGeneration = generationI,
            pokemonState = PokemonUiState.Loading,
            showLoadingItem = false
        )
        val fakeScreenUiState = GenerationScreenUiState.Success(
            generations = generations,
            selectedGeneration = fakeSelectedGenerationUiState
        )

        setupComposeTestRule(fakeScreenUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_detail_success)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_detail_pokemon_loading)
            .assertIsDisplayed()
    }

    @Test
    fun test_GenerationScreen_displays_GenerationDetail_Success_Pokemon_Error() {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        val generations = listOf(generationI, generationII, generationVI)

        val fakeSelectedGenerationUiState = SelectedGenerationUiState.Success(
            selectedGeneration = generationI,
            pokemonState = PokemonUiState.Error,
            showLoadingItem = false
        )
        val fakeScreenUiState = GenerationScreenUiState.Success(
            generations = generations,
            selectedGeneration = fakeSelectedGenerationUiState
        )

        setupComposeTestRule(fakeScreenUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_detail_success)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_detail_pokemon_error)
            .assertIsDisplayed()
    }

    @Test
    fun test_GenerationScreen_displays_GenerationDetail_Success_Pokemon_Success_NoLoading() {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        val generations = listOf(generationI, generationII, generationVI)
        val ditto =
            getPokemonInfoRemote(getTestModel(POKEMON_DITTO_NAME)).toEntity().asExternalModel()
        val lapras = getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().asExternalModel()
        val fakeSelectedGenerationUiState = SelectedGenerationUiState.Success(
            selectedGeneration = generationI,
            pokemonState = PokemonUiState.Success(listOf(ditto, lapras)),
            showLoadingItem = false
        )
        val fakeScreenUiState = GenerationScreenUiState.Success(
            generations = generations,
            selectedGeneration = fakeSelectedGenerationUiState
        )

        // since showLoading is false
        val expectedListItems = 2

        setupComposeTestRule(fakeScreenUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_detail_success)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_detail_pokemon_success)
            .assertIsDisplayed().onChildren().assertCountEquals(expectedListItems).assertAll(hasClickAction())
    }

    @Test
    fun test_GenerationScreen_displays_GenerationDetail_Success_Pokemon_Success_Loading() {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        val generations = listOf(generationI, generationII, generationVI)
        val ditto =
            getPokemonInfoRemote(getTestModel(POKEMON_DITTO_NAME)).toEntity().asExternalModel()
        val lapras = getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().asExternalModel()
        val fakeSelectedGenerationUiState = SelectedGenerationUiState.Success(
            selectedGeneration = generationI,
            pokemonState = PokemonUiState.Success(listOf(ditto,lapras)),
            showLoadingItem = true
        )
        val fakeScreenUiState = GenerationScreenUiState.Success(
            generations = generations,
            selectedGeneration = fakeSelectedGenerationUiState
        )

        val expectedListItems = 3

        setupComposeTestRule(fakeScreenUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_detail_success)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_detail_pokemon_success)
            .assertIsDisplayed().onChildren().assertCountEquals(expectedListItems)
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_loading_animation).assertIsDisplayed()
    }

    @Test
    fun test_GenerationScreen_displays_GenerationDetail_Error() {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        val generations = listOf(generationI, generationII, generationVI)

        val fakeSelectedGenerationUiState = SelectedGenerationUiState.Error

        val fakeScreenUiState = GenerationScreenUiState.Success(
            generations = generations,
            selectedGeneration = fakeSelectedGenerationUiState
        )

        setupComposeTestRule(fakeScreenUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_generation_screen_detail)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTextStringId(R.string.error_fetching_generation)
            .assertIsDisplayed()

    }

    private fun setupComposeTestRule(screenState: GenerationScreenUiState) {
        composeTestRule.setContent {
            GenerationScreenContent(
                screenState = screenState,
                searchGeneration = { _ -> },
                updateFavourite = { _, _ -> },
                navigateToPokemon = { _ -> }
            )
        }
    }
}
