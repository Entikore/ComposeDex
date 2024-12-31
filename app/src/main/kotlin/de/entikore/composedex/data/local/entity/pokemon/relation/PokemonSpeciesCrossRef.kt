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
package de.entikore.composedex.data.local.entity.pokemon.relation

import androidx.room.ColumnInfo
import androidx.room.Entity
import de.entikore.composedex.data.local.entity.pokemon.PokemonEntity
import de.entikore.composedex.data.local.entity.species.SpeciesEntity

/**
 * Represents a cross-reference entity linking a [PokemonEntity] to its [SpeciesEntity].
 *
 * @property pokemonId The ID of the Pokémon.
 * @property speciesId The id of the species.
 */
@Entity(tableName = "pokemon_species", primaryKeys = ["pokemonId", "speciesId"])
data class PokemonSpeciesCrossRef(val pokemonId: Int, @ColumnInfo(index = true) val speciesId: Int)
