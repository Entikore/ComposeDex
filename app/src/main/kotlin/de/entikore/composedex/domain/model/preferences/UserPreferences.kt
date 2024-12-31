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
package de.entikore.composedex.domain.model.preferences

import de.entikore.composedex.domain.model.type.Type

/**
 * Data class representing user preferences for app theme and type theme.
 *
 * @property appThemeConfig The user's preferred app theme configuration.
 * @property typeThemeConfig The user's preferred type theme configuration.
 */
data class UserPreferences(
    val appThemeConfig: AppThemeConfig = AppThemeConfig.MODE_AUTO,
    val typeThemeConfig: TypeThemeConfig = TypeThemeConfig.COLORLESS
)

/**
 * Represents the app's theme configuration options.
 *
 * This enum defines the available theme modes for the application:
 * - [MODE_DAY]: Light theme.
 * - [MODE_NIGHT]: Dark theme.
 * - [MODE_AUTO]: Automatic theme switching based on system settings.
 */
enum class AppThemeConfig {
    MODE_DAY,
    MODE_NIGHT,
    MODE_AUTO;

    companion object {
        fun fromOrdinal(ordinal: Int) = entries[ordinal]
    }
}

/**
 * Represents theme configurations based on Pokémon types.
 *
 * This enum defines themes for various Pokémon types, including a default `COLORLESS` theme.
 * It provides helper functions to retrieve themes by ordinal or type name.
 */
enum class TypeThemeConfig {
    COLORLESS,
    BUG,
    DARK,
    DRAGON,
    ELECTRIC,
    FAIRY,
    FIGHTING,
    FIRE,
    FLYING,
    GHOST,
    GRASS,
    GROUND,
    ICE,
    NORMAL,
    POISON,
    PSYCHIC,
    ROCK,
    STEEL,
    WATER;

    companion object {
        fun fromOrdinal(ordinal: Int) = TypeThemeConfig.entries[ordinal]

        private val typeThemeConfigMap = mapOf(
            Type.BUG to BUG,
            Type.DARK to DARK,
            Type.DRAGON to DRAGON,
            Type.ELECTRIC to ELECTRIC,
            Type.FAIRY to FAIRY,
            Type.FIGHTING to FIGHTING,
            Type.FIRE to FIRE,
            Type.FLYING to FLYING,
            Type.GHOST to GHOST,
            Type.GRASS to GRASS,
            Type.GROUND to GROUND,
            Type.ICE to ICE,
            Type.NORMAL to NORMAL,
            Type.POISON to POISON,
            Type.PSYCHIC to PSYCHIC,
            Type.ROCK to ROCK,
            Type.STEEL to STEEL,
            Type.WATER to WATER
        )

        fun fromTypeString(typeName: String): TypeThemeConfig =
            typeThemeConfigMap[typeName] ?: COLORLESS
    }
}
