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
package de.entikore.composedex.ui.component

/**
 * Data class representing a set of filter options.
 *
 * Holds the state of various filters.
 * Provides a function to update the state of individual filters.
 */
data class FilterOptions(
    val baby: Pair<String, Boolean> = Pair(BABY_FILTER, false),
    val legendary: Pair<String, Boolean> = Pair(LEGENDARY_FILTER, false),
    val mystical: Pair<String, Boolean> = Pair(MYSTICAL_FILTER, false),
    val favourite: Pair<String, Boolean> = Pair(FAVOURITE_FILTER, false)
) {
    fun update(option: String, newValue: Boolean): FilterOptions {
        return when (option) {
            BABY_FILTER -> this.copy(baby = Pair(BABY_FILTER, newValue))
            LEGENDARY_FILTER -> this.copy(legendary = Pair(LEGENDARY_FILTER, newValue))
            MYSTICAL_FILTER -> this.copy(mystical = Pair(MYSTICAL_FILTER, newValue))
            FAVOURITE_FILTER -> this.copy(favourite = Pair(FAVOURITE_FILTER, newValue))
            else -> this
        }
    }

    companion object {
        const val BABY_FILTER: String = "Baby"
        const val LEGENDARY_FILTER: String = "Legendary"
        const val MYSTICAL_FILTER: String = "Mystical"
        const val FAVOURITE_FILTER: String = "Favourite"
    }
}

fun FilterOptions.asCheckBoxItems(withFavouriteFilter: Boolean = true): List<Pair<String, Boolean>> {
    return mutableListOf(this.baby, this.legendary, this.mystical).also {
        if (withFavouriteFilter) {
            it.add(this.favourite)
        }
    }
}
