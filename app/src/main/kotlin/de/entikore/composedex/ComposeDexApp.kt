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
package de.entikore.composedex

import androidx.compose.runtime.Composable
import de.entikore.composedex.domain.model.preferences.TypeThemeConfig
import de.entikore.composedex.ui.navigation.DrawerNavHost
import de.entikore.composedex.ui.theme.ComposeDexTheme

@Composable
fun ComposeDexApp(typeTheme: TypeThemeConfig, isInDarkTheme: Boolean) {
    val appState: ComposeDexAppState = rememberComposeDexAppState()

    ComposeDexTheme(appTheme = typeTheme, isDarkMode = isInDarkTheme) {
        DrawerNavHost(
            drawerState = appState.drawerState,
            snackBarHostState = appState.snackbarHostState,
            changeDrawerState = appState::changeDrawerState,
            showSnackbar = appState::showSnackbar
        )
    }
}
