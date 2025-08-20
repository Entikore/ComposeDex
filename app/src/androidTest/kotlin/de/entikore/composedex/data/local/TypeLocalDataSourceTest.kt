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
package de.entikore.composedex.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.entikore.composedex.MainCoroutineRule
import de.entikore.composedex.data.local.converter.ChainConverter
import de.entikore.composedex.data.local.converter.StatsConverter
import de.entikore.composedex.data.local.converter.TypesConverter
import de.entikore.composedex.data.local.datasource.PokemonLocalDataSource
import de.entikore.composedex.data.local.datasource.TypeLocalDataSource
import de.entikore.composedex.data.local.entity.type.TypeEntity
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.composedex.data.remote.model.type.toEntity
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.TYPE_GRASS_FILE
import de.entikore.sharedtestcode.TYPE_ICE_FILE
import de.entikore.sharedtestcode.TYPE_NORMAL_FILE
import de.entikore.sharedtestcode.TYPE_POISON_FILE
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTypeListRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTypeRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TypeLocalDataSourceTest: LocalDataSourceTest() {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ComposeDexDatabase
    private lateinit var localDataSource: TypeLocalDataSource
    private lateinit var pokemonLocalDataSource: PokemonLocalDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        database = Room.inMemoryDatabaseBuilder(context, ComposeDexDatabase::class.java)
            .addTypeConverter(ChainConverter(moshi))
            .addTypeConverter(StatsConverter(moshi))
            .addTypeConverter(TypesConverter(moshi))
            .build()
        pokemonLocalDataSource = PokemonLocalDataSource(
            database = database,
            dispatcher = mainCoroutineRule.getDispatcher()
        )
        localDataSource = TypeLocalDataSource(
            database = database,
            dispatcher = mainCoroutineRule.getDispatcher()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun canInsertTypeOverview() = runTest {
        val expectedTypeOverviewEntity = getTypeListRemote().toEntity()
        database.typeDao().getOverview().test {
            assertThat(awaitItem()).isNull()
        }

        localDataSource.insertTypeOverview(expectedTypeOverviewEntity)

        database.typeDao().getOverview().test {
            val actualTypeOverviewEntity = awaitItem()
            assertThat(actualTypeOverviewEntity).isEqualTo(expectedTypeOverviewEntity)
        }
    }

    @Test
    fun canInsertType() = runTest {
        val expectedTypeEntity = getTypeRemote(TYPE_ICE_FILE).toEntity()
        database.typeDao().getByName(expectedTypeEntity.typeName).test {
            assertThat(awaitItem()).isNull()
        }

        localDataSource.insertType(expectedTypeEntity)

        database.typeDao().getByName(expectedTypeEntity.typeName).test {
            val actualTypeEntity = awaitItem()
            assertThat(actualTypeEntity).isEqualTo(expectedTypeEntity)
        }
    }

    @Test
    fun canInsertPokemonForType() = runTest {
        val expectedEntity = getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity()
        val expectedType = getTypeRemote(TYPE_GRASS_FILE).toEntity()
        database.typeDao().getPokemonWithType(expectedType.typeName).test {
            assertThat(awaitItem()).isEmpty()
        }

        localDataSource.insertPokemonForType(expectedType, expectedEntity)

        database.typeDao().getPokemonWithType(expectedType.typeName).test {
            val actualEntity = awaitItem()
            assertThat(actualEntity.sortTypesForComparison()).containsExactly(expectedEntity.sortTypesForComparison())
        }
    }

    @Test
    fun observeTypeOverview() = runTest {
        val expectedTypeOverviewEntity = getTypeListRemote().toEntity()

        localDataSource.getTypeOverview().distinctUntilChanged().test {
            assertThat(awaitItem()).isNull()
            localDataSource.insertTypeOverview(expectedTypeOverviewEntity)
            val actualTypeOverviewEntity = awaitItem()
            assertThat(actualTypeOverviewEntity).isEqualTo(expectedTypeOverviewEntity)
        }
    }

    @Test
    fun observeAllTypes() = runTest {
        val testDataEntities = listOf(
            getTypeRemote(TYPE_ICE_FILE).toEntity(),
            getTypeRemote(TYPE_GRASS_FILE).toEntity(),
            getTypeRemote(TYPE_NORMAL_FILE).toEntity(),
            getTypeRemote(TYPE_POISON_FILE).toEntity()
        )

        localDataSource.getAllTypes().distinctUntilChanged().test {
            assertThat(awaitItem()).isEmpty()
            val expectedTypes = mutableListOf<TypeEntity>()
            for (typeEntity in testDataEntities) {
                expectedTypes.add(typeEntity)
                localDataSource.insertType(typeEntity)
                val actualTypes = awaitItem()
                assertThat(actualTypes.size).isEqualTo(expectedTypes.size)
                assertThat(actualTypes).containsExactlyElementsIn(expectedTypes)
            }
        }
    }

    @Test
    fun observeTypeByName() = runTest {
        val expectedTypeEntity = getTypeRemote(TYPE_ICE_FILE).toEntity()

        localDataSource.getTypeByName(expectedTypeEntity.typeName).distinctUntilChanged().test {
            assertThat(awaitItem()).isNull()
            localDataSource.insertType(expectedTypeEntity)
            val actualTypeEntity = awaitItem()
            assertThat(actualTypeEntity).isEqualTo(expectedTypeEntity)
        }
    }

    @Test
    fun observePokemonOfType() = runTest {
        val expectedTypeEntity = getTypeRemote(TYPE_POISON_FILE).toEntity()
        observePokemonOfX(
            expectedTypeEntity,
            expectedTypeEntity.typeName,
            localDataSource::getPokemonOfType,
            localDataSource::insertType,
            localDataSource::insertPokemonForType
        )
    }
}
