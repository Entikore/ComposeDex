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
import androidx.room.Query
import androidx.room.Update
import de.entikore.composedex.data.local.entity.variety.VarietyEntity
import de.entikore.composedex.data.local.entity.variety.update.VarietyArtworkUpdate
import kotlinx.coroutines.flow.Flow

/** Dao for Variety specific operations. */
@Dao
interface VarietyDao : BaseDao<VarietyEntity> {

    /**
     * Load a [VarietyEntity] with the given name.
     *
     * @param name of the variety to load
     */
    @Query("SELECT * FROM variety WHERE varietyName = :name")
    fun getByName(name: String): Flow<VarietyEntity>

    /**
     * Update the localArtwork for a [VarietyEntity].
     *
     * @param artworkUpdate containing the id of the [VarietyEntity] and the path where the artwork
     *   is stored
     */
    @Update(entity = VarietyEntity::class)
    suspend fun updateArtwork(artworkUpdate: VarietyArtworkUpdate)
}
