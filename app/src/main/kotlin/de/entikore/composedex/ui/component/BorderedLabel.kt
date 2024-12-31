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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.tooling.preview.Preview
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.preferences.TypeThemeConfig
import de.entikore.composedex.ui.theme.ComposeDexTheme

@Composable
fun BorderedLabel(labelText: String, modifier: Modifier = Modifier) {
    Text(
        text = labelText,
        color = MaterialTheme.typography.labelSmall.color,
        fontSize = MaterialTheme.typography.labelSmall.fontSize,
        modifier =
        modifier
            .cutCornerShapeBorder(
                cutCornerPercentage = integerResource(id = R.integer.default_cut_corner_shape_percentage),
                borderWidth = dimensionResource(id = R.dimen.default_border),
                borderColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            .clip(shape = CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))
            .padding(
                start = dimensionResource(id = R.dimen.medium_padding),
                top = dimensionResource(id = R.dimen.medium_padding),
                end = dimensionResource(id = R.dimen.medium_padding),
                bottom = dimensionResource(id = R.dimen.medium_padding)
            )
    )
}

@Preview
@Composable
private fun PreviewBorderedLabel() {
    ComposeDexTheme(appTheme = TypeThemeConfig.COLORLESS) {
        BorderedLabel(labelText = "LabelText")
    }
}
