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
package de.entikore.composedex.data.remote.model.evolution

import com.squareup.moshi.Json
import de.entikore.composedex.data.remote.model.common.NamedApiResource

/**
 * An evolution chain link of [EvolutionChainRemote] contains evolution details for a Pokémon in
 * the chain. Each link references the next Pokémon in the natural evolution order.
 *
 * @property evolvesTo A List of chain objects.
 * @property isBaby Whether or not this link is for a baby Pokémon. This would only ever be true on the base link.
 * @property species The Pokémon species at this point in the evolution chain.
 */
data class ChainRemote(
    @Json(name = "evolves_to") val evolvesTo: List<ChainRemote>,
    @Json(name = "is_baby") val isBaby: Boolean,
    val species: NamedApiResource
)
