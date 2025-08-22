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

import de.entikore.composedex.domain.repository.FavouriteRepository
import de.entikore.composedex.domain.usecase.base.BaseSuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * This use case updates the favourite status of a pokemon.
 */
class SetAsFavouriteUseCase @Inject constructor(
    private val repository: FavouriteRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseSuspendUseCase<@JvmSuppressWildcards SetFavouriteData, @JvmSuppressWildcards Unit>(dispatcher) {
    override suspend fun execute(params: @JvmSuppressWildcards SetFavouriteData) =
        repository.updateIsFavourite(params.id, params.isFavourite)
}

/**
 * Data class representing information needed to update a Pokémon's favourite status.
 *
 * This class encapsulates the necessary data for updating a Pokémon's favourite status,
 * including the Pokémon ID and the desired favourite status (true for favourite, false otherwise).
 *
 * @property id             The ID of the Pokémon whose favourite status is being updated.
 * @property isFavourite    The desired favourite status of the Pokémon
 *                          (true for favourite, false otherwise).
 */
data class SetFavouriteData(
    val id: Int,
    val isFavourite: Boolean
)
