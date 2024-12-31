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
package de.entikore.composedex.data.remote.model.type

import com.squareup.moshi.Json
import de.entikore.composedex.data.remote.model.common.NamedApiResource

/**
 * A detail of how effective a [TypeRemote] is toward others and vice versa.
 *
 * @property doubleDamageFrom A list of types that are very effective against this type.
 * @property doubleDamageTo A list of types this type is very effect against.
 * @property halfDamageFrom A list of types that are not very effective against this type.
 * @property halfDamageTo A list of types this type is not very effect against.
 * @property noDamageFrom A list of types that have no effect on this type.
 * @property noDamageTo A list of types this type has no effect on.
 */
data class DamageRelationsRemote(
    @Json(name = "double_damage_from") val doubleDamageFrom: List<NamedApiResource>,
    @Json(name = "double_damage_to") val doubleDamageTo: List<NamedApiResource>,
    @Json(name = "half_damage_from") val halfDamageFrom: List<NamedApiResource>,
    @Json(name = "half_damage_to") val halfDamageTo: List<NamedApiResource>,
    @Json(name = "no_damage_from") val noDamageFrom: List<NamedApiResource>,
    @Json(name = "no_damage_to") val noDamageTo: List<NamedApiResource>
)
