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
package de.entikore.sharedtestcode

val testModels = mapOf(
    POKEMON_ODDISH_NAME to TestModel(
        pokemonFile = POKEMON_ODDISH_FILE,
        speciesFile = POKEMON_ODDISH_SPECIES_FILE,
        typeFiles = listOf(TYPE_GRASS_FILE, TYPE_POISON_FILE),
        varietyFiles = emptyList(),
        chainFile = POKEMON_ODDISH_CHAIN_FILE
    ),
    POKEMON_GLOOM_NAME to TestModel(
        pokemonFile = POKEMON_GLOOM_FILE,
        speciesFile = POKEMON_GLOOM_SPECIES_FILE,
        typeFiles = listOf(TYPE_GRASS_FILE, TYPE_POISON_FILE),
        varietyFiles = emptyList(),
        chainFile = POKEMON_GLOOM_CHAIN_FILE
    ),
    POKEMON_VILEPLUME_NAME to TestModel(
        pokemonFile = POKEMON_VILEPLUME_FILE,
        speciesFile = POKEMON_VILEPLUME_SPECIES_FILE,
        typeFiles = listOf(TYPE_GRASS_FILE, TYPE_POISON_FILE),
        varietyFiles = emptyList(),
        chainFile = POKEMON_VILEPLUME_CHAIN_FILE
    ),
    POKEMON_BELLOSSOM_NAME to TestModel(
        pokemonFile = POKEMON_BELLOSSOM_FILE,
        speciesFile = POKEMON_BELLOSSOM_SPECIES_FILE,
        typeFiles = listOf(TYPE_GRASS_FILE),
        varietyFiles = emptyList(),
        chainFile = POKEMON_BELLOSSOM_CHAIN_FILE
    ),
    POKEMON_DITTO_NAME to TestModel(
        pokemonFile = POKEMON_DITTO_FILE,
        speciesFile = POKEMON_DITTO_SPECIES_FILE,
        typeFiles = listOf(TYPE_NORMAL_FILE),
        varietyFiles = emptyList(),
        chainFile = POKEMON_DITTO_CHAIN_FILE
    ),
    POKEMON_LAPRAS_NAME to TestModel(
        pokemonFile = POKEMON_LAPRAS_FILE,
        speciesFile = POKEMON_LAPRAS_SPECIES_FILE,
        typeFiles = listOf(TYPE_WATER_FILE, TYPE_ICE_FILE),
        varietyFiles = listOf(POKEMON_LAPRAS_VARIETY_FILE),
        chainFile = POKEMON_LAPRAS_CHAIN_FILE
    )
)
