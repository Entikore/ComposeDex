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
package de.entikore.composedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import de.entikore.composedex.domain.model.preferences.AppThemeConfig

/**
 * Primary entry point for the application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var isDarkModeEnabled: MutableState<Boolean> = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setOnExitAnimationListener { splashScreenView ->
                splashScreenView.remove()
            }
        }

        setContent {
            val userPreferences = viewModel.themeState.collectAsState()

            isDarkModeEnabled.value = when (userPreferences.value.appThemeConfig) {
                AppThemeConfig.MODE_DAY -> false
                AppThemeConfig.MODE_NIGHT -> true
                else -> isSystemInDarkTheme()
            }
            ComposeDexApp(userPreferences.value.typeThemeConfig, isDarkModeEnabled.value)
        }
    }
}
