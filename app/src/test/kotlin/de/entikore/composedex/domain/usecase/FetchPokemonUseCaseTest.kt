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
package de.entikore.composedex.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import de.entikore.composedex.MainCoroutineRule
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.fake.repository.FailableFakeRepository.Companion.EXPECTED_TEST_EXCEPTION
import de.entikore.composedex.fake.repository.FakePokemonRepository
import de.entikore.composedex.fake.repository.FakePokemonRepository.Companion.POKEMON_WITH_ID_NOT_FOUND
import de.entikore.composedex.fake.repository.FakePokemonRepository.Companion.POKEMON_WITH_NAME_NOT_FOUND
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

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class FetchPokemonUseCaseTest {
    private lateinit var repository: FakePokemonRepository
    private lateinit var getPokemonUseCase: FetchPokemonUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePokemonRepository()
        getPokemonUseCase = FetchPokemonUseCase(repository)
    }

    @Test
    fun `get pokemon by name results in successful Result with expected Pokemon`() = runTest {
        val oddish =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
        val gloom =
            getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
        val vileplume =
            getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity().asExternalModel()
        val bellossom =
            getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity().asExternalModel()

        getPokemonByXSuccessful(
            expectedPokemon = gloom,
            useCaseParam = gloom.name,
            oddish,
            gloom,
            vileplume,
            bellossom
        )
    }

    @Test
    fun `get pokemon by id results in successful Result with expected Pokemon`() = runTest {
        val oddish =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
        val gloom =
            getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
        val vileplume =
            getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity().asExternalModel()
        val bellossom =
            getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity().asExternalModel()

        getPokemonByXSuccessful(
            expectedPokemon = gloom,
            useCaseParam = gloom.id.toString(),
            oddish,
            gloom,
            vileplume,
            bellossom
        )
    }

    @Test
    fun `get pokemon by unknown name results in error Result`() = runTest {
        val oddish =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()

        getPokemonByXFailure(
            shouldReturnError = false,
            expectedException = POKEMON_WITH_NAME_NOT_FOUND,
            useCaseParam = POKEMON_GLOOM_NAME,
            oddish
        )
    }

    @Test
    fun `get pokemon by unknown id results in error Result`() = runTest {
        val oddish =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()

        getPokemonByXFailure(
            shouldReturnError = false,
            expectedException = POKEMON_WITH_ID_NOT_FOUND,
            useCaseParam = POKEMON_GLOOM_ID.toString(),
            oddish
        )
    }

    @Test
    fun `get pokemon by name results in error Result on any exception`() = runTest {
        val oddish =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()

        getPokemonByXFailure(
            shouldReturnError = true,
            expectedException = EXPECTED_TEST_EXCEPTION,
            useCaseParam = oddish.name,
            oddish
        )
    }

    @Test
    fun `get pokemon by id results in error Result on any exception`() = runTest {
        val oddish =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()

        getPokemonByXFailure(
            shouldReturnError = true,
            expectedException = EXPECTED_TEST_EXCEPTION,
            useCaseParam = oddish.id.toString(),
            oddish
        )
    }

    private suspend fun getPokemonByXSuccessful(
        expectedPokemon: Pokemon,
        useCaseParam: String,
        vararg testData: Pokemon
    ) {
        repository.addPokemon(*testData)

        getPokemonUseCase.invoke(useCaseParam).test {
            val actualPokemon = awaitItem()
            assertThat(actualPokemon.isSuccess).isTrue()
            assertThat(actualPokemon.getOrThrow().name).isEqualTo(expectedPokemon.name)
            assertThat(actualPokemon.getOrThrow().id).isEqualTo(expectedPokemon.id)
            awaitComplete()
        }
    }

    private suspend fun getPokemonByXFailure(
        shouldReturnError: Boolean,
        expectedException: String,
        useCaseParam: String,
        vararg testData: Pokemon
    ) {
        repository.addPokemon(*testData)
        repository.setReturnError(shouldReturnError)

        getPokemonUseCase.invoke(useCaseParam).test {
            val actualPokemon = awaitItem()
            assertThat(actualPokemon.isFailure).isTrue()
            assertThat(actualPokemon.exceptionOrNull()?.message).isEqualTo(
                expectedException
            )
            awaitComplete()
        }
    }
}
