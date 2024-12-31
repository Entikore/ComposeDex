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
import de.entikore.composedex.data.local.datasource.FavouriteLocalDataSource
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonSpeciesCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonTypeCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonVarietyCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.sharedtestcode.POKEMON_GLOOM_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.POKEMON_VILEPLUME_NAME
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
class FavouriteLocalDataSourceTest: LocalDataSourceTest() {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ComposeDexDatabase
    private lateinit var localDataSource: FavouriteLocalDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        database = Room.inMemoryDatabaseBuilder(context, ComposeDexDatabase::class.java)
            .addTypeConverter(ChainConverter(moshi))
            .addTypeConverter(StatsConverter(moshi))
            .addTypeConverter(TypesConverter(moshi))
            .build()
        localDataSource = FavouriteLocalDataSource(
            pokemonDao = database.pokemonDao(),
            dispatcher = mainCoroutineRule.getDispatcher()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun canUpdateIsFavourite() = runTest {
        val expectedPokemonEntity =
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().pokemon
        database.pokemonDao().insert(expectedPokemonEntity)
        database.pokemonDao().getByName(expectedPokemonEntity.pokemonName).test {
            assertThat(awaitItem().isFavourite).isFalse()
        }

        localDataSource.updateIsFavourite(expectedPokemonEntity.pokemonId, true)

        database.pokemonDao().getByName(expectedPokemonEntity.pokemonName).test {
            val actualEntity = awaitItem()
            assertThat(actualEntity.isFavourite).isTrue()
        }
    }

    @Test
    fun observeAllFavourites() = runTest {
        val testDataEntities = listOf(
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity().markAsFavourite(),
            getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity().markAsFavourite(),
            getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity()
                .markAsFavourite()
        )
        localDataSource.getAllFavourites().distinctUntilChanged().test {
            assertThat(awaitItem()).isEmpty()

            val expectedFavourites = mutableListOf<PokemonWithSpeciesTypesAndVarieties>()
            for (entity in testDataEntities) {
                expectedFavourites.add(entity)
                insertEntities(entity)
                val actualFavourites = awaitItem()
                assertThat(actualFavourites.size).isEqualTo(expectedFavourites.size)
                assertThat(actualFavourites.sortTypesForComparison()).containsExactlyElementsIn(
                    expectedFavourites.sortTypesForComparison()
                )
            }
        }
    }

    private suspend fun insertEntities(entity: PokemonWithSpeciesTypesAndVarieties) {
        for (variety in entity.varieties) {
            database.varietyDao().insert(variety)
            database.pokemonDao().insertVarietyCrossRef(
                PokemonVarietyCrossRef(
                    entity.pokemon.pokemonId,
                    variety.varietyName
                )
            )
        }
        for (type in entity.types) {
            database.typeDao().insert(type)
            database.pokemonDao().insertTypeCrossRef(
                PokemonTypeCrossRef(
                    entity.pokemon.pokemonId,
                    type.typeId
                )
            )
        }
        database.speciesDao().insert(entity.species)
        database.pokemonDao().insertSpeciesCrossRef(
            PokemonSpeciesCrossRef(
                entity.pokemon.pokemonId,
                entity.species.speciesId
            )
        )
        database.pokemonDao().insert(entity.pokemon)
    }

    private fun PokemonWithSpeciesTypesAndVarieties.markAsFavourite(favourite: Boolean = true) =
        this.copy(pokemon = this.pokemon.copy(isFavourite = favourite))
}
