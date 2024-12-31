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
package de.entikore.composedex.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import coil3.compose.AsyncImage
import de.entikore.composedex.R
import de.entikore.composedex.ui.component.cutCornerShapeBorder
import de.entikore.composedex.ui.navigation.destination.drawerScreens

@Composable
fun ComposeDexDrawer(
    currentlySelected: NavBackStackEntry?,
    onDestinationClick: (route: String) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        drawerShape = CutCornerShape(
            topEndPercent = integerResource(id = R.integer.small_cut_corner_shape_percentage),
            bottomEndPercent = integerResource(R.integer.small_cut_corner_shape_percentage)
        ),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = dimensionResource(id = R.dimen.standard_padding))
        ) {
            DrawerHead(modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding)))
            DrawerBody(
                onDestinationClick,
                currentlySelected,
                Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.large_padding))
            )
        }
    }
}

@Composable
fun DrawerHead(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.drawer_title),
            style = MaterialTheme.typography.headlineLarge
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.standard_padding))
        ) {
            AsyncImage(
                model = R.drawable.gengar,
                contentDescription = stringResource(R.string.gengar_idle_animation),
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.idle_animation_foreground))
            )
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_padding)))
            AsyncImage(
                model = R.drawable.nidorino,
                contentDescription = stringResource(R.string.nidorino_idle_animation),
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.idle_animation_background))
            )
        }
    }
}

@Composable
fun DrawerBody(
    onDestinationClick: (route: String) -> Unit,
    currentlySelected: NavBackStackEntry?,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        drawerScreens.forEach { entry ->

            DrawerEntry(
                icon = entry.icon,
                name = entry.uiName,
                selected = entry.route == (currentlySelected?.destination?.route ?: ""),
                onClick = { onDestinationClick(entry.route) },
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.small_padding))
            )
        }
    }
}

@Composable
fun DrawerEntry(
    icon: Int,
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = name,
                fontStyle = MaterialTheme.typography.labelMedium.fontStyle
            )
        },
        selected = selected,
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "",
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.nav_icon_size))
                    .padding(start = dimensionResource(id = R.dimen.large_padding))
            )
        },
        onClick = onClick,
        modifier = modifier.cutCornerShapeBorder(
            cutCornerPercentage = integerResource(id = R.integer.default_cut_corner_shape_percentage),
            borderWidth = dimensionResource(R.dimen.default_border),
            borderColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}
