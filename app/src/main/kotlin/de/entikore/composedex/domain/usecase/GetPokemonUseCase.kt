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
package de.entikore.composedex.domain.usecase

import de.entikore.composedex.domain.WorkResult
import de.entikore.composedex.domain.asWorkResult
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.pokemon.wasPokemonUpdated
import de.entikore.composedex.domain.repository.PokemonRepository
import de.entikore.composedex.domain.usecase.base.ParamsUseCase
import de.entikore.composedex.domain.util.whitespacePattern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A use case to retrieve [Pokemon] with the given name or id from the [PokemonRepository]. When the
 * [Pokemon] was retrieved successfully the flavor text entries are formatted and duplicates are
 * filtered.
 */
class GetPokemonUseCase @Inject constructor(private val repository: PokemonRepository) :
    ParamsUseCase<String, Flow<WorkResult<Pokemon>>>() {

    override operator fun invoke(params: String): Flow<WorkResult<Pokemon>> {
        val id = params.trim().toIntOrNull()
        return if (id != null) {
            repository.getPokemonById(id).distinctUntilChanged(Pokemon::wasPokemonUpdated).asWorkResult().map {
                processSuccessResult(it)
            }
        } else {
            val normalizedParams = params.lowercase().trim()
            repository.getPokemonByName(
                normalizedParams
            ).distinctUntilChanged(Pokemon::wasPokemonUpdated).asWorkResult()
                .map {
                    processSuccessResult(it)
                }
        }
    }

    private fun processSuccessResult(result: WorkResult<Pokemon>): WorkResult<Pokemon> {
        return when (result) {
            is WorkResult.Success -> {
                val pokemon =
                    result.data.copy(textEntries = processFlavorTextEntries(result.data.textEntries))
                WorkResult.Success(pokemon)
            }

            else -> result
        }
    }

    /**
     * Replace all whitespaces with a single whitespace and filter duplicate text entries.
     */
    private fun processFlavorTextEntries(flavorTextEntries: List<String>): List<String> {
        return flavorTextEntries
            .map {
                it.replace(whitespacePattern, " ").trim()
            }.distinctBy { it.lowercase() }
    }
}
