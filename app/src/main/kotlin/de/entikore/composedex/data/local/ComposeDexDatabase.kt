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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import de.entikore.composedex.data.local.converter.ChainConverter
import de.entikore.composedex.data.local.converter.StatsConverter
import de.entikore.composedex.data.local.converter.TypesConverter
import de.entikore.composedex.data.local.dao.GenerationDao
import de.entikore.composedex.data.local.dao.PokemonDao
import de.entikore.composedex.data.local.dao.SpeciesDao
import de.entikore.composedex.data.local.dao.TypeDao
import de.entikore.composedex.data.local.dao.VarietyDao
import de.entikore.composedex.data.local.entity.generation.GenerationEntity
import de.entikore.composedex.data.local.entity.generation.GenerationOverviewEntity
import de.entikore.composedex.data.local.entity.generation.relation.GenerationPokemonCrossRef
import de.entikore.composedex.data.local.entity.pokemon.PokemonEntity
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonSpeciesCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonTypeCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonVarietyCrossRef
import de.entikore.composedex.data.local.entity.species.SpeciesEntity
import de.entikore.composedex.data.local.entity.type.TypeEntity
import de.entikore.composedex.data.local.entity.type.TypeOverviewEntity
import de.entikore.composedex.data.local.entity.type.relation.TypePokemonCrossRef
import de.entikore.composedex.data.local.entity.variety.VarietyEntity
import de.entikore.composedex.domain.repository.LocalStorage

/**
 * Room database for storing any persisted data from the PokeApi.
 */
@Database(
    entities = [
        GenerationEntity::class,
        GenerationOverviewEntity::class,
        GenerationPokemonCrossRef::class,
        PokemonEntity::class,
        PokemonSpeciesCrossRef::class,
        PokemonTypeCrossRef::class,
        PokemonVarietyCrossRef::class,
        SpeciesEntity::class,
        TypeEntity::class,
        TypeOverviewEntity::class,
        TypePokemonCrossRef::class,
        VarietyEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TypesConverter::class, StatsConverter::class, ChainConverter::class)
abstract class ComposeDexDatabase : RoomDatabase(), LocalStorage {

    abstract fun generationDao(): GenerationDao

    abstract fun pokemonDao(): PokemonDao

    abstract fun speciesDao(): SpeciesDao

    abstract fun typeDao(): TypeDao

    abstract fun varietyDao(): VarietyDao

    override fun clearData() {
        this.clearAllTables()
    }

    @Transaction
    suspend inline fun insertPokemonWithSpeciesTypesAndVarieties(
        pokemon: PokemonEntity,
        species: SpeciesEntity,
        types: List<TypeEntity>,
        varieties: List<VarietyEntity>
    ) {
        varieties.forEach { variety ->
            varietyDao().insert(variety)
            pokemonDao().insertVarietyCrossRef(
                PokemonVarietyCrossRef(
                    pokemon.pokemonId,
                    variety.varietyName
                )
            )
        }

        types.forEach { type ->
            typeDao().insert(type)
            pokemonDao().insertTypeCrossRef(
                PokemonTypeCrossRef(
                    pokemon.pokemonId,
                    type.typeId
                )
            )
        }

        speciesDao().insert(species)
        pokemonDao().insertSpeciesCrossRef(
            PokemonSpeciesCrossRef(
                pokemon.pokemonId,
                species.speciesId
            )
        )

        pokemonDao().insert(pokemon)
    }

    companion object {
        const val DATABASE_NAME = "composedex_db"
    }
}
