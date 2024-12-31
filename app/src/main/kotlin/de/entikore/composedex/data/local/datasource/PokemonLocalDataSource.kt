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

import de.entikore.composedex.data.local.dao.PokemonDao
import de.entikore.composedex.data.local.dao.SpeciesDao
import de.entikore.composedex.data.local.dao.TypeDao
import de.entikore.composedex.data.local.dao.VarietyDao
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonSpeciesCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonTypeCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonVarietyCrossRef
import de.entikore.composedex.data.local.entity.pokemon.relation.PokemonWithSpeciesTypesAndVarieties
import de.entikore.composedex.data.local.entity.pokemon.update.ArtworkUpdate
import de.entikore.composedex.data.local.entity.pokemon.update.CryUpdate
import de.entikore.composedex.data.local.entity.pokemon.update.SpriteUpdate
import de.entikore.composedex.data.local.entity.variety.update.VarietyArtworkUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Local data source for [PokemonWithSpeciesTypesAndVarieties] instances.
 */
class PokemonLocalDataSource(
    private val pokemonDao: PokemonDao,
    private val speciesDao: SpeciesDao,
    private val varietyDao: VarietyDao,
    private val typeDao: TypeDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun insertPokemonWithSpeciesTypesAndVarieties(
        pokemonWithSpeciesTypesAndVarieties: PokemonWithSpeciesTypesAndVarieties
    ) =
        withContext(dispatcher) {
            for (variety in pokemonWithSpeciesTypesAndVarieties.varieties) {
                varietyDao.insert(variety)
                pokemonDao.insertVarietyCrossRef(
                    PokemonVarietyCrossRef(
                        pokemonWithSpeciesTypesAndVarieties.pokemon.pokemonId,
                        variety.varietyName
                    )
                )
            }
            for (type in pokemonWithSpeciesTypesAndVarieties.types) {
                typeDao.insert(type)
                pokemonDao.insertTypeCrossRef(
                    PokemonTypeCrossRef(
                        pokemonWithSpeciesTypesAndVarieties.pokemon.pokemonId,
                        type.typeId
                    )
                )
            }
            speciesDao.insert(pokemonWithSpeciesTypesAndVarieties.species)
            pokemonDao.insertSpeciesCrossRef(
                PokemonSpeciesCrossRef(
                    pokemonWithSpeciesTypesAndVarieties.pokemon.pokemonId,
                    pokemonWithSpeciesTypesAndVarieties.species.speciesId
                )
            )
            pokemonDao.insert(pokemonWithSpeciesTypesAndVarieties.pokemon)
        }

    suspend fun updatePokemonArtwork(pokemonId: Int, artworkPath: String) =
        withContext(dispatcher) {
            return@withContext pokemonDao.updateArtwork(ArtworkUpdate(pokemonId, artworkPath))
        }

    suspend fun updatePokemonSprite(pokemonId: Int, spritePath: String) =
        withContext(dispatcher) {
            return@withContext pokemonDao.updateSprite(SpriteUpdate(pokemonId, spritePath))
        }

    suspend fun updatePokemonCry(pokemonId: Int, cryPath: String) =
        withContext(dispatcher) {
            return@withContext pokemonDao.updateCry(CryUpdate(pokemonId, cryPath))
        }

    suspend fun updateVarietyArtwork(varietyName: String, artworkPath: String) =
        withContext(dispatcher) {
            return@withContext varietyDao.updateArtwork(
                VarietyArtworkUpdate(varietyName, artworkPath)
            )
        }

    fun getPokemonWithSpeciesTypesAndVarietiesByName(
        name: String
    ): Flow<PokemonWithSpeciesTypesAndVarieties> =
        pokemonDao.getWithSpeciesTypesAndVarietiesByName(name)

    fun getPokemonWithSpeciesTypesAndVarietiesById(
        id: Int
    ): Flow<PokemonWithSpeciesTypesAndVarieties> =
        pokemonDao.getWithSpeciesTypesAndVarietiesById(id)
}
