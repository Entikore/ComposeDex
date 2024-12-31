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
package de.entikore.composedex.ui.screen.setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.entikore.composedex.R
import de.entikore.composedex.domain.model.preferences.AppThemeConfig
import de.entikore.composedex.ui.component.TopBar
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_BORDER
import de.entikore.composedex.ui.theme.TYPE_TCG_COLORLESS_PRIMARY

/**
 * The top level composable for the Settings screen.
 */
@Composable
fun SettingsScreen(
    openDrawer: () -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsState()
    val switchTheme = viewModel::switchTheme
    val deleteLocalData = viewModel::deleteCachedFiles

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        SettingsScreenTopBar(openDrawer)
        SettingsScreenContent(screenState, switchTheme, deleteLocalData, showSnackbar)
    }
}

@Composable
private fun SettingsScreenTopBar(openDrawer: () -> Unit) {
    TopBar(title = stringResource(R.string.settings_screen_top_bar), openDrawer = openDrawer)
}

@Composable
fun SettingsScreenContent(
    screenState: SettingScreenUiState,
    switchTheme: (theme: AppThemeConfig) -> Unit,
    deleteLocalData: () -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.settings_option_theme_settings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(2.dp))
            RadioGroup(
                items = screenState.themeItems,
                selected = screenState.selected,
                onItemSelect = { id -> switchTheme(AppThemeConfig.fromOrdinal(id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.settings_option_delete_local_data),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(2.dp))
            OutlinedButton(
                border = BorderStroke(2.dp, color = TYPE_TCG_COLORLESS_BORDER),
                shape = CutCornerShape(integerResource(R.integer.default_cut_corner_shape_percentage)),
                onClick = {
                    showSnackbar("Deleting local data")
                    deleteLocalData()
                },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = TYPE_TCG_COLORLESS_PRIMARY)
            ) {
                Text(text = "Delete")
            }
        }
    }
}

@Composable
private fun RadioGroup(
    items: Iterable<RadioButtonItem>,
    selected: Int,
    onItemSelect: ((Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
        modifier
            .selectableGroup()
            .border(
                width = dimensionResource(id = R.dimen.default_border),
                color = MaterialTheme.colorScheme.outline,
                shape = CutCornerShape(5)
            )
    ) {
        items.forEach { item ->
            CustomRadioButtonItem(
                item = item,
                selected = selected == item.id,
                onClick = { onItemSelect?.invoke(item.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(integerResource(R.integer.medium_cut_corner_shape_percentage)))
            )
        }
    }
}

@Composable
private fun CustomRadioButtonItem(
    item: RadioButtonItem,
    selected: Boolean,
    onClick: ((Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier =
        modifier
            .selectable(
                selected = selected,
                onClick = { onClick?.invoke(item.id) },
                role = Role.RadioButton
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ball_red),
            contentDescription = "Pokeball icon",
            modifier = Modifier
                .size(16.dp)
                .alpha(if (selected) 1f else 0.3f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
