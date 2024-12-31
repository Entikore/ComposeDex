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
package de.entikore.composedex.data.local.datasource

import de.entikore.composedex.data.local.dao.TypeDao
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.local.entity.type.TypeEntity
import de.entikore.composedex.data.local.entity.type.TypeOverviewEntity
import de.entikore.composedex.data.local.entity.type.relation.TypePokemonCrossRef
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Local data source for [TypeEntity] instances and their associated [PokemonWithSpeciesTypesAndVarieties].
 */
class TypeLocalDataSource(
    private val pokemonLocalDataSource: PokemonLocalDataSource,
    private val typeDao: TypeDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun insertTypeOverview(typeOverviewEntity: TypeOverviewEntity) =
        withContext(dispatcher) { typeDao.insertOverview(typeOverviewEntity) }

    suspend fun insertType(typeEntity: TypeEntity) =
        withContext(dispatcher) { typeDao.insert(typeEntity) }

    suspend fun insertPokemonForType(
        type: TypeEntity,
        pokemon: PokemonWithSpeciesTypesAndVarieties
    ) {
        withContext(dispatcher) {
            pokemonLocalDataSource.insertPokemonWithSpeciesTypesAndVarieties(pokemon)
            typeDao.insertPokemonCrossRef(
                TypePokemonCrossRef(
                    type.typeId,
                    pokemon.pokemon.pokemonId
                )
            )
        }
    }

    fun getTypeOverview(): Flow<TypeOverviewEntity> = typeDao.getOverview()

    fun getAllTypes(): Flow<List<TypeEntity>> = typeDao.getAll()

    fun getTypeByName(name: String): Flow<TypeEntity> = typeDao.getByName(name)

    fun getPokemonOfType(name: String): Flow<List<PokemonWithSpeciesTypesAndVarieties>> =
        typeDao.getPokemonWithType(name)
}
