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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.preferences.TypeThemeConfig
import de.entikore.composedex.ui.theme.ComposeDexTheme

@Composable
fun ErrorMessage(errorMessage: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.medium_padding))
            .border(
                width = dimensionResource(id = R.dimen.default_border),
                MaterialTheme.colorScheme.onErrorContainer,
                shape = CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage))
            )
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage))
            )
            .clip(shape = CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))
            .padding(dimensionResource(id = R.dimen.medium_padding))
    ) {
        Text(
            text = stringResource(id = R.string.ui_error_head),
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding))
        )
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding))
        )
    }
}

@Preview
@Composable
private fun ErrorMessagePreview() {
    ComposeDexTheme(appTheme = TypeThemeConfig.COLORLESS) {
        ErrorMessage(errorMessage = "Could not find Pokemon")
    }
}
