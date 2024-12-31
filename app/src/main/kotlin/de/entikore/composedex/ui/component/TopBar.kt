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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.pokemon.PokemonShape
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_BORDER
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_PRIMARY

@Composable
fun TopBarWithSearchbarAndFilter(
    title: String,
    openDrawer: () -> Unit,
    searchOnClick: (String) -> Unit,
    searchText: String,
    changeSearchText: (String) -> Unit,
    checkBoxItems: List<Pair<String, Boolean>>,
    changeFilter: (String, Boolean) -> Unit,
    shapeFilter: Pair<List<PokemonShape>, PokemonShape>,
    changeShapeFilter: (shape: PokemonShape) -> Unit,
    modifier: Modifier = Modifier
) {
    val showDropDownMenu = remember { mutableStateOf(false) }

    TopBarWithSearchbar(
        title = title,
        openDrawer = openDrawer,
        searchOnClick = searchOnClick,
        modifier = modifier
    ) {
        IconButton(
            onClick = { showDropDownMenu.value = !showDropDownMenu.value }
        ) {
            Icon(
                painterResource(id = R.drawable.more_vert),
                contentDescription = stringResource(R.string.open_drop_down_menu)
            )
        }
        DropDownMenuWithFilterOptions(
            showDropDownMenu = showDropDownMenu.value,
            changeVisibility = { showDropDownMenu.value = false },
            filterText = searchText,
            changeFilterText = changeSearchText,
            checkBoxItems = checkBoxItems,
            changeCheckBoxItem = changeFilter,
            shapeFiler = shapeFilter,
            changeShapeFilter = changeShapeFilter,
        )
    }
}

@Composable
fun TopBarWithFilter(
    title: String,
    openDrawer: () -> Unit,
    searchText: String,
    changeSearchText: (String) -> Unit,
    checkBoxItems: List<Pair<String, Boolean>>,
    changeFilter: (String, Boolean) -> Unit,
    shapeFilter: Pair<List<PokemonShape>, PokemonShape>,
    changeShapeFilter: (shape: PokemonShape) -> Unit,
    modifier: Modifier = Modifier
) {
    val showDropDownMenu = remember { mutableStateOf(false) }

    TopBar(title = title, openDrawer = openDrawer, modifier = modifier) {
        IconButton(
            onClick = { showDropDownMenu.value = !showDropDownMenu.value }
        ) {
            Icon(
                painterResource(id = R.drawable.more_vert),
                contentDescription = stringResource(R.string.description_show_more_options)
            )
        }

        DropDownMenuWithFilterOptions(
            showDropDownMenu = showDropDownMenu.value,
            changeVisibility = { showDropDownMenu.value = false },
            filterText = searchText,
            changeFilterText = changeSearchText,
            checkBoxItems = checkBoxItems,
            changeCheckBoxItem = changeFilter,
            shapeFiler = shapeFilter,
            changeShapeFilter = changeShapeFilter
        )
    }
}

@Composable
fun TopBarWithSearchbar(
    title: String,
    openDrawer: () -> Unit,
    searchOnClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    topBarEntry: (@Composable () -> Unit) = {}
) {
    var searchWidgetState by remember { mutableStateOf(SearchWidgetState.CLOSED) }
    val updateSearchWidgetState: (SearchWidgetState) -> Unit = { searchWidgetState = it }

    if (searchWidgetState == SearchWidgetState.OPENED) {
        ClosableSearchbar(
            searchWidgetState = searchWidgetState,
            openSearch = updateSearchWidgetState,
            searchOnClick = searchOnClick,
            modifier = modifier
        )
    } else {
        TopBar(title, openDrawer, modifier) {
            Row {
                IconButton(
                    onClick = {
                        if (searchWidgetState == SearchWidgetState.OPENED) {
                            updateSearchWidgetState(SearchWidgetState.CLOSED)
                        } else {
                            updateSearchWidgetState(SearchWidgetState.OPENED)
                        }
                    }
                ) {
                    Icon(painterResource(id = R.drawable.search), contentDescription = "")
                }
                topBarEntry()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    action: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(
                onClick = { openDrawer() },
                modifier = Modifier.testTag(stringResource(R.string.test_tag_open_drawer))
            ) {
                Icon(
                    painterResource(id = R.drawable.menu),
                    contentDescription = stringResource(R.string.description_top_bar_menu_icon),
                )
            }
        },
        actions = { action() },
        colors = backgroundColor?.let { TopAppBarDefaults.topAppBarColors(containerColor = it) }
            ?: TopAppBarDefaults.topAppBarColors(),
        modifier = modifier
    )
}

@Composable
fun ClosableSearchbar(
    searchWidgetState: SearchWidgetState,
    openSearch: (SearchWidgetState) -> Unit,
    searchOnClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchTextState by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        TextField(
            colors = TextFieldDefaults.colors(
                cursorColor = TYPE_TCG_COLORLESS_BORDER,
                focusedContainerColor = TYPE_TCG_COLORLESS_PRIMARY,
                unfocusedContainerColor = TYPE_TCG_COLORLESS_PRIMARY,
                focusedIndicatorColor = TYPE_TCG_COLORLESS_BORDER,
                unfocusedIndicatorColor = TYPE_TCG_COLORLESS_BORDER,
                focusedTextColor = TYPE_TCG_COLORLESS_BORDER,
                unfocusedTextColor = TYPE_TCG_COLORLESS_BORDER,
                focusedPlaceholderColor = TYPE_TCG_COLORLESS_BORDER,
                unfocusedPlaceholderColor = TYPE_TCG_COLORLESS_BORDER,
                focusedTrailingIconColor = TYPE_TCG_COLORLESS_BORDER,
                unfocusedTrailingIconColor = TYPE_TCG_COLORLESS_BORDER,
                focusedLeadingIconColor = TYPE_TCG_COLORLESS_BORDER,
                unfocusedLeadingIconColor = TYPE_TCG_COLORLESS_BORDER
            ),
            modifier = Modifier.fillMaxWidth(),
            value = searchTextState,
            onValueChange = { searchTextState = it },
            placeholder = {
                Text(
                    modifier = Modifier,
                    text = "Search"
                )
            },
            singleLine = true,
            maxLines = 1,
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.search),
                    contentDescription = stringResource(R.string.description_search_icon)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (searchTextState.isNotEmpty() || searchTextState.isNotBlank()) {
                            searchTextState = ""
                        }
                        if (searchWidgetState == SearchWidgetState.OPENED) {
                            openSearch(SearchWidgetState.CLOSED)
                        } else {
                            openSearch(SearchWidgetState.OPENED)
                        }
                    }
                ) {
                    Icon(
                        painterResource(id = R.drawable.close),
                        contentDescription = stringResource(R.string.description_delete_icon)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions =
            KeyboardActions(
                onSearch = {
                    searchOnClick(searchTextState)
                    openSearch(SearchWidgetState.CLOSED)
                },
                onDone = { openSearch(SearchWidgetState.CLOSED) }
            )
        )
    }
}

@Composable
fun Searchbar(searchOnClick: (String) -> Unit, modifier: Modifier = Modifier) {
    var searchTextState by remember { mutableStateOf("") }

    TextField(
        colors = TextFieldDefaults.colors(
            cursorColor = TYPE_TCG_COLORLESS_BORDER,
            focusedContainerColor = TYPE_TCG_COLORLESS_PRIMARY,
            unfocusedContainerColor = TYPE_TCG_COLORLESS_PRIMARY,
            focusedIndicatorColor = TYPE_TCG_COLORLESS_BORDER,
            unfocusedIndicatorColor = TYPE_TCG_COLORLESS_BORDER,
            focusedTextColor = TYPE_TCG_COLORLESS_BORDER,
            unfocusedTextColor = TYPE_TCG_COLORLESS_BORDER,
            focusedPlaceholderColor = TYPE_TCG_COLORLESS_BORDER,
            unfocusedPlaceholderColor = TYPE_TCG_COLORLESS_BORDER,
            focusedTrailingIconColor = TYPE_TCG_COLORLESS_BORDER,
            unfocusedTrailingIconColor = TYPE_TCG_COLORLESS_BORDER,
            focusedLeadingIconColor = TYPE_TCG_COLORLESS_BORDER,
            unfocusedLeadingIconColor = TYPE_TCG_COLORLESS_BORDER
        ),
        modifier = modifier.fillMaxWidth(),
        value = searchTextState,
        onValueChange = { searchTextState = it },
        placeholder = {
            Text(
                modifier = Modifier,
                text = "Search"
            )
        },
        singleLine = true,
        maxLines = 1,
        leadingIcon = {
            Icon(
                painterResource(id = R.drawable.search),
                contentDescription = stringResource(R.string.description_search_icon)
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (searchTextState.isNotEmpty() || searchTextState.isNotBlank()) {
                        searchTextState = ""
                    }
                }
            ) {
                Icon(
                    painterResource(id = R.drawable.close),
                    contentDescription = stringResource(R.string.description_delete_icon)
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions =
        KeyboardActions(
            onSearch = {
                searchOnClick(searchTextState)
            }
        )
    )
}

/**
 * Represents the state of the search widget.
 */
enum class SearchWidgetState {
    OPENED,
    CLOSED
}
