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
 * The evolution chain of a Pokémon species.
 *
 * @property babyTriggerItem   The item that a Pokémon would be holding when mating that would trigger
 *                          the egg hatching a baby Pokémon rather than a basic Pokémon.
 * @property chain             The base chain link object. Each link contains evolution details for a
 *                          Pokémon in the chain. Each link references the next Pokémon in the
 *                          natural evolution order.
 * @property id                The identifier for this resource.
 */
data class EvolutionChainRemote(
    @Json(name = "baby_trigger_item") val babyTriggerItem: NamedApiResource? = null,
    val chain: ChainRemote,
    val id: Int
)
