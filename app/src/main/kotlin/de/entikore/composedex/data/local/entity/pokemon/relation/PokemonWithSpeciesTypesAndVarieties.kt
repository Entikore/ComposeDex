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
package de.entikore.composedex.data.local.entity.pokemon.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.entikore.composedex.data.local.entity.pokemon.PokemonEntity
import de.entikore.composedex.data.local.entity.species.SpeciesEntity
import de.entikore.composedex.data.local.entity.type.TypeEntity
import de.entikore.composedex.data.local.entity.type.asExternalModel
import de.entikore.composedex.data.local.entity.variety.VarietyEntity
import de.entikore.composedex.data.local.entity.variety.asExternalModel
import de.entikore.composedex.domain.model.pokemon.ChainLink
import de.entikore.composedex.domain.model.pokemon.EvolutionRank
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.pokemon.PokemonLabel
import de.entikore.composedex.domain.model.pokemon.PokemonLabels
import de.entikore.composedex.domain.model.pokemon.PokemonShape
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Represents a [PokemonEntity] with its associated [SpeciesEntity], [TypeEntity], and [VarietyEntity].
 *
 * This data class combines data from multiple tables using Room's `@Relation` annotation
 * to provide a complete representation of a Pokémon and its related information.
 *
 * @property pokemon The core Pokémon data.
 * @property species The species of the Pokémon.
 * @property types A list of types associated with the Pokémon.
 * @property varieties A list of varieties associated with the Pokémon.
 */
data class PokemonWithSpeciesTypesAndVarieties(
    @Embedded val pokemon: PokemonEntity,
    @Relation(
        parentColumn = "pokemonId",
        entityColumn = "speciesId",
        associateBy = Junction(PokemonSpeciesCrossRef::class)
    )
    val species: SpeciesEntity,
    @Relation(
        parentColumn = "pokemonId",
        entityColumn = "typeId",
        associateBy = Junction(PokemonTypeCrossRef::class)
    )
    val types: List<TypeEntity>,
    @Relation(
        parentColumn = "pokemonId",
        entityColumn = "varietyName",
        associateBy = Junction(PokemonVarietyCrossRef::class)
    )
    val varieties: List<VarietyEntity>
)

/**
 * Converts a [PokemonWithSpeciesTypesAndVarieties] to an [Pokemon].
 */
fun PokemonWithSpeciesTypesAndVarieties.asExternalModel(): Pokemon {
    return Pokemon(
        id = pokemon.pokemonId,
        name = pokemon.pokemonName,
        defaultName = species.name,
        height = heightInMeter(pokemon.height),
        weight = weightInKilo(pokemon.weight),
        types = types.map { it.asExternalModel() },
        stats = pokemon.stats,
        pokemonLabel = PokemonLabels(
            baby = PokemonLabel.BABY to species.isBaby,
            legendary = PokemonLabel.LEGENDARY to species.isLegendary,
            mystical = PokemonLabel.MYSTICAL to species.isMythical
        ),
        textEntries = species.flavorTextEntries,
        evolutionRank = getEvolutionRank(species.name, species.evolutionChain),
        evolvesFrom = species.evolvesFrom,
        evolutionChain = species.evolutionChain,
        genera = species.genera,
        varieties = varieties.asExternalModel(),
        remoteArtwork = pokemon.artwork,
        artwork = pokemon.localArtwork,
        remoteSprite = pokemon.sprite,
        sprite = pokemon.localSprite,
        remoteCry = pokemon.cry,
        cry = pokemon.localCry,
        isFavourite = pokemon.isFavourite,
        shape = PokemonShape.getShape(species.shape)

    )
}

/**
 * Converts a list of [PokemonWithSpeciesTypesAndVarieties] to a list of [Pokemon].
 */
@JvmName("pokemonWithSpeciesTypesAndVarietiesToExternal")
fun List<PokemonWithSpeciesTypesAndVarieties>.asExternalModel() = map(
    PokemonWithSpeciesTypesAndVarieties::asExternalModel
)

private const val UNITS_PER_BASE_UNIT = 10.0

// The weight is specified in hectograms
private fun weightInKilo(weight: Int): String {
    val inKilo = weight / UNITS_PER_BASE_UNIT
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(inKilo)
}

// The height is specified in decimeters
private fun heightInMeter(height: Int): String {
    val inMeter = height / UNITS_PER_BASE_UNIT
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(inMeter)
}

private fun getEvolutionRank(
    name: String,
    evolutionChain: Map<Int, List<ChainLink>>
): EvolutionRank {
    val containsBaby = evolutionChain.any {
        it.value.any { chainLink -> chainLink.isBaby }
    }
    val stage = evolutionChain.entries.firstOrNull { (_, chainLinks) ->
        chainLinks.any { it.name == name }
    }?.key ?: 0

    return when (stage) {
        1 -> if (containsBaby) EvolutionRank.BASIC else EvolutionRank.STAGE1
        2 -> if (containsBaby) EvolutionRank.STAGE1 else EvolutionRank.STAGE2
        else -> if (containsBaby) EvolutionRank.BABY else EvolutionRank.BASIC
    }
}
