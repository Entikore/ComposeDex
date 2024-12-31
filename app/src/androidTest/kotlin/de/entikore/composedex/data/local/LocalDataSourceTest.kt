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

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.remote.model.toEntity
import de.entikore.sharedtestcode.POKEMON_GLOOM_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.POKEMON_VILEPLUME_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

abstract class LocalDataSourceTest {

    suspend fun <E, P> observePokemonOfX(
        entity: E,
        param: P,
        observe: (P) -> Flow<List<PokemonWithSpeciesTypesAndVarieties>>,
        insertEntity: suspend (E) -> Unit,
        insertPokemonForEntity: suspend (E, PokemonWithSpeciesTypesAndVarieties) -> Unit
    ) {
        val testDataEntities = listOf(
            getPokemonInfoRemote(getTestModel(POKEMON_ODDISH_NAME)).toEntity(),
            getPokemonInfoRemote(getTestModel(POKEMON_GLOOM_NAME)).toEntity(),
            getPokemonInfoRemote(getTestModel(POKEMON_VILEPLUME_NAME)).toEntity()
        )
        observe.invoke(param).distinctUntilChanged().test {
            insertEntity.invoke(entity)
            assertThat(awaitItem()).isEmpty()
            val expectedPokemon = mutableListOf<PokemonWithSpeciesTypesAndVarieties>()
            for (testData in testDataEntities) {
                expectedPokemon.add(testData)
                insertPokemonForEntity(entity, testData)
                val actualPokemon = awaitItem()
                assertThat(actualPokemon.size).isEqualTo(expectedPokemon.size)
                assertThat(actualPokemon.sortTypesForComparison()).containsExactlyElementsIn(
                    expectedPokemon.sortTypesForComparison()
                )
            }
        }
    }

    fun List<PokemonWithSpeciesTypesAndVarieties>.sortTypesForComparison() =
        this.map { it.sortTypesForComparison() }

    fun PokemonWithSpeciesTypesAndVarieties.sortTypesForComparison() =
        this.copy(types = this.types.sortedBy { it.typeId })
}
