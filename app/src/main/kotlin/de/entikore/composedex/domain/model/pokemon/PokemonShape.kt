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
 * Different shapes of [Pokemon], useful for filtering.
 */
enum class PokemonShape {
    BALL,
    SQUIGGLE,
    FISH,
    ARMS,
    BLOB,
    UPRIGHT,
    LEGS,
    QUADRUPED,
    WINGS,
    TENTACLES,
    HEADS,
    HUMANOID,
    BUG_WINGS,
    ARMOR,
    UNDEFINED;

    companion object {

        fun getShapes(): List<PokemonShape> = entries.filter { it != UNDEFINED }.sortedBy { it.name }

        private val shapeMap = mapOf(
            BALL.name.lowercase() to BALL,
            SQUIGGLE.name.lowercase() to SQUIGGLE,
            FISH.name.lowercase() to FISH,
            ARMS.name.lowercase() to ARMS,
            BLOB.name.lowercase() to BLOB,
            UPRIGHT.name.lowercase() to UPRIGHT,
            LEGS.name.lowercase() to LEGS,
            QUADRUPED.name.lowercase() to QUADRUPED,
            WINGS.name.lowercase() to WINGS,
            TENTACLES.name.lowercase() to TENTACLES,
            HEADS.name.lowercase() to HEADS,
            HUMANOID.name.lowercase() to HUMANOID,
            BUG_WINGS.name.lowercase() to BUG_WINGS,
            ARMOR.name.lowercase() to ARMOR
        )

        fun getShape(shape: String): PokemonShape =
            shapeMap[shape.lowercase()] ?: UNDEFINED
    }
}
