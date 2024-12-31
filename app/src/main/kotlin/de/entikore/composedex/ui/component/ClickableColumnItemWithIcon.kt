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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import de.entikore.composedex.R

@Composable
fun ClickableColumnItem(
    text: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Button(
            onClick = { onClick(text) },
            shape = CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(id = R.dimen.standard_padding),
                vertical = dimensionResource(id = R.dimen.medium_padding)
            ),
            colors = ButtonDefaults.outlinedButtonColors(),
            border = BorderStroke(
                width = dimensionResource(id = R.dimen.default_border),
                color = borderColor,
            ),
            modifier = Modifier.fillMaxWidth().background(
                backgroundColor,
                CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage))
            )
        ) {
            Text(
                text = text,
                fontStyle = MaterialTheme.typography.labelMedium.fontStyle,
                color = textColor,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.large_padding))
            )
        }
    }
}

@Composable
fun ClickableColumnItemWithIcon(
    text: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Button(
            onClick = { onClick(text) },
            shape = CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(id = R.dimen.standard_padding),
                vertical = dimensionResource(id = R.dimen.medium_padding)
            ),
            colors = ButtonDefaults.outlinedButtonColors(),
            border = BorderStroke(
                width = dimensionResource(id = R.dimen.default_border),
                color = borderColor,
            ),
            modifier = Modifier.fillMaxWidth().background(
                backgroundColor,
                CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage))
            )
        ) {
            icon()
            Spacer(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.default_spacer))
                    .weight(0.2f)
            )
            Text(
                text = text,
                fontStyle = MaterialTheme.typography.labelMedium.fontStyle,
                color = textColor,
                modifier = Modifier
                    .weight(0.6f)
                    .padding(end = dimensionResource(id = R.dimen.large_padding))
            )
        }
    }
}
