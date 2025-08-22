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
package de.entikore.composedex.domain.usecase

import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.pokemon.wasPokemonUpdated
import de.entikore.composedex.domain.repository.PokemonRepository
import de.entikore.composedex.domain.usecase.base.BaseFetchUseCase
import de.entikore.composedex.domain.util.asResult
import de.entikore.composedex.domain.util.whitespacePattern
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A use case to retrieve [Pokemon] with the given name or id from the [PokemonRepository]. When the
 * [Pokemon] was retrieved successfully the flavor text entries are formatted and duplicates are
 * filtered.
 */
class FetchPokemonUseCase @Inject constructor(
    private val repository: PokemonRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseFetchUseCase<String, Pokemon>(dispatcher) {
    override fun execute(params: String): Flow<Result<Pokemon>> {
        val id = params.trim().toIntOrNull()
        return if (id != null) {
            repository.getPokemonById(id).distinctUntilChanged(Pokemon::wasPokemonUpdated).map {
                processSuccessResult(it)
            }.asResult()
        } else {
            val normalizedParams = params.lowercase().trim()
            repository.getPokemonByName(
                normalizedParams
            ).distinctUntilChanged(Pokemon::wasPokemonUpdated)
                .map {
                    processSuccessResult(it)
                }.asResult()
        }
    }

    private fun processSuccessResult(result: Pokemon): Pokemon {
        return result.copy(textEntries = processFlavorTextEntries(result.textEntries))
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
