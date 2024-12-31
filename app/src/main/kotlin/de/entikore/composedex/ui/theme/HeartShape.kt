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
package de.entikore.composedex.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * A shape representing a heart.
 *
 * This shape is created using cubic Bézier curves to form the characteristic
 * heart shape. The logic for generating the path is based on a Stack Overflow
 * answer ([answer](https://stackoverflow.com/a/41251829/5348665)) and
 * adapted for use in Jetpack Compose.
 *
 * The heart shape is centered within the given size and scales proportionally
 * to fill the available space.
 */
@Suppress("MagicNumber")
class HeartShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height

            moveTo(width / 2, height / 5)
            cubicTo(
                5 * width / 14,
                0f,
                0f,
                height / 15,
                width / 28,
                2 * height / 5
            )
            cubicTo(
                width / 14,
                2 * height / 3,
                3 * width / 7,
                5 * height / 6,
                width / 2,
                height
            )
            cubicTo(
                4 * width / 7,
                5 * height / 6,
                13 * width / 14,
                2 * height / 3,
                27 * width / 28,
                2 * height / 5
            )
            cubicTo(
                width,
                height / 15,
                9 * width / 14,
                0f,
                width / 2,
                height / 5
            )
        }
        return Outline.Generic(path)
    }
}
