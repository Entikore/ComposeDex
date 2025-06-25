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
package de.entikore.composedex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.entikore.composedex.data.local.entity.pokemon.PokemonEntity
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonSpeciesCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonTypeCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonVarietyCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.local.entity.pokemon.update.ArtworkUpdate
import de.entikore.composedex.data.local.entity.pokemon.update.CryUpdate
import de.entikore.composedex.data.local.entity.pokemon.update.FavouriteUpdate
import de.entikore.composedex.data.local.entity.pokemon.update.SpriteUpdate
import de.entikore.composedex.data.local.entity.species.SpeciesEntity
import de.entikore.composedex.data.local.entity.type.TypeEntity
import de.entikore.composedex.data.local.entity.variety.VarietyEntity
import kotlinx.coroutines.flow.Flow

/** Dao for Pokemon specific operations. */
@Dao
interface PokemonDao : BaseDao<PokemonEntity> {

    /**
     * Insert a relation object between [PokemonEntity] and [TypeEntity] into the database.
     *
     * @param crossRef with the [PokemonEntity] id and [TypeEntity] id that belong together.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTypeCrossRef(crossRef: PokemonTypeCrossRef)

    /**
     * Insert a relation object between [PokemonEntity] and [SpeciesEntity] into the database.
     *
     * @param crossRef with the [PokemonEntity] id and [SpeciesEntity] id that belong together.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSpeciesCrossRef(crossRef: PokemonSpeciesCrossRef)

    /**
     * Insert a relation object between [PokemonEntity] and [VarietyEntity] into the database.
     *
     * @param crossRef with the [PokemonEntity] id and [VarietyEntity] id that belong together.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVarietyCrossRef(crossRef: PokemonVarietyCrossRef)

    /**
     * Update the localArtwork of a [PokemonEntity].
     *
     * @param spriteUpdate containing the id of the [PokemonEntity] and the path where the artwork
     *   is stored
     */
    @Update(entity = PokemonEntity::class)
    suspend fun updateArtwork(spriteUpdate: ArtworkUpdate)

    /**
     * Update the localSprite of a [PokemonEntity].
     *
     * @param spriteUpdate containing the id of the [PokemonEntity] and the path where the sprite is
     *   stored
     */
    @Update(entity = PokemonEntity::class)
    suspend fun updateSprite(spriteUpdate: SpriteUpdate)

    /**
     * Update the localCry of a [PokemonEntity].
     *
     * @param cryUpdate containing the id of the [PokemonEntity] and the path where the cry is
     *   stored
     */
    @Update(entity = PokemonEntity::class)
    suspend fun updateCry(cryUpdate: CryUpdate)

    /**
     * Update the isFavourite of a [PokemonEntity].
     *
     * @param favouriteUpdate containing the id of the [PokemonEntity] and the value for the
     *   isFavourite attribute
     */
    @Update(entity = PokemonEntity::class)
    suspend fun updateFavourite(favouriteUpdate: FavouriteUpdate)

    /**
     * Load a [PokemonEntity] with the given id.
     *
     * @param id of the pokemon to load
     */
    @Query("SELECT * FROM pokemon WHERE pokemonId = :id")
    fun getById(id: Int): Flow<PokemonEntity>

    /**
     * Load a [PokemonEntity] with the given name.
     *
     * @param name of the pokemon to load
     */
    @Query("SELECT * FROM pokemon WHERE pokemonName = :name")
    fun getByName(name: String): Flow<PokemonEntity>

    /**
     * Load a [PokemonEntity] with the given id and all associated [SpeciesEntity], [TypeEntity] and
     * [VarietyEntity].
     *
     * @param id of the pokemon to load
     */
    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemonId = :id")
    fun getWithSpeciesTypesAndVarietiesById(id: Int): Flow<PokemonWithSpeciesTypesAndVarieties?>

    /**
     * Load a [PokemonEntity] with the given name and all associated [SpeciesEntity], [TypeEntity]
     * and [VarietyEntity].
     *
     * @param name of the pokemon to load
     */
    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemonName = :name")
    fun getWithSpeciesTypesAndVarietiesByName(
        name: String
    ): Flow<PokemonWithSpeciesTypesAndVarieties?>

    /**
     * Load a [PokemonEntity] with the given name and all associated [SpeciesEntity], [TypeEntity]
     * and [VarietyEntity] which are marked as favourite.
     */
    @Transaction
    @Query("SELECT * FROM pokemon WHERE isFavourite = 1 ORDER BY pokemonId ASC")
    fun getAllFavourites(): Flow<List<PokemonWithSpeciesTypesAndVarieties>>
}
