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
 * A [ChainLink] is a stage of the evolution chain of a [Pokemon]. A form of a family tree which
 * shows the different Evolutions a [Pokemon] can have.
 *
 * @property name of the [Pokemon]
 * @property url name of the resource, might be the same as 'name'
 * @property isBaby marks if this entry represents a baby pokemon or not
 */
data class ChainLink(val name: String, val url: String, val isBaby: Boolean)
