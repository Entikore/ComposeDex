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
package de.entikore.composedex.data.repository.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.entikore.composedex.data.local.ComposeDexDatabase
import de.entikore.composedex.data.local.datasource.FavouriteLocalDataSource
import de.entikore.composedex.data.local.datasource.GenerationLocalDataSource
import de.entikore.composedex.data.local.datasource.PokemonLocalDataSource
import de.entikore.composedex.data.local.datasource.TypeLocalDataSource
import de.entikore.composedex.data.remote.RemoteDataSource
import de.entikore.composedex.data.repository.DefaultAppSettingsRepository
import de.entikore.composedex.data.repository.DefaultFavouriteRepository
import de.entikore.composedex.data.repository.OfflineFirstGenerationRepository
import de.entikore.composedex.data.repository.OfflineFirstPokemonRepository
import de.entikore.composedex.data.repository.OfflineFirstTypeRepository
import de.entikore.composedex.domain.repository.AppSettingsRepository
import de.entikore.composedex.domain.repository.FavouriteRepository
import de.entikore.composedex.domain.repository.GenerationRepository
import de.entikore.composedex.domain.repository.LocalStorage
import de.entikore.composedex.domain.repository.PokemonRepository
import de.entikore.composedex.domain.repository.TypeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun providePokemonRepository(
        localDataSource: PokemonLocalDataSource,
        remoteDataSource: RemoteDataSource
    ): PokemonRepository = OfflineFirstPokemonRepository(localDataSource, remoteDataSource)

    @Singleton
    @Provides
    fun provideGenerationRepository(
        localDataSource: GenerationLocalDataSource,
        remoteDataSource: RemoteDataSource
    ): GenerationRepository = OfflineFirstGenerationRepository(localDataSource, remoteDataSource)

    @Singleton
    @Provides
    fun provideTypeRepository(
        localDataSource: TypeLocalDataSource,
        remoteDataSource: RemoteDataSource
    ): TypeRepository = OfflineFirstTypeRepository(localDataSource, remoteDataSource)

    @Singleton
    @Provides
    fun provideFavouriteRepository(
        localDataSource: FavouriteLocalDataSource
    ): FavouriteRepository = DefaultFavouriteRepository(localDataSource)

    @Singleton
    @Provides
    fun provideLocalStorage(composeDexDatabase: ComposeDexDatabase): LocalStorage =
        composeDexDatabase

    @Singleton
    @Provides
    fun provideAppSettingsRepository(
        @ApplicationContext context: Context
    ): AppSettingsRepository = DefaultAppSettingsRepository(context)
}
