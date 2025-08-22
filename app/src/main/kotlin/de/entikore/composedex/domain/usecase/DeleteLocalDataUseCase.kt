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
package de.entikore.composedex.domain.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.entikore.composedex.domain.repository.LocalStorage
import de.entikore.composedex.domain.usecase.base.BaseSuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * This use case deletes all stored data from the local storage.
 */
class DeleteLocalDataUseCase(
    @ApplicationContext private val context: Context,
    private val localStorage: LocalStorage,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseSuspendUseCase<Unit, Unit>(dispatcher) {

    override suspend fun execute(params: Unit) {
        val files = context.fileList()
        for (file in files) {
            context.deleteFile(file)
        }
        localStorage.clearData()
    }
}
