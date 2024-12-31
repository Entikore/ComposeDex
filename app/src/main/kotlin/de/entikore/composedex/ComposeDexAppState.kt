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

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Models state for the [MainActivity].
 */
data class ComposeDexAppState(
    val navController: NavHostController,
    val drawerState: DrawerState,
    private val scope: CoroutineScope,
    val snackbarHostState: SnackbarHostState
) {

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    fun changeDrawerState() {
        scope.launch {
            if (drawerState.isOpen) drawerState.close() else drawerState.open()
        }
    }
}

@Composable
fun rememberComposeDexAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
) = remember(snackbarHostState, navController, coroutineScope, drawerState) {
    ComposeDexAppState(
        snackbarHostState = snackbarHostState,
        navController = navController,
        scope = coroutineScope,
        drawerState = drawerState
    )
}
