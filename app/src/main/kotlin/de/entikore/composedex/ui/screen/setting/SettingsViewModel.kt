/*
 * Copyright 2025 Entikore
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.entikore.composedex.domain.model.preferences.AppThemeConfig
import de.entikore.composedex.domain.usecase.GetUserPreferencesUseCase
import de.entikore.composedex.domain.usecase.base.BaseSuspendUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages application state for the [SettingsScreen].
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    userPreferencesUseCase: GetUserPreferencesUseCase,
    private val changeLightDarkThemeUseCase: @JvmSuppressWildcards BaseSuspendUseCase<AppThemeConfig, Unit>,
    private val deleteLocalData: @JvmSuppressWildcards BaseSuspendUseCase<Unit, Unit>
) : ViewModel() {

    val screenState = userPreferencesUseCase.invoke().map {
        SettingScreenUiState(selected = it.appThemeConfig.ordinal)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        SettingScreenUiState()
    )

    fun deleteCachedFiles() {
        Timber.d("Delete all cached files")
        viewModelScope.launch { deleteLocalData() }
    }

    fun switchTheme(theme: AppThemeConfig) {
        viewModelScope.launch { changeLightDarkThemeUseCase(theme) }
    }
}
