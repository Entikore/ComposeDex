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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

fun createGradientBrush(colors: List<Color>, isVertical: Boolean = true): Brush {
    val brushColors = mutableListOf<Color>()
    brushColors.addAll(colors)
    if (colors.size < 2) {
        brushColors.addAll(colors)
    }
    brushColors.map { it }
    val endOffset =
        if (isVertical) {
            Offset(0f, Float.POSITIVE_INFINITY)
        } else {
            Offset(Float.POSITIVE_INFINITY, 0f)
        }

    return Brush.linearGradient(
        colors = brushColors,
        start = Offset(0f, 0f),
        end = endOffset,
        tileMode = TileMode.Clamp
    )
}
