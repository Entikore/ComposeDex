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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import de.entikore.composedex.R

@Composable
fun LazyTypeRowWithTopLabel(
    typeNames: List<String>,
    labelText: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (String) -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.Start, modifier = modifier) {
        BorderedLabel(
            labelText = labelText
        )
        Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.small_padding)))
        TypeRow(typeNames, onClick = onClick, onLongClick = onLongClick)
        Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.small_padding)))
    }
}

@Composable
fun TypeRow(
    typeNames: List<String>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
    onLongClick: (String) -> Unit = {}
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        items(typeNames) { type ->
            TypeIcon(
                modifier = Modifier
                    .padding(
                        start = dimensionResource(id = R.dimen.small_padding),
                        end = dimensionResource(id = R.dimen.small_padding),
                        top = dimensionResource(id = R.dimen.small_padding)
                    )
                    .size(dimensionResource(id = R.dimen.type_icon_size)),
                type = type,
                onClick = onClick,
                onLongClick = onLongClick
            )
        }
    }
}
