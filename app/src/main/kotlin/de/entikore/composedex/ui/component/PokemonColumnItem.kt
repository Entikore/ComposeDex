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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.pokemon.Pokemon

@Composable
fun PokemonColumnItem(
    pokemon: Pokemon,
    backgroundBrush: Brush,
    borderBrush: Brush,
    textColor: Color,
    navigateToPokemon: (String) -> Unit,
    updateFavourite: (id: Int, isFavourite: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(CutCornerShape(integerResource(id = R.integer.default_cut_corner_shape_percentage)))
            .background(backgroundBrush)
            .border(
                border = BorderStroke(
                    width = dimensionResource(id = R.dimen.default_border),
                    brush = borderBrush
                ),
                shape = CutCornerShape(integerResource(R.integer.default_cut_corner_shape_percentage))
            )
            .clickable { navigateToPokemon(pokemon.name) }
    ) {
        AsyncImage(
            model = pokemon.sprite,
            contentDescription = stringResource(R.string.cD_display_image_of, pokemon.name),
            modifier = Modifier
                .weight(0.3f)
                .padding(start = dimensionResource(id = R.dimen.medium_padding))
                .fillMaxSize()
        )

        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .align(Alignment.Center)
            ) {
                items(pokemon.types) {
                    AsyncImage(
                        model = Icon(
                            painter = getTypeIcon(icon = it.name),
                            contentDescription = null,
                            tint = getTypePrimaryColor(it.name),
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .alpha(0.2f)
                        ),
                        contentDescription = stringResource(R.string.cD_type_icon_of, it.name),
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.standard_padding))
                    )
                }
            }
            Text(
                text = stringResource(
                    R.string.generation_screen_pokemon_id_and_name,
                    pokemon.id,
                    pokemon.name.replaceFirstChar { it.uppercaseChar() }
                ),
                fontStyle = MaterialTheme.typography.labelMedium.fontStyle,
                color = textColor,
                modifier = Modifier
                    .zIndex(2f)
                    .align(Alignment.Center)
            )
        }

        IconButton(
            onClick = { updateFavourite(pokemon.id, !pokemon.isFavourite) },
            modifier = Modifier
                .weight(0.1f)
                .padding(end = dimensionResource(id = R.dimen.medium_padding))
        ) {
            Icon(
                painterResource(
                    id = if (pokemon.isFavourite) R.drawable.ic_favourite else R.drawable.ic_favourite_border
                ),
                contentDescription = stringResource(
                    R.string.cD_is_pokemon_marked_as_favourite,
                    pokemon.name
                ),
                tint = getTypeBorderColor(pokemon.types.first().name)
            )
        }
    }
}
