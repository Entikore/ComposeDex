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
package de.entikore.composedex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import de.entikore.composedex.data.local.entity.generation.GenerationEntity
import de.entikore.composedex.data.local.entity.generation.GenerationOverviewEntity
import de.entikore.composedex.data.local.entity.generation.relation.GenerationPokemonCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import kotlinx.coroutines.flow.Flow

/** Dao for Generation specific operations. */
@Dao
interface GenerationDao : BaseDao<GenerationEntity> {

    /** Load the [GenerationOverviewEntity]. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOverview(overview: GenerationOverviewEntity)

    /** Load the [GenerationOverviewEntity]. */
    @Query("SELECT * FROM generation_overview WHERE id = 0")
    fun getOverview(): Flow<GenerationOverviewEntity>

    /** Load all [GenerationEntity]. */
    @Transaction
    @Query("SELECT * FROM generation")
    fun getAll(): Flow<List<GenerationEntity>>

    /**
     * Load a [GenerationEntity] with the given name.
     *
     * @param name of the [GenerationEntity]
     */
    @Transaction
    @Query("SELECT * FROM generation WHERE generationName = :name")
    fun getByName(name: String): Flow<GenerationEntity>

    /**
     * Load a [GenerationEntity] with the given id.
     *
     * @param id of the [GenerationEntity]
     */
    @Transaction
    @Query("SELECT * FROM generation WHERE generationId = :id")
    fun getById(id: Int): Flow<GenerationEntity>

    /**
     * Load all [PokemonWithSpeciesTypesAndVarieties] belonging to the [GenerationEntity] with the
     * given name.
     *
     * @param name of the [GenerationEntity]
     */
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM generation INNER JOIN generation_pokemon ON generation.generationId = generation_pokemon.generationPokemonCrossRefGenerationId" +
            " INNER JOIN pokemon ON pokemon.pokemonId = generation_pokemon.generationPokemonCrossRefPokemonId" +
            " WHERE generation.generationName = :name"
    )
    fun getPokemonWithinGenerationByName(name: String): Flow<List<PokemonWithSpeciesTypesAndVarieties>>

    /**
     * Load all [PokemonWithSpeciesTypesAndVarieties] belonging to the [GenerationEntity] with the
     * given id.
     *
     * @param id of the [GenerationEntity]
     */
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM generation INNER JOIN generation_pokemon ON generation.generationId = generation_pokemon.generationPokemonCrossRefGenerationId" +
            " INNER JOIN pokemon ON pokemon.pokemonId = generation_pokemon.generationPokemonCrossRefPokemonId" +
            " WHERE generation.generationId = :id"
    )
    fun getPokemonWithinGenerationById(id: Int): Flow<List<PokemonWithSpeciesTypesAndVarieties>>

    /**
     * Insert a relation object between [GenerationEntity] and [PokemonEntity] into the database.
     *
     * @param crossRef with the [GenerationEntity] id and [PokemonEntity] id that belong together.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonCrossRef(crossRef: GenerationPokemonCrossRef)
}
