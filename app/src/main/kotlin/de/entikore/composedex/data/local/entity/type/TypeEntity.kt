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
package de.entikore.composedex.data.local.entity.type

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.entikore.composedex.data.remote.model.type.TypeRemote
import de.entikore.composedex.domain.model.type.Type

/**
 * Local representation of [TypeRemote].
 */
@Entity(tableName = "type")
data class TypeEntity(
    @PrimaryKey(autoGenerate = false) val typeId: Int,
    val typeName: String,
    val pokemonOfType: List<String>,
    val doubleDamageFrom: List<String>,
    val doubleDamageTo: List<String>,
    val halfDamageFrom: List<String>,
    val halfDamageTo: List<String>,
    val noDamageFrom: List<String>,
    val noDamageTo: List<String>
)

fun TypeEntity.asExternalModel() =
    Type(
        name = typeName,
        pokemonOfType = pokemonOfType,
        doubleDamageFrom = doubleDamageFrom,
        doubleDamageTo = doubleDamageTo,
        halfDamageFrom = halfDamageFrom,
        halfDamageTo = halfDamageTo,
        noDamageFrom = noDamageFrom,
        noDamageTo = noDamageTo
    )

@JvmName("typeListAsExternalModel")
fun List<TypeEntity>.asExternalModel() = map(TypeEntity::asExternalModel).ifEmpty {
    listOf(Type.getBackupType())
}
