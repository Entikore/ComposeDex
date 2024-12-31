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
import de.entikore.composedex.data.local.entity.variety.VarietyEntity
import de.entikore.composedex.data.remote.model.common.NamedApiResource

/**
 * The variety of a Pokémon species.
 *
 * @property isDefault Whether this variety is the default variety.
 * @property pokemon The Pokémon variety.
 */
data class VarietyRemote(
    @Json(name = "is_default") val isDefault: Boolean,
    val pokemon: NamedApiResource
)

/**
 * Converts a [VarietyRemote] to a [VarietyEntity].
 */
fun VarietyRemote.toEntity() =
    VarietyEntity(varietyName = pokemon.name, isDefault = isDefault, localArtwork = null)

/**
 * Converts a list of [VarietyRemote] to a list of [VarietyEntity].
 */
@JvmName("varietyListToEntity")
fun List<VarietyRemote>.toEntity() = map(VarietyRemote::toEntity)
