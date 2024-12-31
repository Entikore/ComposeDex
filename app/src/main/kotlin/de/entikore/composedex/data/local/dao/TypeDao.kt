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
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.local.entity.type.TypeEntity
import de.entikore.composedex.data.local.entity.type.TypeOverviewEntity
import de.entikore.composedex.data.local.entity.type.relation.TypePokemonCrossRef
import kotlinx.coroutines.flow.Flow

/** Dao for Type specific operations. */
@Dao
interface TypeDao : BaseDao<TypeEntity> {

    /** Load the [TypeOverviewEntity]. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOverview(overview: TypeOverviewEntity)

    /** Load the [TypeOverviewEntity]. */
    @Query("SELECT * FROM type_overview WHERE id = 0")
    fun getOverview(): Flow<TypeOverviewEntity>

    /**
     * Insert a relation object between [TypeEntity] and [PokemonEntity] into the database.
     *
     * @param crossRef with the [TypeEntity] id and [PokemonEntity] id that belong together.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonCrossRef(crossRef: TypePokemonCrossRef)

    /** Load all [TypeEntity]. */
    @Transaction
    @Query("SELECT * FROM type")
    fun getAll(): Flow<List<TypeEntity>>

    /**
     * Load a [TypeEntity] with the given id.
     *
     * @param id of the type to load
     */
    @Query("SELECT * FROM type WHERE typeId = :id")
    fun getById(id: Int): Flow<TypeEntity>

    /**
     * Load a [TypeEntity] with the given name.
     *
     * @param name of the type to load
     */
    @Query("SELECT * FROM type WHERE typeName = :name")
    fun getByName(name: String): Flow<TypeEntity>

    /**
     * Load all [PokemonWithSpeciesTypesAndVarieties] belonging to the [TypeEntity] with the
     * given name.
     *
     * @param name of the [TypeEntity]
     */
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM type INNER JOIN type_pokemon ON type.typeId = type_pokemon.typePokemonCrossRefTypeId" +
            " INNER JOIN pokemon ON pokemon.pokemonId = type_pokemon.typePokemonCrossRefPokemonId WHERE type.typeName = :name"
    )
    fun getPokemonWithType(name: String): Flow<List<PokemonWithSpeciesTypesAndVarieties>>
}
