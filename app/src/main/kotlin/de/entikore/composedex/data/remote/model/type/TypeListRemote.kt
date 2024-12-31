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

import de.entikore.composedex.data.local.entity.type.TypeOverviewEntity
import de.entikore.composedex.data.remote.model.common.NamedApiResource

/**
 * A list of available [TypeRemote] resources.
 *
 * @property count The total number of resources available from this API.
 * @property results A list of named API resources.
 */
data class TypeListRemote(val count: Int, val results: List<NamedApiResource>)

/**
 * Converts a [TypeListRemote] to a [TypeOverviewEntity].
 */
@JvmName("typeListToEntities")
fun TypeListRemote.toEntity() =
    TypeOverviewEntity(count = results.size, names = results.map { it.name })