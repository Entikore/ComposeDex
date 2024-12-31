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

import de.entikore.composedex.data.remote.model.common.NamedApiResource

/**
 * A Pokemon that have a certain [TypeRemote].
 *
 * @property pokemon The Pokémon that has the referenced type.
 * @property slot The order the Pokémon's types are listed in.
 */
data class TypePokemon(val pokemon: NamedApiResource, val slot: Int)
