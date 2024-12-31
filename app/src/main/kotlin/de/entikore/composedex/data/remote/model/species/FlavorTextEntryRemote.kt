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
package de.entikore.composedex.data.remote.model.species

import com.squareup.moshi.Json
import de.entikore.composedex.data.remote.model.common.NamedApiResource

/**
 * A flavor text entries for a [PokemonSpeciesRemote].
 *
 * @property flavorText    The localized flavor text for an API resource in a specific language.
 *                      Note that this text is left unprocessed as it is found in game files.
 *                      This means that it contains special characters that one might want to
 *                      replace with their visible decodable version.
 * @property language      The language this flavor text entry is in.
 */
data class FlavorTextEntryRemote(
    @Json(name = "flavor_text") val flavorText: String,
    val language: NamedApiResource
)
