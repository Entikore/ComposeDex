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
package de.entikore.composedex.data.local.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.entikore.composedex.domain.model.pokemon.ChainLink
import javax.inject.Inject

/**
 * A [TypeConverter] to convert an evolution chain to and from a JSON format.
 */
@ProvidedTypeConverter
class ChainConverter @Inject constructor(val moshi: Moshi) {
    private val chainType =
        Types.newParameterizedType(
            Map::class.java,
            Integer::class.java,
            Types.newParameterizedType(
                List::class.java,
                ChainLink::class.java
            )
        )
    private val chainAdapter = moshi.adapter<Map<Int, List<ChainLink>>>(chainType)

    @TypeConverter
    fun chainFromJson(string: String): Map<Int, List<ChainLink>> {
        return chainAdapter.fromJson(string).orEmpty()
    }

    @TypeConverter
    fun chainToJson(stats: Map<Int, List<ChainLink>>): String {
        return chainAdapter.toJson(stats)
    }
}
