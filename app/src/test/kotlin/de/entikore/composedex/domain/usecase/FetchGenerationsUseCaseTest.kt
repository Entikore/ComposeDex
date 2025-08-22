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
import de.entikore.composedex.data.local.entity.generation.asExternalModel
import de.entikore.composedex.data.remote.model.generation.toEntity
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
class FetchGenerationsUseCaseTest {

    private lateinit var repository: FakeGenerationRepository
    private lateinit var getGenerationsUseCase: FetchGenerationsUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeGenerationRepository()
        getGenerationsUseCase = FetchGenerationsUseCase(repository)
    }

    @Test
    fun `get generations results in successful Result with all generations`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        repository.addGenerations(generationI, generationII, generationVI)

        getGenerationsUseCase().test {
            val allGenerationsResult = awaitItem()
            assertThat(allGenerationsResult.isSuccess).isTrue()
            assertThat(
                (allGenerationsResult.getOrThrow())
            ).containsExactly(generationI, generationII, generationVI)
            awaitComplete()
        }
    }

    @Test
    fun `successful Result with empty list when no generations are present`() = runTest {
        getGenerationsUseCase().test {
            val allGenerationsResult = awaitItem()
            assertThat(allGenerationsResult.isSuccess).isTrue()
            assertThat((allGenerationsResult.getOrThrow())).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `get generations results in error Result on any exception`() = runTest {
        val generationI = getGenerationRemote(GEN_I_FILE).toEntity().asExternalModel()
        val generationII = getGenerationRemote(GEN_II_FILE).toEntity().asExternalModel()
        val generationVI = getGenerationRemote(GEN_VI_FILE).toEntity().asExternalModel()
        repository.addGenerations(generationI, generationII, generationVI)
        repository.setReturnError(true)

        getGenerationsUseCase().test {
            val allGenerationsResult = awaitItem()
            assertThat(allGenerationsResult.isFailure).isTrue()
            assertThat((allGenerationsResult.exceptionOrNull())?.message).isEqualTo(
                EXPECTED_TEST_EXCEPTION
            )
            awaitComplete()
        }
    }
}
