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

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import okio.Path.Companion.toPath
import timber.log.Timber
import timber.log.Timber.Forest.plant

/**
 * Custom application class.
 */
@HiltAndroidApp
class ComposeDex :
    Application(),
    SingletonImageLoader.Factory {

    @Override
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, RAM_PERCENTAGE)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve(CACHE_NAME).absolutePath.toPath())
                .maxSizeBytes(MB_AMOUNT * BINARY_PREFIX * BINARY_PREFIX)
                .build()
        }
        .build()

    companion object {
        const val BINARY_PREFIX = 1024L
        const val CACHE_NAME = "image_cache"
        const val MB_AMOUNT = 50L

        const val RAM_PERCENTAGE = 0.25
    }
}
