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
package de.entikore.composedex.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import de.entikore.composedex.MainCoroutineRule
import de.entikore.composedex.data.local.entity.generation.asExternalModel
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.remote.model.generation.toEntity
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.domain.WorkResult
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.fake.repository.FailableFakeRepository.Companion.EXPECTED_TEST_EXCEPTION
import de.entikore.composedex.fake.repository.FakeGenerationRepository
import de.entikore.sharedtestcode.GEN_II_FILE
import de.entikore.sharedtestcode.GEN_I_FILE
import de.entikore.sharedtestcode.POKEMON_BELLOSSOM_NAME
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

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class GetPokemonOfGenerationUseCaseTest {
    private lateinit var repository: FakeGenerationRepository
    private lateinit var getPokemonOfGenerationUseCase: GetPokemonOfGenerationUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeGenerationRepository()
        getPokemonOfGenerationUseCase = GetPokemonOfGenerationUseCase(repository)
    }

    @Test
    fun `get pokemon of generation by name results in WorkResult Success with all expected pokemon`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()

        val lapras = getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().asExternalModel()
        val ditto = getPokemonInfoRemote(getTestModel(POKEMON_DITTO_NAME)).toEntity().asExternalModel()
        val bellossom = getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity().asExternalModel()

        repository.addGenerations(generationI, generationII)
        repository.addPokemon(lapras, ditto, bellossom)

        val expectedGenIPokemon = listOf(lapras, ditto)
        val expectedGenIIPokemon = listOf(bellossom)

        getPokemonOfGenerationSuccessful(generationI.name, expectedGenIPokemon)
        getPokemonOfGenerationSuccessful(generationII.name, expectedGenIIPokemon)
    }

    @Test
    fun `get pokemon of generation by id results in WorkResult Success with all expected pokemon`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()

        val lapras = getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().asExternalModel()
        val ditto = getPokemonInfoRemote(getTestModel(POKEMON_DITTO_NAME)).toEntity().asExternalModel()
        val bellossom = getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity().asExternalModel()

        repository.addGenerations(generationI, generationII)
        repository.addPokemon(lapras, ditto, bellossom)

        val expectedGenIPokemon = listOf(lapras, ditto)
        val expectedGenIIPokemon = listOf(bellossom)

        getPokemonOfGenerationSuccessful(generationI.id.toString(), expectedGenIPokemon)
        getPokemonOfGenerationSuccessful(generationII.id.toString(), expectedGenIIPokemon)
    }

    @Test
    fun `get pokemon of generation results in WorkResult Success with empty list if no pokemon of generation`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val lapras = getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().asExternalModel()
        val ditto = getPokemonInfoRemote(getTestModel(POKEMON_DITTO_NAME)).toEntity().asExternalModel()

        repository.addGenerations(generationI, generationII)
        repository.addPokemon(lapras, ditto)

        getPokemonOfGenerationSuccessful(generationII.name, emptyList())
        getPokemonOfGenerationSuccessful(generationII.id.toString(), emptyList())
    }

    @Test
    fun `get pokemon of generation results in WorkResult Error on any exception`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val lapras = getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().asExternalModel()
        val ditto = getPokemonInfoRemote(getTestModel(POKEMON_DITTO_NAME)).toEntity().asExternalModel()

        repository.addGenerations(generationI)
        repository.addPokemon(lapras, ditto)
        repository.setReturnError(true)
        getPokemonOfGenerationUseCase(generationI.name).test {
            var actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Loading::class.java)
            actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Error::class.java)
            assertThat((actualPokemon as WorkResult.Error).exception!!.message).isEqualTo(
                EXPECTED_TEST_EXCEPTION
            )
            awaitComplete()
        }
    }
    private suspend fun getPokemonOfGenerationSuccessful(useCaseParam: String, expectedResult: List<Pokemon>) {
        getPokemonOfGenerationUseCase(useCaseParam).test {
            var actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Loading::class.java)
            actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Success::class.java)
            assertThat((actualPokemon as WorkResult.Success).data).isEqualTo(expectedResult)
            awaitComplete()
        }
    }
}
