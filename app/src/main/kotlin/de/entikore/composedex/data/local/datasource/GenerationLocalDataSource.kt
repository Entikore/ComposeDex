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
package de.entikore.composedex.data.local.datasource

import de.entikore.composedex.data.local.ComposeDexDatabase
import de.entikore.composedex.data.local.dao.GenerationDao
import de.entikore.composedex.data.local.entity.generation.GenerationEntity
import de.entikore.composedex.data.local.entity.generation.GenerationOverviewEntity
import de.entikore.composedex.data.local.entity.generation.relation.GenerationPokemonCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Local data source for [GenerationEntity] instances and their associated [PokemonWithSpeciesTypesAndVarieties].
 */
class GenerationLocalDataSource(
    private val database: ComposeDexDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val generationDao: GenerationDao = database.generationDao()

    suspend fun insertGenerationOverview(generationOverviewEntity: GenerationOverviewEntity) =
        withContext(dispatcher) { generationDao.insertOverview(generationOverviewEntity) }

    suspend fun insertGeneration(generationEntity: GenerationEntity) =
        withContext(dispatcher) { generationDao.insert(generationEntity) }

    suspend fun insertPokemonForGeneration(
        generation: GenerationEntity,
        fullPokemon: PokemonWithSpeciesTypesAndVarieties
    ) {
        withContext(dispatcher) {
            insertGeneration(generation)
            database.insertPokemonWithSpeciesTypesAndVarieties(
                fullPokemon.pokemon,
                fullPokemon.species,
                fullPokemon.types,
                fullPokemon.varieties
            )
            generationDao.insertPokemonCrossRef(
                GenerationPokemonCrossRef(
                    generation.generationId,
                    fullPokemon.pokemon.pokemonId
                )
            )
        }
    }

    fun getGenerationOverview(): Flow<GenerationOverviewEntity?> = generationDao.getOverview()

    fun getAllGenerations(): Flow<List<GenerationEntity>> = generationDao.getAll()

    fun getGenerationByName(name: String): Flow<GenerationEntity?> = generationDao.getByName(name)

    fun getGenerationById(id: Int): Flow<GenerationEntity?> = generationDao.getById(id)

    fun getPokemonOfGenerationByName(name: String): Flow<List<PokemonWithSpeciesTypesAndVarieties>> =
        generationDao.getPokemonWithinGenerationByName(name)

    fun getPokemonOfGenerationById(id: Int): Flow<List<PokemonWithSpeciesTypesAndVarieties>> =
        generationDao.getPokemonWithinGenerationById(id)
}
