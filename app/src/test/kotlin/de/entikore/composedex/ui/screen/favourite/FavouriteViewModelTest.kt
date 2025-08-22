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
package de.entikore.composedex.ui.screen.favourite

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import de.entikore.composedex.MainCoroutineRule
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.domain.usecase.FetchFavouritesUseCase
import de.entikore.composedex.domain.usecase.SetAsFavouriteUseCase
import de.entikore.composedex.fake.repository.FakeFavouriteRepository
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.sharedtestcode.POKEMON_DITTO_NAME
import de.entikore.sharedtestcode.POKEMON_LAPRAS_ID
import de.entikore.sharedtestcode.POKEMON_LAPRAS_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class FavouriteViewModelTest {

    private lateinit var viewModel: FavouriteViewModel

    private lateinit var getFavouritesUseCase: FetchFavouritesUseCase
    private lateinit var setAsFavouriteUseCase: SetAsFavouriteUseCase
    private lateinit var fakeFavouriteRepository: FakeFavouriteRepository

    @BeforeEach
    fun setup() {
        fakeFavouriteRepository = FakeFavouriteRepository()
        getFavouritesUseCase = FetchFavouritesUseCase(fakeFavouriteRepository)
        setAsFavouriteUseCase = SetAsFavouriteUseCase(fakeFavouriteRepository)
    }

    @Test
    fun `creating FavouriteViewModel without favourite Pokemon exposes PokemonUiState loading`() =
        runTest {
            viewModel = FavouriteViewModel(getFavouritesUseCase, setAsFavouriteUseCase)

            assertThat(viewModel.screenState.value).isEqualTo(PokemonUiState.Loading)
        }

    @Test
    fun `FavouriteViewModel exposes success state when getting favourites are loaded correctly`() =
        runTest {
            val lapras =
                getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().asExternalModel()
                    .copy(isFavourite = true)
            val ditto =
                getPokemonInfoRemote(getTestModel(POKEMON_DITTO_NAME)).toEntity().asExternalModel()
                    .copy(isFavourite = true)
            viewModel = FavouriteViewModel(getFavouritesUseCase, setAsFavouriteUseCase)

            viewModel.screenState.test {
                var stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonUiState.Loading::class.java)

                stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonUiState.Success::class.java)
                assertThat((stateResult as PokemonUiState.Success).pokemon).isEmpty()

                fakeFavouriteRepository.addPokemon(
                    lapras,
                    ditto
                )

                stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonUiState.Success::class.java)
                assertThat((stateResult as PokemonUiState.Success).pokemon).isEqualTo(
                    listOf(lapras, ditto)
                )

                viewModel.updateFavourite(POKEMON_LAPRAS_ID, false)

                stateResult = awaitItem()
                assertThat(stateResult).isInstanceOf(PokemonUiState.Success::class.java)
                assertThat((stateResult as PokemonUiState.Success).pokemon).isEqualTo(
                    listOf(ditto)
                )
            }
        }

    @Test
    fun `FavouriteViewModel exposes error state when getting favourites fails`() = runTest {
        val lapras =
            getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().asExternalModel()
                .copy(isFavourite = true)
        val ditto =
            getPokemonInfoRemote(getTestModel(POKEMON_DITTO_NAME)).toEntity().asExternalModel()
                .copy(isFavourite = true)
        fakeFavouriteRepository.shouldReturnError = true

        fakeFavouriteRepository.addPokemon(
            lapras,
            ditto
        )

        viewModel = FavouriteViewModel(getFavouritesUseCase, setAsFavouriteUseCase)

        viewModel.screenState.test {
            var stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(PokemonUiState.Loading::class.java)

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(PokemonUiState.Error::class.java)
        }
    }
}
