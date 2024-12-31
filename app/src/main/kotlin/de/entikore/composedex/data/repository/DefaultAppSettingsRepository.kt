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
package de.entikore.composedex.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import de.entikore.composedex.domain.model.preferences.AppThemeConfig
import de.entikore.composedex.domain.model.preferences.TypeThemeConfig
import de.entikore.composedex.domain.model.preferences.UserPreferences
import de.entikore.composedex.domain.repository.AppSettingsRepository
import de.entikore.composedex.domain.repository.AppSettingsRepository.Companion.APP_THEME
import de.entikore.composedex.domain.repository.AppSettingsRepository.Companion.TYPE_THEME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of [AppSettingsRepository].
 */
class DefaultAppSettingsRepository @Inject constructor(private val context: Context) :
    AppSettingsRepository {

    override suspend fun setTheme(value: AppThemeConfig) {
        Timber.d("Set theme to $value")
        context.themeDataStore.edit {
            it[APP_THEME] = value.ordinal
        }
    }

    override suspend fun setTypeTheme(value: TypeThemeConfig) {
        Timber.d("Set type theme to $value")
        context.themeDataStore.edit {
            it[TYPE_THEME] = value.ordinal
        }
    }

    override fun getUserPreferences(): Flow<UserPreferences> =
        context.themeDataStore.data.map {
            UserPreferences(mapToAppThemeConfig(it[APP_THEME]), mapToTypeThemeConfig(it[TYPE_THEME]))
        }

    private fun mapToAppThemeConfig(value: Int?): AppThemeConfig {
        val theme =
            AppThemeConfig.fromOrdinal(value ?: AppThemeConfig.MODE_AUTO.ordinal)
        Timber.d("Mapped value $value to $theme")
        return theme
    }

    private fun mapToTypeThemeConfig(value: Int?): TypeThemeConfig {
        val theme =
            TypeThemeConfig.fromOrdinal(value ?: TypeThemeConfig.COLORLESS.ordinal)
        Timber.d("Mapped value $value to $theme")
        return theme
    }
}

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_settings"
)
