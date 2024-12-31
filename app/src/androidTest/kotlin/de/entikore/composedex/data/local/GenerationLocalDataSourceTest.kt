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
import de.entikore.composedex.data.local.datasource.GenerationLocalDataSource
import de.entikore.composedex.data.local.datasource.PokemonLocalDataSource
import de.entikore.composedex.data.local.entity.generation.GenerationEntity
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.remote.model.generation.toEntity
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.sharedtestcode.GEN_II_FILE
import de.entikore.sharedtestcode.GEN_I_FILE
import de.entikore.sharedtestcode.GEN_VI_FILE
import de.entikore.sharedtestcode.POKEMON_GLOOM_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.POKEMON_VILEPLUME_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getGenerationListRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getGenerationRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GenerationLocalDataSourceTest: LocalDataSourceTest() {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ComposeDexDatabase
    private lateinit var localDataSource: GenerationLocalDataSource
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
            pokemonDao = database.pokemonDao(),
            speciesDao = database.speciesDao(),
            varietyDao = database.varietyDao(),
            typeDao = database.typeDao(),
            dispatcher = mainCoroutineRule.getDispatcher()
        )
        localDataSource = GenerationLocalDataSource(
            pokemonLocalDataSource = pokemonLocalDataSource,
            generationDao = database.generationDao(),
            dispatcher = mainCoroutineRule.getDispatcher()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun canInsertGenerationOverview() = runTest {
        val expectedGenerationOverviewEntity = getGenerationListRemote().toEntity()
        database.generationDao().getOverview().test {
            assertThat(awaitItem()).isNull()
        }

        localDataSource.insertGenerationOverview(expectedGenerationOverviewEntity)

        database.generationDao().getOverview().test {
            val actualGenerationOverviewEntity = awaitItem()
            assertThat(actualGenerationOverviewEntity).isEqualTo(expectedGenerationOverviewEntity)
        }
    }

    @Test
    fun canInsertGeneration() = runTest {
        val expectedGenerationEntity = getGenerationRemote(GEN_I_FILE).toEntity()
        database.generationDao().getByName(expectedGenerationEntity.generationName).test {
            assertThat(awaitItem()).isNull()
        }

        localDataSource.insertGeneration(expectedGenerationEntity)

        database.generationDao().getByName(expectedGenerationEntity.generationName).test {
            val actualGenerationEntity = awaitItem()
            assertThat(actualGenerationEntity).isEqualTo(expectedGenerationEntity)
        }
    }

    @Test
    fun canInsertPokemonForGeneration() = runTest {
        val expectedEntity = getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity()
        val expectedGenerationEntity = getGenerationRemote(GEN_I_FILE).toEntity()
        database.generationDao()
            .getPokemonWithinGenerationByName(expectedGenerationEntity.generationName).test {
                assertThat(awaitItem()).isEmpty()
            }

        localDataSource.insertPokemonForGeneration(expectedGenerationEntity, expectedEntity)

        database.generationDao()
            .getPokemonWithinGenerationByName(expectedGenerationEntity.generationName).test {
                val actualEntity = awaitItem()
                assertThat(actualEntity.sortTypesForComparison()).containsExactly(expectedEntity.sortTypesForComparison())
            }
    }

    @Test
    fun observeGenerationOverview() = runTest {
        val expectedGenerationOverviewEntity = getGenerationListRemote().toEntity()

        localDataSource.getGenerationOverview().distinctUntilChanged().test {
            assertThat(awaitItem()).isNull()
            localDataSource.insertGenerationOverview(expectedGenerationOverviewEntity)
            val actualGenerationOverviewEntity = awaitItem()
            assertThat(actualGenerationOverviewEntity).isEqualTo(expectedGenerationOverviewEntity)
        }
    }

    @Test
    fun observeAllGenerations() = runTest {
        val testDataEntities = listOf(
            getGenerationRemote(GEN_I_FILE).toEntity(),
            getGenerationRemote(GEN_II_FILE).toEntity(),
            getGenerationRemote(GEN_VI_FILE).toEntity()
        )

        localDataSource.getAllGenerations().distinctUntilChanged().test {
            assertThat(awaitItem()).isEmpty()
            val expectedGenerations = mutableListOf<GenerationEntity>()
            for (generationEntity in testDataEntities) {
                expectedGenerations.add(generationEntity)
                localDataSource.insertGeneration(generationEntity)
                val actualGenerations = awaitItem()
                assertThat(actualGenerations.size).isEqualTo(expectedGenerations.size)
                assertThat(actualGenerations).containsExactlyElementsIn(expectedGenerations)
            }
        }
    }

    @Test
    fun observeGenerationByName() = runTest {
        val expectedGenerationEntity = getGenerationRemote(GEN_II_FILE).toEntity()

        localDataSource.getGenerationByName(expectedGenerationEntity.generationName)
            .distinctUntilChanged().test {
                assertThat(awaitItem()).isNull()
                localDataSource.insertGeneration(expectedGenerationEntity)
                val actualGenerationEntity = awaitItem()
                assertThat(actualGenerationEntity).isEqualTo(expectedGenerationEntity)
            }
    }

    @Test
    fun observeGenerationById() = runTest {
        val expectedGenerationEntity = getGenerationRemote(GEN_II_FILE).toEntity()

        localDataSource.getGenerationById(expectedGenerationEntity.generationId)
            .distinctUntilChanged().test {
                assertThat(awaitItem()).isNull()
                localDataSource.insertGeneration(expectedGenerationEntity)
                val actualGenerationEntity = awaitItem()
                assertThat(actualGenerationEntity).isEqualTo(expectedGenerationEntity)
            }
    }

    @Test
    fun observePokemonOfGenerationByName() = runTest {
        val expectedGenerationEntity = getGenerationRemote(GEN_I_FILE).toEntity()
        observePokemonOfX(
            expectedGenerationEntity,
            expectedGenerationEntity.generationName,
            localDataSource::getPokemonOfGenerationByName,
            localDataSource::insertGeneration,
            localDataSource::insertPokemonForGeneration
        )
    }

    @Test
    fun observePokemonOfGenerationById() = runTest {
        val expectedGenerationEntity = getGenerationRemote(GEN_I_FILE).toEntity()
        observePokemonOfX(
            expectedGenerationEntity,
            expectedGenerationEntity.generationId,
            localDataSource::getPokemonOfGenerationById,
            localDataSource::insertGeneration,
            localDataSource::insertPokemonForGeneration
        )
    }
}
