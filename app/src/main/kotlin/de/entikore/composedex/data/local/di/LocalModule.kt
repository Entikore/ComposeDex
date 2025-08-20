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
package de.entikore.composedex.data.local.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.entikore.composedex.data.local.ComposeDexDatabase
import de.entikore.composedex.data.local.converter.ChainConverter
import de.entikore.composedex.data.local.converter.StatsConverter
import de.entikore.composedex.data.local.converter.TypesConverter
import de.entikore.composedex.data.local.dao.GenerationDao
import de.entikore.composedex.data.local.dao.PokemonDao
import de.entikore.composedex.data.local.dao.SpeciesDao
import de.entikore.composedex.data.local.dao.TypeDao
import de.entikore.composedex.data.local.dao.VarietyDao
import de.entikore.composedex.data.local.datasource.FavouriteLocalDataSource
import de.entikore.composedex.data.local.datasource.GenerationLocalDataSource
import de.entikore.composedex.data.local.datasource.PokemonLocalDataSource
import de.entikore.composedex.data.local.datasource.TypeLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    fun provideChainConverter(moshi: Moshi): ChainConverter = ChainConverter(moshi)

    @Provides
    fun provideStatsConverter(moshi: Moshi): StatsConverter = StatsConverter(moshi)

    @Provides
    fun provideTypesConverter(moshi: Moshi): TypesConverter = TypesConverter(moshi)

    @Singleton
    @Provides
    fun provideComposeDexDatabase(
        @ApplicationContext context: Context,
        chainConverter: ChainConverter,
        statsConverter: StatsConverter,
        typesConverter: TypesConverter

    ): ComposeDexDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            ComposeDexDatabase::class.java,
            ComposeDexDatabase.DATABASE_NAME
        )
            .addTypeConverter(chainConverter)
            .addTypeConverter(statsConverter)
            .addTypeConverter(typesConverter)
            .build()

    @Provides
    fun providePokemonDao(composeDexDatabase: ComposeDexDatabase): PokemonDao =
        composeDexDatabase.pokemonDao()

    @Provides
    fun provideTypeDao(composeDexDatabase: ComposeDexDatabase): TypeDao =
        composeDexDatabase.typeDao()

    @Provides
    fun provideSpeciesDao(composeDexDatabase: ComposeDexDatabase): SpeciesDao =
        composeDexDatabase.speciesDao()

    @Provides
    fun provideGenerationDao(composeDexDatabase: ComposeDexDatabase): GenerationDao =
        composeDexDatabase.generationDao()

    @Provides
    fun provideVarietyDao(composeDexDatabase: ComposeDexDatabase): VarietyDao =
        composeDexDatabase.varietyDao()

    @Provides
    fun providePokemonLocalDataSource(
        database: ComposeDexDatabase
    ): PokemonLocalDataSource =
        PokemonLocalDataSource(database)

    @Provides
    fun provideFavouriteLocalDataSource(pokemonDao: PokemonDao): FavouriteLocalDataSource =
        FavouriteLocalDataSource(
            pokemonDao
        )

    @Provides
    fun provideTypeLocalDataSource(
        database: ComposeDexDatabase
    ): TypeLocalDataSource = TypeLocalDataSource(database)

    @Provides
    fun provideGenerationLocalDataSource(
        database: ComposeDexDatabase
    ): GenerationLocalDataSource =
        GenerationLocalDataSource(database)
}
