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
package de.entikore.composedex.domain.usecase

import de.entikore.composedex.domain.model.preferences.AppThemeConfig
import de.entikore.composedex.domain.repository.AppSettingsRepository
import de.entikore.composedex.domain.usecase.base.ParamsSuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * This use case changes the app theme between light and dark mode.
 */
class ChangeLightDarkThemeUseCase @Inject constructor(
    private val repository: AppSettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ParamsSuspendUseCase<AppThemeConfig, Unit>() {
    override suspend operator fun invoke(params: AppThemeConfig) {
        withContext(ioDispatcher) {
            repository.setTheme(params)
        }
    }
}
