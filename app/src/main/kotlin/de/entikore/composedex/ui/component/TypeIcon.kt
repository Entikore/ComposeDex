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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.type.Type
import de.entikore.composedex.ui.theme.TYPE_BUG_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_BUG_BORDER
import de.entikore.composedex.ui.theme.TYPE_BUG_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_DARK_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_DARK_BORDER
import de.entikore.composedex.ui.theme.TYPE_DARK_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_DRAGON_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_DRAGON_BORDER
import de.entikore.composedex.ui.theme.TYPE_DRAGON_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_ELECTRIC_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_ELECTRIC_BORDER
import de.entikore.composedex.ui.theme.TYPE_ELECTRIC_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_FAIRY_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_FAIRY_BORDER
import de.entikore.composedex.ui.theme.TYPE_FAIRY_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_FIGHTING_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_FIGHTING_BORDER
import de.entikore.composedex.ui.theme.TYPE_FIGHTING_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_FIRE_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_FIRE_BORDER
import de.entikore.composedex.ui.theme.TYPE_FIRE_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_FLYING_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_FLYING_BORDER
import de.entikore.composedex.ui.theme.TYPE_FLYING_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_GHOST_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_GHOST_BORDER
import de.entikore.composedex.ui.theme.TYPE_GHOST_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_GRASS_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_GRASS_BORDER
import de.entikore.composedex.ui.theme.TYPE_GRASS_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_GROUND_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_GROUND_BORDER
import de.entikore.composedex.ui.theme.TYPE_GROUND_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_ICE_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_ICE_BORDER
import de.entikore.composedex.ui.theme.TYPE_ICE_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_NORMAL_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_NORMAL_BORDER
import de.entikore.composedex.ui.theme.TYPE_NORMAL_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_POISON_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_POISON_BORDER
import de.entikore.composedex.ui.theme.TYPE_POISON_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_PSYCHIC_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_PSYCHIC_BORDER
import de.entikore.composedex.ui.theme.TYPE_PSYCHIC_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_ROCK_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_ROCK_BORDER
import de.entikore.composedex.ui.theme.TYPE_ROCK_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_STEEL_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_STEEL_BORDER
import de.entikore.composedex.ui.theme.TYPE_STEEL_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_BORDER
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_PRIMARY
import de.entikore.composedex.ui.theme.TYPE_WATER_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_WATER_BORDER
import de.entikore.composedex.ui.theme.TYPE_WATER_PRIMARY

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TypeIcon(
    type: String,
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    onClick: (String) -> Unit = {},
    onLongClick: (String) -> Unit = {}
) {
    val shape = CircleShape
    val borderColor = if (selected) getTypeBorderColor(type = type) else TYPE_TCG_COLORLESS_BORDER
    val primaryColor =
        if (selected) getTypePrimaryColor(type = type) else TYPE_TCG_COLORLESS_PRIMARY
    Icon(
        painter = getTypeIcon(icon = type),
        contentDescription = type,
        tint = borderColor,
        modifier =
        modifier
            .background(primaryColor, shape)
            .border(dimensionResource(id = R.dimen.default_border), borderColor, shape)
            .clip(shape)
            .combinedClickable(
                onClickLabel = stringResource(R.string.label_type_icon_click),
                onClick = { onClick(type) },
                onLongClickLabel = stringResource(R.string.label_type_icon_long_click),
                onLongClick = { onLongClick(type) }
            )
            .padding(dimensionResource(id = R.dimen.standard_padding))

    )
}

private val typeIcons = mapOf(
    Type.BUG to R.drawable.ic_bug,
    Type.DARK to R.drawable.ic_dark,
    Type.DRAGON to R.drawable.ic_dragon,
    Type.ELECTRIC to R.drawable.ic_electric,
    Type.FAIRY to R.drawable.ic_fairy,
    Type.FIGHTING to R.drawable.ic_fighting,
    Type.FIRE to R.drawable.ic_fire,
    Type.FLYING to R.drawable.ic_flying,
    Type.GHOST to R.drawable.ic_ghost,
    Type.GRASS to R.drawable.ic_grass,
    Type.GROUND to R.drawable.ic_ground,
    Type.ICE to R.drawable.ic_ice,
    Type.NORMAL to R.drawable.ic_normal,
    Type.POISON to R.drawable.ic_poison,
    Type.PSYCHIC to R.drawable.ic_psychic,
    Type.ROCK to R.drawable.ic_rock,
    Type.STEEL to R.drawable.ic_steel,
    Type.WATER to R.drawable.ic_water
)

@Composable
fun getTypeIcon(icon: String?): Painter {
    val iconResId = typeIcons[icon] ?: throw IllegalArgumentException("undefined type: $icon")
    return painterResource(id = iconResId)
}

fun getTypePrimaryColor(type: String?): Color {
    return typePrimaryColors[type] ?: TYPE_TCG_COLORLESS_PRIMARY
}

fun getTypeBorderColor(type: String?): Color {
    return typeBorderColors[type] ?: TYPE_TCG_COLORLESS_BORDER
}

fun getTypeBackgroundColor(type: String?): Color {
    return typeBackgroundColors[type] ?: TYPE_TCG_COLORLESS_BACKGROUND
}

private val typePrimaryColors = mapOf(
    Type.BUG to TYPE_BUG_PRIMARY,
    Type.DARK to TYPE_DARK_PRIMARY,
    Type.DRAGON to TYPE_DRAGON_PRIMARY,
    Type.ELECTRIC to TYPE_ELECTRIC_PRIMARY,
    Type.FAIRY to TYPE_FAIRY_PRIMARY,
    Type.FIGHTING to TYPE_FIGHTING_PRIMARY,
    Type.FIRE to TYPE_FIRE_PRIMARY,
    Type.FLYING to TYPE_FLYING_PRIMARY,
    Type.GHOST to TYPE_GHOST_PRIMARY,
    Type.GRASS to TYPE_GRASS_PRIMARY,
    Type.GROUND to TYPE_GROUND_PRIMARY,
    Type.ICE to TYPE_ICE_PRIMARY,
    Type.NORMAL to TYPE_NORMAL_PRIMARY,
    Type.POISON to TYPE_POISON_PRIMARY,
    Type.PSYCHIC to TYPE_PSYCHIC_PRIMARY,
    Type.ROCK to TYPE_ROCK_PRIMARY,
    Type.STEEL to TYPE_STEEL_PRIMARY,
    Type.WATER to TYPE_WATER_PRIMARY
)

private val typeBorderColors = mapOf(
    Type.BUG to TYPE_BUG_BORDER,
    Type.DARK to TYPE_DARK_BORDER,
    Type.DRAGON to TYPE_DRAGON_BORDER,
    Type.ELECTRIC to TYPE_ELECTRIC_BORDER,
    Type.FAIRY to TYPE_FAIRY_BORDER,
    Type.FIGHTING to TYPE_FIGHTING_BORDER,
    Type.FIRE to TYPE_FIRE_BORDER,
    Type.FLYING to TYPE_FLYING_BORDER,
    Type.GHOST to TYPE_GHOST_BORDER,
    Type.GRASS to TYPE_GRASS_BORDER,
    Type.GROUND to TYPE_GROUND_BORDER,
    Type.ICE to TYPE_ICE_BORDER,
    Type.NORMAL to TYPE_NORMAL_BORDER,
    Type.POISON to TYPE_POISON_BORDER,
    Type.PSYCHIC to TYPE_PSYCHIC_BORDER,
    Type.ROCK to TYPE_ROCK_BORDER,
    Type.STEEL to TYPE_STEEL_BORDER,
    Type.WATER to TYPE_WATER_BORDER
)

private val typeBackgroundColors = mapOf(
    Type.BUG to TYPE_BUG_BACKGROUND,
    Type.DARK to TYPE_DARK_BACKGROUND,
    Type.DRAGON to TYPE_DRAGON_BACKGROUND,
    Type.ELECTRIC to TYPE_ELECTRIC_BACKGROUND,
    Type.FAIRY to TYPE_FAIRY_BACKGROUND,
    Type.FIGHTING to TYPE_FIGHTING_BACKGROUND,
    Type.FIRE to TYPE_FIRE_BACKGROUND,
    Type.FLYING to TYPE_FLYING_BACKGROUND,
    Type.GHOST to TYPE_GHOST_BACKGROUND,
    Type.GRASS to TYPE_GRASS_BACKGROUND,
    Type.GROUND to TYPE_GROUND_BACKGROUND,
    Type.ICE to TYPE_ICE_BACKGROUND,
    Type.NORMAL to TYPE_NORMAL_BACKGROUND,
    Type.POISON to TYPE_POISON_BACKGROUND,
    Type.PSYCHIC to TYPE_PSYCHIC_BACKGROUND,
    Type.ROCK to TYPE_ROCK_BACKGROUND,
    Type.STEEL to TYPE_STEEL_BACKGROUND,
    Type.WATER to TYPE_WATER_BACKGROUND
)
