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
import de.entikore.composedex.data.local.entity.type.asExternalModel
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.composedex.fake.repository.FailableFakeRepository.Companion.EXPECTED_TEST_EXCEPTION
import de.entikore.composedex.fake.repository.FakeTypeRepository
import de.entikore.sharedtestcode.TYPE_GRASS_FILE
import de.entikore.sharedtestcode.TYPE_NORMAL_FILE
import de.entikore.sharedtestcode.TYPE_POISON_FILE
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTypeRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class FetchTypesUseCaseTest {

    private lateinit var repository: FakeTypeRepository
    private lateinit var getTypesUseCase: FetchTypesUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeTypeRepository()
        getTypesUseCase = FetchTypesUseCase(repository)
    }

    @Test
    fun `get types results in successful Result with all types`() = runTest {
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val normalType = getTypeRemote(TYPE_NORMAL_FILE).toEntity().asExternalModel()
        repository.addTypes(poisonType, grassType, normalType)

        getTypesUseCase().test {
            val allTypesResult = awaitItem()
            assertThat(allTypesResult.isSuccess).isTrue()
            assertThat(allTypesResult.getOrThrow()).containsExactly(poisonType, grassType, normalType)
            awaitComplete()
        }
    }

    @Test
    fun `get types results in successful Result with empty list when no types are present`() = runTest {
        getTypesUseCase().test {
            val allTypesResult = awaitItem()
            assertThat(allTypesResult.isSuccess).isTrue()
            assertThat(allTypesResult.getOrThrow()).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `get types results in error Result on any exception`() = runTest {
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val normalType = getTypeRemote(TYPE_NORMAL_FILE).toEntity().asExternalModel()
        repository.addTypes(poisonType, grassType, normalType)
        repository.setReturnError(true)

        getTypesUseCase().test {
            val allTypesResult = awaitItem()
            assertThat(allTypesResult.isFailure).isTrue()
            assertThat(allTypesResult.exceptionOrNull()?.message).isEqualTo(EXPECTED_TEST_EXCEPTION)
            awaitComplete()
        }
    }
}
