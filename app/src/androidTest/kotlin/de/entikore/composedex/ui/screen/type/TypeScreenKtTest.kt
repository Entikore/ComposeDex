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

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import de.entikore.composedex.R
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.local.entity.type.asExternalModel
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.composedex.onNodeWithTagStringId
import de.entikore.composedex.onNodeWithTextStringId
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.sharedtestcode.POKEMON_GLOOM_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.POKEMON_VILEPLUME_NAME
import de.entikore.sharedtestcode.TYPE_GRASS_FILE
import de.entikore.sharedtestcode.TYPE_ICE_FILE
import de.entikore.sharedtestcode.TYPE_POISON_FILE
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTypeRemote
import org.junit.Rule
import org.junit.Test

//TODO not finished, check the current implementation
class TypeScreenKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun test_TypeScreen_displays_Error() {
        val fakeScreenState = TypeScreenUiState.Error

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_typescreen_error)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTextStringId(R.string.error_fetching_types)
            .assertIsDisplayed()
    }

    @Test
    fun test_TypeScreen_displays_Loading() {
        val fakeScreenState = TypeScreenUiState.Loading

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_typescreen_loading)
            .assertIsDisplayed()
    }

    @Test
    fun test_TypeScreen_displays_Success_NoTypeSelected() {
        val iceType = getTypeRemote(TYPE_ICE_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        val types = listOf(iceType, grassType, poisonType)
        val fakeScreenState = TypeScreenUiState.Success(types, SelectedTypeUiState.NoTypeSelected)

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_typescreen_success_overview)
            .assertIsDisplayed().onChildren().assertAll(hasClickAction())
    }

    @Test
    fun test_TypeScreen_displays_Success_SelectedType_Loading() {
        val iceType = getTypeRemote(TYPE_ICE_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        val types = listOf(iceType, grassType, poisonType)
        val fakeScreenState = TypeScreenUiState.Success(types, SelectedTypeUiState.Loading)

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_typescreen_success_detail_loading)
            .assertIsDisplayed()
    }

    @Test
    fun test_TypeScreen_displays_Success_SelectedType_Error() {
        val iceType = getTypeRemote(TYPE_ICE_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        val types = listOf(iceType, grassType, poisonType)
        val fakeScreenState = TypeScreenUiState.Success(types, SelectedTypeUiState.Error)

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_typescreen_success_detail_error)
            .assertIsDisplayed()
    }

    @Test
    fun test_TypeScreen_displays_Success_SelectedType_Success_Pokemon_Loading() {
        val pokemonUiState = PokemonUiState.Loading
        testTypeScreenDisplaysSuccessSelectedTypeSuccessPokemonX(pokemonUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_typescreen_success_detail_sucess_pokemon_loading)
            .assertIsDisplayed()
    }

    @Test
    fun test_TypeScreen_displays_Success_SelectedType_Success_Pokemon_Error() {
        val pokemonUiState = PokemonUiState.Error
        testTypeScreenDisplaysSuccessSelectedTypeSuccessPokemonX(pokemonUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_typescreen_success_detail_sucess_pokemon_error)
            .assertIsDisplayed()
    }

    @Test
    fun test_TypeScreen_displays_Success_SelectedType_Success_Pokemon_Success() {
        val oddish =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
        val gloom =
            getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
        val vileplume =
            getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity().asExternalModel()
        val poisonPokemon = listOf(oddish, gloom, vileplume)

        val pokemonUiState = PokemonUiState.Success(poisonPokemon)
        testTypeScreenDisplaysSuccessSelectedTypeSuccessPokemonX(pokemonUiState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_typescreen_success_detail_sucess_pokemon_success)
            .assertIsDisplayed().onChildren().assertCountEquals(poisonPokemon.count())
    }

    private fun testTypeScreenDisplaysSuccessSelectedTypeSuccessPokemonX(pokemonUiState: PokemonUiState) {
        val iceType = getTypeRemote(TYPE_ICE_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        val types = listOf(iceType, grassType, poisonType)
        val fakeScreenState = TypeScreenUiState.Success(
            types, SelectedTypeUiState.Success(
                poisonType,
                pokemonUiState
            )
        )

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_typescreen_success_detail_success)
            .assertIsDisplayed()
    }

    private fun setupComposeTestRule(screenState: TypeScreenUiState) {
        composeTestRule.setContent {
            TypeScreenContent(
                screenState = screenState,
                searchType = { _ -> },
                updateFavourite = { _, _ -> },
                navigateToPokemon = { _ -> }
            )
        }
    }
}
