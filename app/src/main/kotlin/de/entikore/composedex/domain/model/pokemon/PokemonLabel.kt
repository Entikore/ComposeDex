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
package de.entikore.composedex.domain.model.pokemon

/**
 * Data container for the different possible labels a Pokemon can have.
 *
 * @property baby
 * @property legendary
 * @property mystical
 */
data class PokemonLabels(
    val baby: Pair<PokemonLabel, Boolean> = PokemonLabel.BABY to false,
    val legendary: Pair<PokemonLabel, Boolean> = PokemonLabel.LEGENDARY to false,
    val mystical: Pair<PokemonLabel, Boolean> = PokemonLabel.MYSTICAL to false
)

/**
 * Some Pokemon are classified with a certain label. These are displayed on the screen and used
 * for sorting.
 *
 * @property uiString to display on the screen.
 */
enum class PokemonLabel(val uiString: String) {
    BABY("Baby"),
    LEGENDARY("Legendary"),
    MYSTICAL("Mystical")
}
