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

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeUp
import de.entikore.composedex.R
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.hasClickLabel
import de.entikore.composedex.hasContentDescriptionStringId
import de.entikore.composedex.hasTestTagStringId
import de.entikore.composedex.onNodeWithTagStringId
import de.entikore.sharedtestcode.POKEMON_BELLOSSOM_NAME
import de.entikore.sharedtestcode.POKEMON_GLOOM_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.POKEMON_VILEPLUME_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import org.junit.Rule
import org.junit.Test

class PokemonScreenKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun test_PokemonScreen_displays_NoPokemonSelected() {
        val fakeScreenState = PokemonScreenState.NoPokemonSelected

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemonscreenstate_nopokemonselected)
            .assertIsDisplayed()
    }

    @Test
    fun test_PokemonScreen_displays_Loading() {
        val fakeScreenState = PokemonScreenState.Loading

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemonscreenstate_loading)
            .assertIsDisplayed()
    }

    @Test
    fun test_PokemonScreen_displays_Error() {
        val expectedErrorMessage = "Expected unit test error"
        val fakeScreenState = PokemonScreenState.Error(expectedErrorMessage)

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemonscreenstate_error)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedErrorMessage).assertIsDisplayed()
    }

    @Test
    fun test_PokemonScreen_displays_Success() {
        val selectedPokemon =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
        val evolvesTo =
            getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
                .toPokemonPreview(evolvesTo = true)
        val selectedType = selectedPokemon.types.first()
        val fakeScreenState = PokemonScreenState.Success(
            selectedPokemon = selectedPokemon,
            displayedEvolution = "Evolves to $POKEMON_GLOOM_NAME",
            evolvesTo = listOf(evolvesTo),
            selectedType = selectedType
        )

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemonscreenstate_success)
            .assertIsDisplayed()

        // check top part of the screen
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemonheader)
            .assertIsDisplayed()

        // check center of the screen
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemonpicture)
            .assertIsDisplayed()

        // check PokemonInformation
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemoninfo)
            .assertIsDisplayed()

        // check bottom part of the screen
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemonweaknessandresistance)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_weakness).assertIsDisplayed()

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_weakness).performTouchInput {
            swipeLeft()
        }
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodes(composeTestRule.hasClickLabel(R.string.label_type_icon_click))
            .filter(
                hasAnyAncestor(composeTestRule.hasTestTagStringId(R.string.test_tag_weakness))
            ).assertCountEquals(selectedType.doubleDamageFrom.size)
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_resistance).assertIsDisplayed()
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_resistance).performTouchInput {
            swipeLeft()
        }
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodes(composeTestRule.hasClickLabel(R.string.label_type_icon_click))
            .filter(
                hasAnyAncestor(composeTestRule.hasTestTagStringId(R.string.test_tag_resistance))
            ).assertCountEquals(selectedType.halfDamageFrom.size)
    }

    @Test
    fun test_PokemonScreen_Success_switchBetweenEvolvesFromAndEvolvesTo() {
        val oddish =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
                .toPokemonPreview(evolvesTo = false).copy(isLoading = false)
        val gloom =
            getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
        val vileplume =
            getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity().asExternalModel()
                .toPokemonPreview(evolvesTo = true).copy(isLoading = false)
        val bellossom =
            getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity().asExternalModel()
                .toPokemonPreview(evolvesTo = true).copy(isLoading = false)
        val fakeScreenState = PokemonScreenState.Success(
            selectedPokemon = gloom,
            displayedEvolution = "Evolves from ${oddish.name}",
            evolvesFrom = oddish,
            evolvesTo = listOf(vileplume, bellossom)
        )

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemonheader)
            .assertIsDisplayed()

        // assert evolvesFrom is displayed
        composeTestRule.onNodeWithContentDescription(
            oddish.name,
            substring = true,
            ignoreCase = true
        ).assertIsDisplayed()

        // swipe to evolvesTo
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_stage1pokemon).assertIsDisplayed()
            .performTouchInput { swipeLeft() }

        // assert evolvesTo is displayed
        composeTestRule.onNodeWithContentDescription(
            vileplume.name,
            substring = true,
            ignoreCase = true
        ).assertIsDisplayed()

        // swipe to other evolvesTo
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_evolvestopager).assertIsDisplayed()
            .performTouchInput { swipeUp() }

        // assert other evolvesTo is displayed
        composeTestRule.onNodeWithContentDescription(
            bellossom.name,
            substring = true,
            ignoreCase = true
        ).assertIsDisplayed()

    }

    @Test
    fun test_PokemonScreen_Success_switchBetweenTextEntries() {
        val gloom =
            getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
        val fakeScreenState = PokemonScreenState.Success(
            selectedPokemon = gloom
        )

        setupComposeTestRule(fakeScreenState)

        composeTestRule.onNodeWithTagStringId(R.string.test_tag_pokemoninfo)
            .assertIsDisplayed()

        // assert the correct text entry and counter is displayed
        composeTestRule.onNode(composeTestRule.hasContentDescriptionStringId(
            id = R.string.semantics_displays_the_text_information_of_the_pokemon,
            args = arrayOf(gloom.name)
        )).assertIsDisplayed()

        composeTestRule.onNodeWithText("1 of ${gloom.textEntries.size}").assertExists()

        // swipe to the next text entry and assert the correct counter is displayed
        composeTestRule.onNode(composeTestRule.hasContentDescriptionStringId(
            id = R.string.semantics_displays_the_text_information_of_the_pokemon,
            args = arrayOf(gloom.name)
        )).assertIsDisplayed()
            .performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithText("2 of ${gloom.textEntries.size}").assertExists()

        // swipe to the last text entry and assert the correct counter is displayed
        composeTestRule.onNode(composeTestRule.hasContentDescriptionStringId(
            id = R.string.semantics_displays_the_text_information_of_the_pokemon,
            args = arrayOf(gloom.name)
        )).assertIsDisplayed()
            .performTouchInput { for (i in 3..gloom.textEntries.size) swipeLeft() }

        composeTestRule.onNodeWithText("${gloom.textEntries.size} of ${gloom.textEntries.size}").assertExists()
    }

    private fun setupComposeTestRule(screenState: PokemonScreenState) {
        composeTestRule.setContent {
            PokemonScreen(
                screenState = screenState,
                openDrawer = {},
                navigateToTypes = { _ -> },
                lookUpPokemon = { _ -> },
                selectVariety = { _ -> },
                selectType = {},
                changeEvolutionText = {},
                updateFavourite = { _, _ -> },
                playSound = {},
                speakTextEntry = {}
            )
        }
    }
}
