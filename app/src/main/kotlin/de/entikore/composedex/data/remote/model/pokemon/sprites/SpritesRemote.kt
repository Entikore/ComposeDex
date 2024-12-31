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
package de.entikore.composedex.data.remote.model.pokemon.sprites

import com.squareup.moshi.Json
import de.entikore.composedex.data.remote.model.pokemon.sprites.other.OtherRemote

/**
 * A set of sprites used to depict a Pokémon.
 */
data class SpritesRemote(
    @Json(name = "back_default") val backDefault: String?,
    @Json(name = "back_female") val backFemale: String?,
    @Json(name = "back_shiny") val backShiny: String?,
    @Json(name = "back_shiny_female") val backShinyFemale: String?,
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_female") val frontFemale: String?,
    @Json(name = "front_shiny") val frontShiny: String?,
    @Json(name = "front_shiny_female") val frontShinyFemale: String?,
    val other: OtherRemote
)
