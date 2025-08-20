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
import de.entikore.composedex.data.local.entity.pokemon.update.FavouriteUpdate
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.sharedtestcode.POKEMON_LAPRAS_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonLocalDataSourceTest: LocalDataSourceTest() {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ComposeDexDatabase
    private lateinit var localDataSource: PokemonLocalDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        database = Room.inMemoryDatabaseBuilder(context, ComposeDexDatabase::class.java)
            .addTypeConverter(ChainConverter(moshi))
            .addTypeConverter(StatsConverter(moshi))
            .addTypeConverter(TypesConverter(moshi))
            .build()
        localDataSource = PokemonLocalDataSource(
            database = database,
            dispatcher = mainCoroutineRule.getDispatcher()
        )

    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun canInsertPokemonWithSpeciesTypesAndVarieties() = runTest {
        val expectedEntity = getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity()
        database.pokemonDao()
            .getWithSpeciesTypesAndVarietiesByName(expectedEntity.pokemon.pokemonName).test {
                assertThat(awaitItem()).isNull()
            }

        localDataSource.insertPokemonWithSpeciesTypesAndVarieties(expectedEntity)

        database.pokemonDao()
            .getWithSpeciesTypesAndVarietiesByName(expectedEntity.pokemon.pokemonName).test {
                val actualEntity = awaitItem()
                assertThat(actualEntity).isNotNull()
                assertThat(actualEntity!!.sortTypesForComparison())
                    .isEqualTo(expectedEntity.sortTypesForComparison())
            }
    }

    @Test
    fun canUpdatePokemonArtwork() = runTest {
        val expectedPokemonEntity =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().pokemon
        val expectedArtwork = "newArtwork"
        database.pokemonDao().insert(expectedPokemonEntity)
        database.pokemonDao().getByName(expectedPokemonEntity.pokemonName).test {
            assertThat(awaitItem().localArtwork).isNull()
        }

        localDataSource.updatePokemonArtwork(expectedPokemonEntity.pokemonId, expectedArtwork)

        database.pokemonDao().getByName(expectedPokemonEntity.pokemonName).test {
            val actualEntity = awaitItem()
            assertThat(actualEntity.localArtwork).isEqualTo(expectedArtwork)
        }
    }

    @Test
    fun canUpdatePokemonSprite() = runTest {
        val expectedPokemonEntity =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().pokemon
        val expectedSprite = "newSprite"
        database.pokemonDao().insert(expectedPokemonEntity)
        database.pokemonDao().getByName(expectedPokemonEntity.pokemonName).test {
            assertThat(awaitItem().localSprite).isNull()
        }

        localDataSource.updatePokemonSprite(expectedPokemonEntity.pokemonId, expectedSprite)

        database.pokemonDao().getByName(expectedPokemonEntity.pokemonName).test {
            val actualEntity = awaitItem()
            assertThat(actualEntity.localSprite).isEqualTo(expectedSprite)
        }
    }

    @Test
    fun canUpdatePokemonCry() = runTest {
        val expectedPokemonEntity =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().pokemon
        val expectedCry = "newCry"
        database.pokemonDao().insert(expectedPokemonEntity)
        database.pokemonDao().getByName(expectedPokemonEntity.pokemonName).test {
            assertThat(awaitItem().localCry).isNull()
        }

        localDataSource.updatePokemonCry(expectedPokemonEntity.pokemonId, expectedCry)

        database.pokemonDao().getByName(expectedPokemonEntity.pokemonName).test {
            val actualEntity = awaitItem()
            assertThat(actualEntity.localCry).isEqualTo(expectedCry)
        }
    }

    @Test
    fun canUpdateVarietyArtwork() = runTest {
        val expectedVarietyEntity =
            getPokemonInfoRemote(getTestModel(POKEMON_LAPRAS_NAME)).toEntity().varieties[1]
        val expectedArtwork = "varietyArtwork"
        database.varietyDao().insert(expectedVarietyEntity)
        database.varietyDao().getByName(expectedVarietyEntity.varietyName).test {
            assertThat(awaitItem().localArtwork).isNull()
        }

        localDataSource.updateVarietyArtwork(expectedVarietyEntity.varietyName, expectedArtwork)

        database.varietyDao().getByName(expectedVarietyEntity.varietyName).test {
            val actualEntity = awaitItem()
            assertThat(actualEntity.localArtwork).isEqualTo(expectedArtwork)
        }
    }

    @Test
    fun observePokemonWithSpeciesTypesAndVarietiesByName() = runTest {
        val expectedEntity =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity()

        localDataSource.getPokemonWithSpeciesTypesAndVarietiesByName(expectedEntity.pokemon.pokemonName)
            .distinctUntilChanged().test {
                assertThat(awaitItem()).isNull()

                localDataSource.insertPokemonWithSpeciesTypesAndVarieties(expectedEntity)
                val actualEntity = awaitItem()
                assertThat(actualEntity).isNotNull()
                assertThat(actualEntity!!.sortTypesForComparison()).isEqualTo(expectedEntity.sortTypesForComparison())
                assertThat(actualEntity.pokemon.isFavourite).isFalse()

                database.pokemonDao().updateFavourite(FavouriteUpdate(expectedEntity.pokemon.pokemonId, true))
                val favouriteEntity = awaitItem()
                assertThat(favouriteEntity).isNotNull()
                assertThat(favouriteEntity!!.pokemon.isFavourite).isTrue()

                val expectedEntitySprite = "newSprite"
                localDataSource.updatePokemonSprite(
                    favouriteEntity.pokemon.pokemonId,
                    expectedEntitySprite
                )
                val spriteEntity = awaitItem()
                assertThat(spriteEntity).isNotNull()
                assertThat(spriteEntity!!.pokemon.localSprite).isEqualTo(expectedEntitySprite)
            }
    }

    @Test
    fun observePokemonWithSpeciesTypesAndVarietiesById() = runTest {
        val expectedEntity =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity()

        localDataSource.getPokemonWithSpeciesTypesAndVarietiesById(expectedEntity.pokemon.pokemonId)
            .distinctUntilChanged().test {
                assertThat(awaitItem()).isNull()

                localDataSource.insertPokemonWithSpeciesTypesAndVarieties(expectedEntity)
                val actualEntity = awaitItem()
                assertThat(actualEntity).isNotNull()
                assertThat(actualEntity!!.sortTypesForComparison()).isEqualTo(expectedEntity.sortTypesForComparison())
                assertThat(actualEntity.pokemon.isFavourite).isFalse()

                database.pokemonDao().updateFavourite(FavouriteUpdate(expectedEntity.pokemon.pokemonId, true))
                val favouriteEntity = awaitItem()
                assertThat(favouriteEntity).isNotNull()
                assertThat(favouriteEntity!!.pokemon.isFavourite).isTrue()

                val expectedEntitySprite = "newSprite"
                localDataSource.updatePokemonSprite(
                    favouriteEntity.pokemon.pokemonId,
                    expectedEntitySprite
                )
                val spriteEntity = awaitItem()
                assertThat(spriteEntity).isNotNull()
                assertThat(spriteEntity!!.pokemon.localSprite).isEqualTo(expectedEntitySprite)
            }
    }
}
