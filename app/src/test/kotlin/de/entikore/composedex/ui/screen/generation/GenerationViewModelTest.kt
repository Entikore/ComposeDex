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

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import de.entikore.composedex.MainCoroutineRule
import de.entikore.composedex.data.local.entity.generation.asExternalModel
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.model.generation.toEntity
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.domain.usecase.GetGenerationUseCase
import de.entikore.composedex.domain.usecase.GetGenerationsUseCase
import de.entikore.composedex.domain.usecase.GetPokemonOfGenerationUseCase
import de.entikore.composedex.domain.usecase.SetAsFavouriteUseCase
import de.entikore.composedex.fake.repository.FakeGenerationRepository
import de.entikore.composedex.fake.usecase.FakeSaveRemoteImageUseCase
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.sharedtestcode.GEN_II_FILE
import de.entikore.sharedtestcode.GEN_II_NAME
import de.entikore.sharedtestcode.GEN_I_FILE
import de.entikore.sharedtestcode.GEN_VI_FILE
import de.entikore.sharedtestcode.POKEMON_DITTO_NAME
import de.entikore.sharedtestcode.POKEMON_LAPRAS_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getGenerationRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class GenerationViewModelTest {

    private lateinit var getGenerationsUseCase: GetGenerationsUseCase
    private lateinit var getGenerationUseCase: GetGenerationUseCase
    private lateinit var getPokemonOfGenerationUseCase: GetPokemonOfGenerationUseCase
    private lateinit var saveRemoteImageUseCase: FakeSaveRemoteImageUseCase
    private lateinit var setAsFavouriteUseCase: SetAsFavouriteUseCase

    private lateinit var viewModel: GenerationViewModel
    private val fakeGenerationRepository = FakeGenerationRepository()

    @BeforeEach
    fun setUp() {
        getGenerationsUseCase = GetGenerationsUseCase(fakeGenerationRepository)
        getGenerationUseCase = GetGenerationUseCase(fakeGenerationRepository)
        getPokemonOfGenerationUseCase = GetPokemonOfGenerationUseCase(fakeGenerationRepository)
        saveRemoteImageUseCase = FakeSaveRemoteImageUseCase()
        setAsFavouriteUseCase = mock()
    }

    @Test
    fun `creating GenerationViewModel exposes an empty Success GenerationScreenUiState`() = runTest {
        viewModel = GenerationViewModel(
            getGenerationsUseCase,
            getGenerationUseCase,
            getPokemonOfGenerationUseCase,
            saveRemoteImageUseCase,
            setAsFavouriteUseCase
        )

        val expectedState = GenerationScreenUiState.Success()

        assertThat(viewModel.screenState.value).isInstanceOf(GenerationScreenUiState.Success::class.java)
        assertThat(viewModel.screenState.value).isEqualTo(expectedState)
    }

    @Test
    fun `creating GenerationViewModel exposes an Success GenerationScreenUiState with all generations`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        fakeGenerationRepository.addGenerations(generationI, generationII, generationVI)

        viewModel = GenerationViewModel(
            getGenerationsUseCase,
            getGenerationUseCase,
            getPokemonOfGenerationUseCase,
            saveRemoteImageUseCase,
            setAsFavouriteUseCase
        )

        val expectedState = GenerationScreenUiState.Success(
            generations = listOf(generationI, generationII, generationVI),
            selectedGeneration = SelectedGenerationUiState.NoGenerationSelected
        )

        viewModel.screenState.test {
            var stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(GenerationScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(GenerationScreenUiState.Success())

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(GenerationScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(expectedState)
        }
    }

    @Test
    fun `search for a generation by name exposes Success SelectedGenerationUiState with expected Generation`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val lapras = getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().asExternalModel()
        val ditto = getPokemonInfoRemote(getTestModel(POKEMON_DITTO_NAME)).toEntity().asExternalModel()
        fakeGenerationRepository.addGenerations(generationI)
        fakeGenerationRepository.addPokemon(lapras, ditto)

        viewModel = GenerationViewModel(
            getGenerationsUseCase,
            getGenerationUseCase,
            getPokemonOfGenerationUseCase,
            saveRemoteImageUseCase,
            setAsFavouriteUseCase
        )

        val expectedState = GenerationScreenUiState.Success(
            generations = listOf(generationI),
            selectedGeneration = SelectedGenerationUiState.Success(
                selectedGeneration = generationI,
                pokemonState = PokemonUiState.Success(listOf(lapras, ditto)),
                showLoadingItem = true
            )
        )

        viewModel.screenState.test {
            var stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(GenerationScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(GenerationScreenUiState.Success())

            viewModel.searchForGeneration(generationI.id.toString())

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(GenerationScreenUiState.Success::class.java)
            assertThat(
                stateResult
            ).isEqualTo(expectedState.copy(selectedGeneration = SelectedGenerationUiState.Loading))

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(GenerationScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(expectedState)
        }
    }

    @Test
    fun `search for an unknown generation by name exposes Error SelectedGenerationUiState`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        fakeGenerationRepository.addGenerations(generationI)

        viewModel = GenerationViewModel(
            getGenerationsUseCase,
            getGenerationUseCase,
            getPokemonOfGenerationUseCase,
            saveRemoteImageUseCase,
            setAsFavouriteUseCase
        )

        val expectedState = GenerationScreenUiState.Success(
            generations = listOf(generationI),
            selectedGeneration = SelectedGenerationUiState.Error
        )

        viewModel.screenState.test {
            var stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(GenerationScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(GenerationScreenUiState.Success())

            viewModel.searchForGeneration(GEN_II_NAME)

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(GenerationScreenUiState.Success::class.java)
            assertThat(
                stateResult
            ).isEqualTo(expectedState.copy(selectedGeneration = SelectedGenerationUiState.Loading))

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(GenerationScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(expectedState)
        }
    }
}
