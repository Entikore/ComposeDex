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
import de.entikore.composedex.data.local.entity.species.SpeciesEntity
import kotlinx.coroutines.flow.Flow

/** Dao for Species specific operations. */
@Dao
interface SpeciesDao : BaseDao<SpeciesEntity> {

    /**
     * Load a [SpeciesEntity] with the given id.
     *
     * @param id of the species to load
     */
    @Query("SELECT * FROM species WHERE speciesId = :id")
    fun getById(id: Int): Flow<SpeciesEntity>
}
