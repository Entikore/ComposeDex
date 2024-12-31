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
package de.entikore.composedex.domain.model.type

import de.entikore.composedex.domain.model.pokemon.Pokemon

/**
 * A [Type] is a property for [Pokemon].
 *
 * @property name the name of this type.
 * @property pokemonOfType a list of [Pokemon] names that have this type.
 * @property doubleDamageFrom a list of [Type] names that are very effective against this type.
 * @property doubleDamageTo a list of [Type] names this type is very effective against.
 * @property halfDamageFrom a list of [Type] names that are not very effective against this type.
 * @property halfDamageTo a list of [Type] names this type is not very effective against.
 * @property noDamageFrom a list of [Type] names this type has no effect on.
 * @property noDamageTo a list of [Type] names that have no effect on this type.
 */
data class Type(
    val name: String,
    val pokemonOfType: List<String>,
    val doubleDamageFrom: List<String>,
    val doubleDamageTo: List<String>,
    val halfDamageFrom: List<String>,
    val halfDamageTo: List<String>,
    val noDamageFrom: List<String>,
    val noDamageTo: List<String>
) {

    companion object {
        const val BUG = "bug"
        const val DARK = "dark"
        const val DRAGON = "dragon"
        const val ELECTRIC = "electric"
        const val FAIRY = "fairy"
        const val FIGHTING = "fighting"
        const val FIRE = "fire"
        const val FLYING = "flying"
        const val GHOST = "ghost"
        const val GRASS = "grass"
        const val GROUND = "ground"
        const val ICE = "ice"
        const val NORMAL = "normal"
        const val POISON = "poison"
        const val PSYCHIC = "psychic"
        const val ROCK = "rock"
        const val STEEL = "steel"
        const val WATER = "water"
        private const val SHADOW = "shadow"
        private const val STELLAR = "stellar"
        private const val UNKNOWN = "unknown"

        private val UNSUPPORTED_TYPES = listOf(SHADOW, STELLAR, UNKNOWN)

        fun isUnsupportedType(name: String) = UNSUPPORTED_TYPES.contains(name)

        fun getBackupType(): Type = Type(
            name = "",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList()
        )
    }
}
