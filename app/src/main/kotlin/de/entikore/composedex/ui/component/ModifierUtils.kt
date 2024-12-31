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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

fun Modifier.cutCornerShapeBackgroundWithBorder(
    shape: Int,
    color: Color,
    borderWidth: Dp,
    borderColor: Color
) = this.background(
    color,
    CutCornerShape(shape)
).cutCornerShapeBorder(
    cutCornerPercentage = shape,
    borderWidth = borderWidth,
    borderColor = borderColor
)

fun Modifier.cutCornerShapeBorder(
    cutCornerPercentage: Int,
    borderWidth: Dp,
    borderColor: Color
): Modifier =
    this.border(
        width = borderWidth,
        color = borderColor,
        shape = CutCornerShape(cutCornerPercentage)
    )
