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
package de.entikore.composedex.ui.screen.util

import timber.log.Timber

const val SUFFIX_ARTWORK = "-artwork.png"
const val SUFFIX_CRY = "-cry.ogg"
const val SUFFIX_SPRITE = "-sprite.png"

suspend fun retrieveAsset(
    id: Int,
    name: String,
    localAsset: String?,
    remoteAsset: String?,
    saveAssetUseCase: suspend (id: Int, url: String, fileName: String) -> String
): String {
    var uri = ""
    if (localAsset != null) {
        Timber.d("Using local asset $localAsset for $id")
        return localAsset
    } else {
        Timber.d("No local asset found for $name, try to fetch remote asset")
        remoteAsset?.let {
            uri = saveAssetUseCase.invoke(id, remoteAsset, name)
        }
    }
    return uri
}
