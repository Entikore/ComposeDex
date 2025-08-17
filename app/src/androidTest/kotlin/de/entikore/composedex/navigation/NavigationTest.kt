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
package de.entikore.composedex.navigation

import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.entikore.composedex.ComposeDexAppState
import de.entikore.composedex.MainActivity
import de.entikore.composedex.R
import de.entikore.composedex.onNodeWithTagStringId
import de.entikore.composedex.ui.navigation.DrawerNavHost
import de.entikore.composedex.ui.navigation.destination.ComposeDexDestination
import de.entikore.composedex.ui.navigation.destination.FavouriteDestination
import de.entikore.composedex.ui.navigation.destination.GenerationDestination
import de.entikore.composedex.ui.navigation.destination.PokemonDestination
import de.entikore.composedex.ui.navigation.destination.SettingsDestination
import de.entikore.composedex.ui.navigation.destination.TypeDestination
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setupNavGraph() {
        hiltRule.inject()
        composeTestRule.activity.setContent {
            val appState = ComposeDexAppState(
                snackbarHostState = remember { SnackbarHostState() },
                scope = rememberCoroutineScope(),
                drawerState = rememberDrawerState(DrawerValue.Closed)
            )
            DrawerNavHost(
                drawerState = appState.drawerState,
                snackBarHostState = appState.snackbarHostState,
                changeDrawerState = appState::changeDrawerState,
                showSnackbar = appState::showSnackbar
            )
        }
    }

    @Test
    fun navGraph_verifyStartDestination() {
        composeTestRule
            .onNodeWithTagStringId(R.string.test_tag_compose_dex_destination_pokemon)
            .assertIsDisplayed()
    }

    @Test
    fun navGraph_clickOnDrawerPokemon_navigateToPokemonScreen() {
        navigateToDrawerScreen(
            FavouriteDestination(),
            R.string.test_tag_compose_dex_destination_favourite
        )
        navigateToDrawerScreen(
            PokemonDestination(),
            R.string.test_tag_compose_dex_destination_pokemon
        )
    }

    @Test
    fun navGraph_clickOnDrawerFavourite_navigateToFavouriteScreen() {
        navigateToDrawerScreen(
            FavouriteDestination(),
            R.string.test_tag_compose_dex_destination_favourite
        )
    }

    @Test
    fun navGraph_clickOnDrawerGeneration_navigateToGenerationScreen() {
        navigateToDrawerScreen(
            GenerationDestination(),
            R.string.test_tag_compose_dex_destination_generation
        )
    }

    @Test
    fun navGraph_clickOnDrawerType_navigateToTypeScreen() {
        navigateToDrawerScreen(TypeDestination(), R.string.test_tag_compose_dex_destination_type)
    }

    @Test
    fun navGraph_clickOnDrawerSettings_navigateToSettingsScreen() {
        navigateToDrawerScreen(
            SettingsDestination(),
            R.string.test_tag_compose_dex_destination_settings
        )
    }

    private fun navigateToDrawerScreen(
        screen: ComposeDexDestination,
        testTagId: Int
    ) {
        composeTestRule.onNodeWithTagStringId(R.string.test_tag_open_drawer).performClick()
        composeTestRule.onNodeWithText(screen.uiName).performClick()
        composeTestRule
            .onNodeWithTagStringId(testTagId)
            .assertIsDisplayed()
    }
}
