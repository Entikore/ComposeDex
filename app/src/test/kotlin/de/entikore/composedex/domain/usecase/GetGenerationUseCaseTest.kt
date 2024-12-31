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
import de.entikore.composedex.data.remote.model.generation.toEntity
import de.entikore.composedex.domain.WorkResult
import de.entikore.composedex.domain.model.generation.Generation
import de.entikore.composedex.fake.repository.FailableFakeRepository.Companion.EXPECTED_TEST_EXCEPTION
import de.entikore.composedex.fake.repository.FakeGenerationRepository
import de.entikore.composedex.fake.repository.FakeGenerationRepository.Companion.GENERATION_WITH_NAME_NOT_FOUND
import de.entikore.sharedtestcode.GEN_II_FILE
import de.entikore.sharedtestcode.GEN_I_FILE
import de.entikore.sharedtestcode.GEN_VI_FILE
import de.entikore.sharedtestcode.TestModelFactory.Companion.getGenerationRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class GetGenerationUseCaseTest {
    private lateinit var repository: FakeGenerationRepository
    private lateinit var getGenerationUseCase: GetGenerationUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeGenerationRepository()
        getGenerationUseCase = GetGenerationUseCase(repository)
    }

    @Test
    fun `get generation by name results in WorkResult Success with searched generation`() =
        runTest {
            val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
            val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
            val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
            getGenerationByXSuccessful(
                expectedGeneration = generationI,
                useCaseParam = generationI.name,
                generationI,
                generationII,
                generationVI
            )
        }

    @Test
    fun `get generation by id results in WorkResult Success with searched generation`() =
        runTest {
            val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
            val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
            val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
            getGenerationByXSuccessful(
                expectedGeneration = generationVI,
                useCaseParam = generationVI.id.toString(),
                generationI,
                generationII,
                generationVI
            )
        }

    private suspend fun getGenerationByXSuccessful(
        expectedGeneration: Generation,
        useCaseParam: String,
        vararg testData: Generation
    ) {
        repository.addGenerations(*testData)

        getGenerationUseCase.invoke(useCaseParam).test {
            var actualGeneration = awaitItem()
            assertThat(actualGeneration).isInstanceOf(WorkResult.Loading::class.java)
            actualGeneration = awaitItem()
            assertThat(actualGeneration).isInstanceOf(WorkResult.Success::class.java)
            assertThat((actualGeneration as WorkResult.Success).data).isEqualTo(expectedGeneration)
            awaitComplete()
        }
    }

    @Test
    fun `get generation by unknown name results in WorkResult Error`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        repository.addGenerations(generationI, generationII, generationVI)

        getGenerationUseCase.invoke("unknown").test {
            var actualGeneration = awaitItem()
            assertThat(actualGeneration).isInstanceOf(WorkResult.Loading::class.java)
            actualGeneration = awaitItem()
            assertThat(actualGeneration).isInstanceOf(WorkResult.Error::class.java)
            assertThat((actualGeneration as WorkResult.Error).exception!!.message).isEqualTo(
                GENERATION_WITH_NAME_NOT_FOUND
            )
            awaitComplete()
        }
    }

    @Test
    fun `get generation by name results in WorkResult Error on any exception`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()

        getGenerationByXException(
            useCaseParam = generationI.name,
            generationI,
            generationII,
            generationVI
        )
    }

    @Test
    fun `get generation by id results in WorkResult Error on any exception`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()

        getGenerationByXException(
            useCaseParam = generationII.id.toString(),
            generationI,
            generationII,
            generationVI
        )
    }

    private suspend fun getGenerationByXException(
        useCaseParam: String,
        vararg testData: Generation
    ) {
        repository.addGenerations(*testData)
        repository.setReturnError(true)

        getGenerationUseCase.invoke(useCaseParam).test {
            var actualGeneration = awaitItem()
            assertThat(actualGeneration).isInstanceOf(WorkResult.Loading::class.java)
            actualGeneration = awaitItem()
            assertThat(actualGeneration).isInstanceOf(WorkResult.Error::class.java)
            assertThat((actualGeneration as WorkResult.Error).exception!!.message).isEqualTo(
                EXPECTED_TEST_EXCEPTION
            )
            awaitComplete()
        }
    }
}
