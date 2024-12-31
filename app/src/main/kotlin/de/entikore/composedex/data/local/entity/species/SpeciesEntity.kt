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
package de.entikore.composedex.data.local.entity.species

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.entikore.composedex.data.remote.model.species.PokemonSpeciesRemote
import de.entikore.composedex.domain.model.pokemon.ChainLink

/**
 * Local representation of [PokemonSpeciesRemote].
 */
@Entity(tableName = "species")
data class SpeciesEntity(
    @PrimaryKey(autoGenerate = false) val speciesId: Int,
    val name: String,
    val isBaby: Boolean,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val flavorTextEntries: List<String>,
    val evolvesFrom: String,
    val evolutionChain: Map<Int, List<ChainLink>>,
    val genera: String,
    val shape: String
)
