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

import de.entikore.composedex.domain.model.preferences.AppThemeConfig

/**
 * Models state of the settings screen.
 */
data class SettingScreenUiState(
    val themeItems: List<RadioButtonItem> =
        listOf(
            RadioButtonItem(id = AppThemeConfig.MODE_DAY.ordinal, title = "Light Theme"),
            RadioButtonItem(id = AppThemeConfig.MODE_NIGHT.ordinal, title = "Dark Theme"),
            RadioButtonItem(id = AppThemeConfig.MODE_AUTO.ordinal, title = "Auto")
        ),
    val selected: Int = 0
)

/**
 * The data class representing a radio button item.
 */
data class RadioButtonItem(val id: Int, val title: String)
