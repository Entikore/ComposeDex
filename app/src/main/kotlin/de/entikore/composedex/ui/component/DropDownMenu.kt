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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.pokemon.PokemonShape
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_BACKGROUND
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_BORDER
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_PRIMARY

@Composable
fun DropDownMenuWithFilterOptions(
    showDropDownMenu: Boolean,
    changeVisibility: () -> Unit,
    filterText: String,
    changeFilterText: (String) -> Unit,
    checkBoxItems: List<Pair<String, Boolean>>,
    changeCheckBoxItem: (String, Boolean) -> Unit,
    shapeFiler: Pair<List<PokemonShape>, PokemonShape>,
    changeShapeFilter: (shape: PokemonShape) -> Unit,
    modifier: Modifier = Modifier,
) {
    CustomDropDownMenu(
        menuExpanded = showDropDownMenu,
        onDismissRequest = changeVisibility,
        modifier =
        modifier.background(
            color = TYPE_TCG_COLORLESS_BACKGROUND
        )
    ) {
        val menuModifier =
            Modifier
                .padding(
                    vertical = dimensionResource(id = R.dimen.medium_padding),
                    horizontal = dimensionResource(
                        id = R.dimen.standard_padding
                    )
                )
                .clip(shape = CutCornerShape(integerResource(id = R.integer.medium_cut_corner_shape_percentage)))
                .border(
                    dimensionResource(id = R.dimen.default_border),
                    TYPE_TCG_COLORLESS_BORDER,
                    CutCornerShape(integerResource(id = R.integer.medium_cut_corner_shape_percentage))
                )

        DropdownMenuItemTextField(
            onClick = {},
            labelText = "search name",
            textFieldValue = filterText,
            onValueChange = changeFilterText,
            textFieldColors =
            TextFieldDefaults.colors(
                focusedContainerColor = TYPE_TCG_COLORLESS_BACKGROUND,
                unfocusedContainerColor = TYPE_TCG_COLORLESS_PRIMARY,
                focusedLabelColor = TYPE_TCG_COLORLESS_PRIMARY,
                unfocusedLabelColor = TYPE_TCG_COLORLESS_BORDER,
                cursorColor = TYPE_TCG_COLORLESS_BORDER
            ),
            modifier = menuModifier
        )
        checkBoxItems.forEach { item ->
            DropdownMenuItemCheckbox(
                onClick = {},
                checkBoxState = item.second,
                checkBoxText = item.first,
                onCheckedClick = { newCheck -> changeCheckBoxItem(item.first, newCheck) },
                modifier = menuModifier
            )
        }
        DropdownMenuItemMultiSelectList(shapeFiler, changeShapeFilter, menuModifier)
    }
}

@Composable
fun CustomDropDownMenu(
    menuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    menuItems: @Composable () -> Unit
) {
    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .border(dimensionResource(id = R.dimen.small_padding), Color.Black)
            .padding(horizontal = dimensionResource(R.dimen.medium_padding))
    ) {
        menuItems()
    }
}

@Composable
fun DropdownMenuItemTextField(
    onClick: () -> Unit,
    labelText: String,
    textFieldValue: String,
    onValueChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            TextField(
                value = textFieldValue,
                onValueChange = onValueChange,
                label = { Text(text = labelText) },
                colors = textFieldColors,
                trailingIcon = {
                    if (textFieldValue.isNotEmpty()) {
                        IconButton(onClick = { onValueChange("") }) {
                            Icon(
                                painterResource(id = R.drawable.close),
                                contentDescription = stringResource(R.string.description_delete_icon)
                            )
                        }
                    }
                }
            )
        },
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun DropdownMenuItemCheckbox(
    onClick: () -> Unit,
    checkBoxState: Boolean,
    checkBoxText: String,
    onCheckedClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(onClick = onClick, modifier = modifier, enabled = false, text = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(checked = checkBoxState, onCheckedChange = onCheckedClick)
            Text(text = checkBoxText)
        }
    })
}

@Composable
fun DropdownMenuItemMultiSelectList(
    shapes: Pair<List<PokemonShape>, PokemonShape>,
    changeShapeFilter: (shape: PokemonShape) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.ArrowDropDown
    }

    DropdownMenuItem(onClick = {}, modifier = modifier, enabled = false, text = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(
                vertical = dimensionResource(id = R.dimen.small_padding),
                horizontal = dimensionResource(
                    id = R.dimen.small_padding
                )
            )
        ) {
            TextField(
                readOnly = true,
                enabled = false,
                singleLine = true,
                value = shapes.second.name,
                onValueChange = {},
                label = { Text("Shape") },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(icon, "contentDescription")
                    }
                }
            )

            if (expanded) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.standard_padding))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = dimensionResource(id = R.dimen.small_padding),
                                horizontal = dimensionResource(
                                    id = R.dimen.medium_padding
                                )
                            )
                            .clip(
                                shape = CutCornerShape(
                                    integerResource(id = R.integer.medium_cut_corner_shape_percentage)
                                )
                            )
                            .border(
                                dimensionResource(id = R.dimen.default_border),
                                TYPE_TCG_COLORLESS_BORDER,
                                CutCornerShape(integerResource(id = R.integer.medium_cut_corner_shape_percentage))
                            )
                            .clickable {
                                changeShapeFilter(PokemonShape.UNDEFINED)
                                expanded = false
                            }
                    ) {
                        Text(
                            text = "no filter",
                            modifier = Modifier.padding(
                                vertical = dimensionResource(id = R.dimen.medium_padding),
                                horizontal = dimensionResource(
                                    id = R.dimen.standard_padding
                                )
                            )
                        )
                    }
                    shapes.first.forEach {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = dimensionResource(id = R.dimen.small_padding),
                                    horizontal = dimensionResource(
                                        id = R.dimen.medium_padding
                                    )
                                )
                                .clip(
                                    shape = CutCornerShape(
                                        integerResource(id = R.integer.medium_cut_corner_shape_percentage)
                                    )
                                )
                                .border(
                                    dimensionResource(id = R.dimen.default_border),
                                    TYPE_TCG_COLORLESS_BORDER,
                                    CutCornerShape(integerResource(id = R.integer.medium_cut_corner_shape_percentage))
                                )
                                .clickable {
                                    changeShapeFilter(it)
                                    expanded = false
                                }
                        ) {
                            Text(
                                text = it.name,
                                modifier = Modifier.padding(
                                    vertical = dimensionResource(id = R.dimen.medium_padding),
                                    horizontal = dimensionResource(
                                        id = R.dimen.standard_padding
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    })
}
