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
package de.entikore.composedex.domain.model.generation

/**
 * A [Generation] is a grouping of the Pokémon games that separates them based on the Pokémon they
 * include. In each generation, a new set of Pokémon that did not exist in the previous generation
 * are released.
 *
 * @property id The identifier for this resource.
 * @property name The name for this resource.
 * @property pokemonInGeneration A list of Pokémon species that were introduced in this generation.
 * @property numberOfPokemon the total number of Pokémon in this generation.
 */
data class Generation(val id: Int, val name: String, val pokemonInGeneration: List<String>, val numberOfPokemon: Int)
