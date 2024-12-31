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
import de.entikore.composedex.data.local.entity.type.asExternalModel
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.composedex.domain.WorkResult
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
class GetTypesUseCaseTest {

    private lateinit var repository: FakeTypeRepository
    private lateinit var getTypesUseCase: GetTypesUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeTypeRepository()
        getTypesUseCase = GetTypesUseCase(repository)
    }

    @Test
    fun `get types results in WorkResult Success with all types`() = runTest {
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val normalType = getTypeRemote(TYPE_NORMAL_FILE).toEntity().asExternalModel()
        repository.addTypes(poisonType, grassType, normalType)

        getTypesUseCase().test {
            var allTypesResult = awaitItem()
            assertThat(allTypesResult).isInstanceOf(WorkResult.Loading::class.java)
            allTypesResult = awaitItem()
            assertThat(allTypesResult).isInstanceOf(WorkResult.Success::class.java)
            assertThat((allTypesResult as WorkResult.Success).data).containsExactly(poisonType, grassType, normalType)
            awaitComplete()
        }
    }

    @Test
    fun `get types results in WorkResult Success with empty list when no types are present`() = runTest {
        getTypesUseCase().test {
            var allTypesResult = awaitItem()
            assertThat(allTypesResult).isInstanceOf(WorkResult.Loading::class.java)
            allTypesResult = awaitItem()
            assertThat(allTypesResult).isInstanceOf(WorkResult.Success::class.java)
            assertThat((allTypesResult as WorkResult.Success).data).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `get types results in WorkResult Error on any exception`() = runTest {
        val poisonType = getTypeRemote(TYPE_POISON_FILE).toEntity().asExternalModel()
        val grassType = getTypeRemote(TYPE_GRASS_FILE).toEntity().asExternalModel()
        val normalType = getTypeRemote(TYPE_NORMAL_FILE).toEntity().asExternalModel()
        repository.addTypes(poisonType, grassType, normalType)
        repository.setReturnError(true)

        getTypesUseCase().test {
            var allTypesResult = awaitItem()
            assertThat(allTypesResult).isInstanceOf(WorkResult.Loading::class.java)
            allTypesResult = awaitItem()
            assertThat(allTypesResult).isInstanceOf(WorkResult.Error::class.java)
            assertThat((allTypesResult as WorkResult.Error).exception!!.message).isEqualTo(EXPECTED_TEST_EXCEPTION)
            awaitComplete()
        }
    }
}
