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
import de.entikore.composedex.data.local.entity.type.TypeEntity
import de.entikore.composedex.data.remote.model.common.Name
import de.entikore.composedex.data.remote.model.common.NamedApiResource

/**
 * Types are properties for Pokémon and their moves. Each type has three properties: which types of
 * Pokémon it is super effective against, which types of Pokémon it is not very effective against,
 * and which types of Pokémon it is completely ineffective against.
 *
 * @property damageRelations A detail of how effective this type is toward others and vice versa.
 * @property generation The generation this type was introduced in.
 * @property id The identifier for this resource.
 * @property moveDamageClass The class of damage inflicted by this type.
 * @property name The name for this resource.
 * @property names The name of this resource listed in different languages.
 * @property pokemon A list of details of Pokémon that have this type.
 */
data class TypeRemote(
    @Json(name = "damage_relations") val damageRelations: DamageRelationsRemote,
    val generation: NamedApiResource,
    val id: Int,
    @Json(name = "move_damage_class") val moveDamageClass: NamedApiResource?,
    val name: String,
    val names: List<Name>,
    val pokemon: List<TypePokemon>
)

/**
 * Converts a [TypeRemote] to a [TypeEntity].
 */
fun TypeRemote.toEntity() =
    TypeEntity(
        typeId = id,
        typeName = name,
        pokemonOfType = pokemon.map { pokemon -> pokemon.pokemon.name },
        doubleDamageFrom = damageRelations.doubleDamageFrom.map { it.name },
        doubleDamageTo = damageRelations.doubleDamageTo.map { it.name },
        halfDamageFrom = damageRelations.halfDamageFrom.map { it.name },
        halfDamageTo = damageRelations.halfDamageTo.map { it.name },
        noDamageFrom = damageRelations.noDamageFrom.map { it.name },
        noDamageTo = damageRelations.noDamageTo.map { it.name }
    )

/**
 * Converts a list of [TypeRemote] to a list of [TypeEntity].
 */
@JvmName("typeListToEntity")
fun List<TypeRemote>.toEntity() = map(TypeRemote::toEntity)
