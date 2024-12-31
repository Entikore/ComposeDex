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
import de.entikore.composedex.data.local.entity.pokemon.relation.asExternalModel
import de.entikore.composedex.data.local.entity.type.asExternalModel
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.composedex.domain.WorkResult
import de.entikore.composedex.fake.repository.FailableFakeRepository.Companion.EXPECTED_TEST_EXCEPTION
import de.entikore.composedex.fake.repository.FakeTypeRepository
import de.entikore.sharedtestcode.POKEMON_BELLOSSOM_NAME
import de.entikore.sharedtestcode.POKEMON_GLOOM_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.POKEMON_VILEPLUME_NAME
import de.entikore.sharedtestcode.TYPE_GRASS_FILE
import de.entikore.sharedtestcode.TYPE_ICE_FILE
import de.entikore.sharedtestcode.TYPE_POISON_FILE
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTypeRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class GetPokemonOfTypeUseCaseTest {
    private lateinit var repository: FakeTypeRepository
    private lateinit var getPokemonOfTypesUseCase: GetPokemonOfTypeUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeTypeRepository()
        getPokemonOfTypesUseCase = GetPokemonOfTypeUseCase(repository)
    }

    @Test
    fun `get pokemon of types results in WorkResult Success with all expected pokemon`() = runTest {
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()

        val oddish = getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
        val gloom = getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
        val vileplume = getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity().asExternalModel()
        val bellossom = getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity().asExternalModel()

        repository.addTypes(poisonType, grassType)
        repository.addPokemon(oddish, gloom, vileplume, bellossom)

        val expectedPoisonPokemon = listOf(oddish, gloom, vileplume)
        val expectedGrassPokemon = expectedPoisonPokemon.plus(bellossom)

        getPokemonOfTypesUseCase(poisonType.name).test {
            var actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Loading::class.java)
            actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Success::class.java)
            assertThat((actualPokemon as WorkResult.Success).data).isEqualTo(expectedPoisonPokemon)
            awaitComplete()
        }
        getPokemonOfTypesUseCase(grassType.name).test {
            var actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Loading::class.java)
            actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Success::class.java)
            assertThat((actualPokemon as WorkResult.Success).data).isEqualTo(expectedGrassPokemon)
            awaitComplete()
        }
    }

    @Test
    fun `get pokemon of types results in WorkResult Success with empty list if no pokemon of type`() = runTest {
        val iceType = getTypeRemote(TYPE_ICE_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()

        val oddish = getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel()
        val gloom = getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().asExternalModel()
        val vileplume = getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity().asExternalModel()
        val bellossom = getPokemonInfoRemote(getTestModel(POKEMON_BELLOSSOM_NAME)).toEntity().asExternalModel()

        repository.addTypes(iceType, grassType)
        repository.addPokemon(oddish, gloom, vileplume, bellossom)

        getPokemonOfTypesUseCase(iceType.name).test {
            var actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Loading::class.java)
            actualPokemon = awaitItem()
            assertThat(actualPokemon).isInstanceOf(WorkResult.Success::class.java)
            assertThat((actualPokemon as WorkResult.Success).data).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `get pokemon of types results in WorkResult Error on any exception`() = runTest {
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        repository.addTypes(getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel())
        repository.addPokemon(getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().asExternalModel())
        repository.setReturnError(true)
        getPokemonOfTypesUseCase(grassType.name).test {
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
}
