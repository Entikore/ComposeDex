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
package de.entikore.composedex.domain.usecase.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import de.entikore.composedex.domain.model.preferences.AppThemeConfig
import de.entikore.composedex.domain.repository.AppSettingsRepository
import de.entikore.composedex.domain.repository.FavouriteRepository
import de.entikore.composedex.domain.repository.LocalStorage
import de.entikore.composedex.domain.repository.PokemonRepository
import de.entikore.composedex.domain.usecase.ChangeLightDarkThemeUseCase
import de.entikore.composedex.domain.usecase.ChangeTypeThemeUseCase
import de.entikore.composedex.domain.usecase.DeleteLocalDataUseCase
import de.entikore.composedex.domain.usecase.GetUserPreferencesUseCase
import de.entikore.composedex.domain.usecase.SaveImageData
import de.entikore.composedex.domain.usecase.SaveRemoteImageUseCase
import de.entikore.composedex.domain.usecase.SaveRemoteSoundUseCase
import de.entikore.composedex.domain.usecase.SaveSoundData
import de.entikore.composedex.domain.usecase.SetAsFavouriteUseCase
import de.entikore.composedex.domain.usecase.SetFavouriteData
import de.entikore.composedex.domain.usecase.base.BaseSuspendUseCase
import okhttp3.OkHttpClient

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideSaveRemoteImageUseCase(
        @ApplicationContext context: Context,
        repository: PokemonRepository,
        okHttpClientBuilder: OkHttpClient.Builder
    ): BaseSuspendUseCase<SaveImageData, String> =
        SaveRemoteImageUseCase(context, repository, okHttpClientBuilder)

    @Provides
    fun provideSaveRemoteSoundUseCase(
        @ApplicationContext context: Context,
        repository: PokemonRepository,
        okHttpClientBuilder: OkHttpClient.Builder
    ): BaseSuspendUseCase<SaveSoundData, String> =
        SaveRemoteSoundUseCase(context, repository, okHttpClientBuilder)

    @Provides
    fun provideSetAsFavouriteUseCase(repository: FavouriteRepository): BaseSuspendUseCase<SetFavouriteData, Unit> =
        SetAsFavouriteUseCase(repository)

    @Provides
    fun provideDeleteLocalDataUseCase(
        @ApplicationContext context: Context,
        composeDexDatabase: LocalStorage
    ): BaseSuspendUseCase<Unit, Unit> = DeleteLocalDataUseCase(context, composeDexDatabase)

    @Provides
    fun provideGetUserPreferencesUseCase(repository: AppSettingsRepository) =
        GetUserPreferencesUseCase(repository)

    @Provides
    fun provideChangeLightDarkThemeUseCase(
        repository: AppSettingsRepository
    ): BaseSuspendUseCase<AppThemeConfig, Unit> =
        ChangeLightDarkThemeUseCase(repository)

    @Provides
    fun provideChangeTypeThemeUseCase(repository: AppSettingsRepository): BaseSuspendUseCase<String, Unit> =
        ChangeTypeThemeUseCase(repository)
}
