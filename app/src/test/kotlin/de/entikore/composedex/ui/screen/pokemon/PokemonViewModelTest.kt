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

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.media3.exoplayer.ExoPlayer
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import de.entikore.composedex.MainCoroutineRule
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.usecase.GetPokemonUseCase
import de.entikore.composedex.domain.usecase.SetAsFavouriteUseCase
import de.entikore.composedex.domain.util.whitespacePattern
import de.entikore.composedex.fake.repository.FakePokemonRepository
import de.entikore.composedex.fake.usecase.FakeChangeThemeUseCase
import de.entikore.composedex.fake.usecase.FakeSaveRemoteCryUseCase
import de.entikore.composedex.fake.usecase.FakeSaveRemoteImageUseCase
import de.entikore.composedex.ui.ComposeDexTTS
import de.entikore.sharedtestcode.POKEMON_BELLOSSOM_NAME
import de.entikore.sharedtestcode.POKEMON_GLOOM_ID
import de.entikore.sharedtestcode.POKEMON_GLOOM_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.POKEMON_VILEPLUME_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class PokemonViewModelTest {

    private lateinit var viewModel: PokemonViewModel

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSavedStateHandle: SavedStateHandle

    @Mock
    private lateinit var mockPlayer: ExoPlayer

    @Mock
    private lateinit var mockTTS: ComposeDexTTS

    private lateinit var pokemonUseCase: GetPokemonUseCase
    private lateinit var saveRemoteImageUseCase: FakeSaveRemoteImageUseCase
    private lateinit var saveRemoteCryUseCase: FakeSaveRemoteCryUseCase
    private lateinit var setAsFavouriteUseCase: SetAsFavouriteUseCase
    private lateinit var changeThemeUseCase: FakeChangeThemeUseCase
    private lateinit var fakePokemonRepository: FakePokemonRepository

    @BeforeEach
    fun setUp() {
        fakePokemonRepository = FakePokemonRepository()
        mockContext = mock(Context::class.java)
        mockSavedStateHandle = mock(SavedStateHandle::class.java)
        mockPlayer = mock(ExoPlayer::class.java)
        mockTTS = mock(ComposeDexTTS::class.java)
        pokemonUseCase = GetPokemonUseCase(fakePokemonRepository)
        saveRemoteImageUseCase = FakeSaveRemoteImageUseCase()
        saveRemoteCryUseCase = FakeSaveRemoteCryUseCase()
        setAsFavouriteUseCase = mock()
        changeThemeUseCase = FakeChangeThemeUseCase()
    }

    @Test
    fun `creating PokemonDetailViewModel exposes an empty Success PokemonDetailScreenUiState`() =
        runTest {
            viewModel = PokemonViewModel(
                GetPokemonUseCase(fakePokemonRepository),
                saveRemoteImageUseCase,
                saveRemoteCryUseCase,
                setAsFavouriteUseCase,
                changeThemeUseCase,
                mockPlayer,
                mockTTS,
                mockSavedStateHandle
            )
            val expectedState = PokemonScreenState.NoPokemonSelected

            assertThat(viewModel.screenState.value).isEqualTo(expectedState)
        }

    @Test
    fun `creating PokemonDetailViewModel with SavedStateHandle exposes Success PokemonDetailScreenUiState with expected Pokemon`() =
        runTest {
            val expectedPokemon =
                getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
            val oddish =
                getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
            val vileplume = getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity()
                .asExternalModel()
            val bellossom = getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity()
                .asExternalModel()
            fakePokemonRepository.addPokemon(expectedPokemon, oddish, vileplume, bellossom)
            whenever(mockSavedStateHandle.get<String>(anyString())).thenReturn(POKEMON_GLOOM_NAME)

            viewModel = PokemonViewModel(
                GetPokemonUseCase(fakePokemonRepository),
                saveRemoteImageUseCase,
                saveRemoteCryUseCase,
                setAsFavouriteUseCase,
                changeThemeUseCase,
                mockPlayer,
                mockTTS,
                mockSavedStateHandle
            )

            val expectedState = PokemonScreenState.Success(
                selectedPokemon = expectedPokemon.processFlavourTextEntries(),
                evolvesFrom = oddish.toPokemonPreview(evolvesTo = false),
                evolvesTo = listOf(
                    vileplume.toPokemonPreview(true),
                    bellossom.toPokemonPreview(true)
                ),
                varieties = listOf(expectedPokemon.processFlavourTextEntries())
            )

            viewModel.screenState.test {
                var stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonScreenState.NoPokemonSelected::class.java)

                stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonScreenState.Loading::class.java)

                stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonScreenState.Success::class.java)
                assertThat(stateResult).isEqualTo(
                    expectedState
                )
            }
        }

    @Test
    fun `creating PokemonDetailViewModel with unknown SavedStateHandle exposes Error PokemonDetailScreenUiState`() =
        runTest {
            whenever(mockSavedStateHandle.get<String>(anyString())).thenReturn(POKEMON_GLOOM_NAME)

            viewModel = PokemonViewModel(
                GetPokemonUseCase(fakePokemonRepository),
                saveRemoteImageUseCase,
                saveRemoteCryUseCase,
                setAsFavouriteUseCase,
                changeThemeUseCase,
                mockPlayer,
                mockTTS,
                mockSavedStateHandle
            )
            val expectedState =
                PokemonScreenState.Error("${PokemonViewModel.Companion.ERROR_LOADING_POKEMON} $POKEMON_GLOOM_NAME")

            viewModel.screenState.test {
                var stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonScreenState.NoPokemonSelected::class.java)

                stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonScreenState.Loading::class.java)

                stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonScreenState.Error::class.java)
                assertThat(stateResult).isEqualTo(
                    expectedState
                )
            }
        }

    @ParameterizedTest
    @MethodSource("searchTerms")
    fun `search for pokemon exposes Success PokemonDetailScreenUiState with expected Pokemon`(
        searchQuery: String,
        testData: Array<Pokemon>,
        expectedState: PokemonScreenState.Success
    ) = runTest {
        fakePokemonRepository.addPokemon(*testData)

        viewModel = PokemonViewModel(
            GetPokemonUseCase(fakePokemonRepository),
            saveRemoteImageUseCase,
            saveRemoteCryUseCase,
            setAsFavouriteUseCase,
            changeThemeUseCase,
            mockPlayer,
            mockTTS,
            mockSavedStateHandle
        )

        viewModel.screenState.test {
            var stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(PokemonScreenState.NoPokemonSelected::class.java)

            viewModel.lookUpPokemon(searchQuery)

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(PokemonScreenState.Loading::class.java)

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(PokemonScreenState.Success::class.java)
            assertThat(stateResult).isEqualTo(
                expectedState
            )
        }
    }

    @Test
    fun `search for unknown pokemon exposes Error PokemonDetailScreenUiState`() =
        runTest {
            viewModel = PokemonViewModel(
                GetPokemonUseCase(fakePokemonRepository),
                saveRemoteImageUseCase,
                saveRemoteCryUseCase,
                setAsFavouriteUseCase,
                changeThemeUseCase,
                mockPlayer,
                mockTTS,
                mockSavedStateHandle
            )

            val expectedState = PokemonScreenState.Error(
                "${PokemonViewModel.Companion.ERROR_LOADING_POKEMON} $POKEMON_GLOOM_NAME"
            )

            viewModel.screenState.test {
                var stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonScreenState.NoPokemonSelected::class.java)

                viewModel.lookUpPokemon(POKEMON_GLOOM_NAME)

                stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonScreenState.Loading::class.java)

                stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonScreenState.Error::class.java)
                assertThat(stateResult).isEqualTo(
                    expectedState
                )
            }
        }

    companion object {
        @JvmStatic
        fun searchTerms(): List<Arguments> {
            val oddish = getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
            val gloom = getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
            val vileplume = getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity().asExternalModel()
            val bellossom = getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity().asExternalModel()

            val expectedState = PokemonScreenState.Success(
                selectedPokemon = gloom.processFlavourTextEntries(),
                evolvesFrom = oddish.toPokemonPreview(false),
                evolvesTo = listOf(vileplume.toPokemonPreview(true), bellossom.toPokemonPreview(true)),
                varieties = listOf(gloom.processFlavourTextEntries())
            )

            return listOf(
                Arguments.of(
                    POKEMON_GLOOM_NAME,
                    arrayOf(
                        oddish,
                        gloom,
                        vileplume,
                        bellossom
                    ),
                    expectedState
                ),
                Arguments.of(
                    POKEMON_GLOOM_ID.toString(),
                    arrayOf(
                        oddish,
                        gloom,
                        vileplume,
                        bellossom
                    ),
                    expectedState
                ),
            )
        }

        /**
         * Copy from [GetPokemonUseCase.processFlavorTextEntries].
         */
        private fun Pokemon.processFlavourTextEntries(): Pokemon =
            this.copy(
                textEntries = textEntries.map {
                    it.replace(whitespacePattern, " ").trim()
                }.distinctBy { it.lowercase() }
            )
    }
}
