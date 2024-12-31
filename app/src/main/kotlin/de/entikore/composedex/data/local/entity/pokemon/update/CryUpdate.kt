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
package de.entikore.composedex.data.local.entity.pokemon.update

import de.entikore.composedex.data.local.entity.pokemon.PokemonEntity

/**
 * Represents an update to a [PokemonEntity]'s cry.
 *
 * This data class holds information about the Pokémon whose cry is being updated
 * and the local path to the new cry file.
 *
 * @property pokemonId The ID of the Pokémon whose artwork is being updated.
 * @property localCry The local path to the new cry file.
 */
data class CryUpdate(val pokemonId: Int, val localCry: String)
