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
import javax.inject.Inject

/**
 * A [TypeConverter] to convert Pokemon type to and from a JSON format.
 */
@ProvidedTypeConverter
class TypesConverter @Inject constructor(val moshi: Moshi) {
    private val typesType = Types.newParameterizedType(List::class.java, String::class.java)
    private val typesAdapter = moshi.adapter<List<String>>(typesType)

    @TypeConverter
    fun typesFromJson(string: String): List<String> {
        return typesAdapter.fromJson(string).orEmpty()
    }

    @TypeConverter
    fun typesToJson(types: List<String>): String {
        return typesAdapter.toJson(types)
    }
}
