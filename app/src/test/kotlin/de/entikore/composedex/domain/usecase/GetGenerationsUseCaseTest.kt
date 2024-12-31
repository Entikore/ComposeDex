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
import de.entikore.composedex.fake.repository.FailableFakeRepository.Companion.EXPECTED_TEST_EXCEPTION
import de.entikore.composedex.fake.repository.FakeGenerationRepository
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
class GetGenerationsUseCaseTest {

    private lateinit var repository: FakeGenerationRepository
    private lateinit var getGenerationsUseCase: GetGenerationsUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeGenerationRepository()
        getGenerationsUseCase = GetGenerationsUseCase(repository)
    }

    @Test
    fun `get generations results in WorkResult Success with all generations`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        repository.addGenerations(generationI, generationII, generationVI)

        getGenerationsUseCase().test {
            var allGenerationsResult = awaitItem()
            assertThat(allGenerationsResult).isInstanceOf(WorkResult.Loading::class.java)
            allGenerationsResult = awaitItem()
            assertThat(allGenerationsResult).isInstanceOf(WorkResult.Success::class.java)
            assertThat(
                (allGenerationsResult as WorkResult.Success).data
            ).containsExactly(generationI, generationII, generationVI)
            awaitComplete()
        }
    }

    @Test
    fun `get generations results in WorkResult Success with empty list when no generations are present`() = runTest {
        getGenerationsUseCase().test {
            var allGenerationsResult = awaitItem()
            assertThat(allGenerationsResult).isInstanceOf(WorkResult.Loading::class.java)
            allGenerationsResult = awaitItem()
            assertThat(allGenerationsResult).isInstanceOf(WorkResult.Success::class.java)
            assertThat((allGenerationsResult as WorkResult.Success).data).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `get generations results in WorkResult Error on any exception`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        repository.addGenerations(generationI, generationII, generationVI)
        repository.setReturnError(true)

        getGenerationsUseCase().test {
            var allGenerationsResult = awaitItem()
            assertThat(allGenerationsResult).isInstanceOf(WorkResult.Loading::class.java)
            allGenerationsResult = awaitItem()
            assertThat(allGenerationsResult).isInstanceOf(WorkResult.Error::class.java)
            assertThat((allGenerationsResult as WorkResult.Error).exception!!.message).isEqualTo(
                EXPECTED_TEST_EXCEPTION
            )
            awaitComplete()
        }
    }
}
