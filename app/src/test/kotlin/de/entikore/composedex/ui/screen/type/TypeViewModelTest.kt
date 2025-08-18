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
package de.entikore.composedex.ui.screen.type

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import de.entikore.composedex.MainCoroutineRule
import de.entikore.composedex.data.local.entity.type.asExternalModel
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.composedex.domain.usecase.GetPokemonOfTypeUseCase
import de.entikore.composedex.domain.usecase.GetTypeUseCase
import de.entikore.composedex.domain.usecase.GetTypesUseCase
import de.entikore.composedex.domain.usecase.SetAsFavouriteUseCase
import de.entikore.composedex.fake.repository.FakeTypeRepository
import de.entikore.composedex.fake.usecase.FakeSaveRemoteImageUseCase
import de.entikore.composedex.ui.screen.shared.PokemonUiState
import de.entikore.sharedtestcode.TYPE_GRASS_FILE
import de.entikore.sharedtestcode.TYPE_ICE_FILE
import de.entikore.sharedtestcode.TYPE_ICE_NAME
import de.entikore.sharedtestcode.TYPE_NORMAL_FILE
import de.entikore.sharedtestcode.TYPE_POISON_FILE
import de.entikore.sharedtestcode.TYPE_POISON_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTypeRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class TypeViewModelTest {

    private lateinit var typesUseCase: GetTypesUseCase
    private lateinit var typeUseCase: GetTypeUseCase
    private lateinit var getPokemonOfTypeUseCase: GetPokemonOfTypeUseCase
    private lateinit var saveRemoteImageUseCase: FakeSaveRemoteImageUseCase
    private lateinit var setAsFavouriteUseCase: SetAsFavouriteUseCase

    private lateinit var viewModel: TypeViewModel
    private val fakeTypeRepository = FakeTypeRepository()

    @BeforeEach
    fun setUp() {
        typesUseCase = GetTypesUseCase(fakeTypeRepository)
        typeUseCase = GetTypeUseCase(fakeTypeRepository)
        getPokemonOfTypeUseCase = GetPokemonOfTypeUseCase(fakeTypeRepository)
        saveRemoteImageUseCase = FakeSaveRemoteImageUseCase()
        setAsFavouriteUseCase = mock()
    }

    @Test
    fun `creating TypeViewModel exposes an empty Success TypeScreenUiState`() = runTest {
        viewModel = TypeViewModel(
            typesUseCase,
            typeUseCase,
            getPokemonOfTypeUseCase,
            saveRemoteImageUseCase,
            setAsFavouriteUseCase
        )

        val expectedState = TypeScreenUiState.Success()

        assertThat(viewModel.screenState.value).isInstanceOf(TypeScreenUiState.Success::class.java)
        assertThat(viewModel.screenState.value).isEqualTo(expectedState)
    }

    @Test
    fun `creating TypeViewModel exposes an Success TypeScreenUiState with all types`() = runTest {
        val iceType = getTypeRemote(TYPE_ICE_FILE).toEntity().asExternalModel()
        val normalType = getTypeRemote(TYPE_NORMAL_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        fakeTypeRepository.addTypes(iceType, normalType, grassType, poisonType)

        viewModel = TypeViewModel(
            typesUseCase,
            typeUseCase,
            getPokemonOfTypeUseCase,
            saveRemoteImageUseCase,
            setAsFavouriteUseCase
        )

        val expectedState = TypeScreenUiState.Success(
            types = listOf(iceType, normalType, grassType, poisonType),
            selectedType = SelectedTypeUiState.NoTypeSelected
        )

        viewModel.screenState.test {
            var stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(TypeScreenUiState.Success())

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(expectedState)
        }
    }

    @Test
    fun `search for a type exposes an Success TypeScreenUiState with expected type`() = runTest {
        val iceType = getTypeRemote(TYPE_ICE_FILE).toEntity().asExternalModel()
        val normalType = getTypeRemote(TYPE_NORMAL_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        fakeTypeRepository.addTypes(iceType, normalType, grassType, poisonType)

        viewModel = TypeViewModel(
            typesUseCase,
            typeUseCase,
            getPokemonOfTypeUseCase,
            saveRemoteImageUseCase,
            setAsFavouriteUseCase
        )

        val expectedState = TypeScreenUiState.Success(
            types = listOf(iceType, normalType, grassType, poisonType),
            selectedType = SelectedTypeUiState.NoTypeSelected
        )

        viewModel.screenState.test {
            var stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(TypeScreenUiState.Success())

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(expectedState)

            viewModel.fetchType(TYPE_ICE_NAME)
            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(
                expectedState.copy(
                    selectedType =
                    SelectedTypeUiState.Loading
                )
            )

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(
                expectedState.copy(
                    selectedType = SelectedTypeUiState.Success(
                        selectedType = iceType,
                        pokemonState = PokemonUiState.Success(emptyList()),
                        showLoadingItem = true
                    )
                )
            )
        }
    }

    @Test
    fun `search for an unknown type exposes an Error SelectedTypeUiState`() = runTest {
        val iceType = getTypeRemote(TYPE_ICE_FILE).toEntity().asExternalModel()
        fakeTypeRepository.addTypes(iceType)

        viewModel = TypeViewModel(
            typesUseCase,
            typeUseCase,
            getPokemonOfTypeUseCase,
            saveRemoteImageUseCase,
            setAsFavouriteUseCase
        )

        val expectedState = TypeScreenUiState.Success(
            types = listOf(iceType),
            selectedType = SelectedTypeUiState.NoTypeSelected
        )

        viewModel.screenState.test {
            var stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(TypeScreenUiState.Success())

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(expectedState)

            viewModel.fetchType(TYPE_POISON_NAME)
            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(
                expectedState.copy(
                    selectedType =
                    SelectedTypeUiState.Loading
                )
            )

            stateResult = awaitItem()
            assertThat(stateResult).isInstanceOf(TypeScreenUiState.Success::class.java)
            assertThat(stateResult).isEqualTo(
                expectedState.copy(
                    selectedType =
                    SelectedTypeUiState.Error
                )
            )
        }
    }
}
