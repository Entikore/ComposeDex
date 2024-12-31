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
package de.entikore.composedex.data.remote.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.entikore.composedex.data.remote.ComposeDexApi
import de.entikore.composedex.data.remote.RemoteDataSource
import de.entikore.composedex.domain.util.IDLE_CONNECTION_COUNT
import de.entikore.composedex.domain.util.KEEP_ALIVE_DURATION
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Singleton
    @Provides
    fun provideComposeDexApi(retrofit: Retrofit.Builder): ComposeDexApi =
        retrofit.baseUrl(ComposeDexApi.BASE_URL).build().create(ComposeDexApi::class.java)

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(moshi: Moshi): Retrofit.Builder =
        Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))

    @Singleton
    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder = OkHttpClient().newBuilder()
        .connectionPool(ConnectionPool(IDLE_CONNECTION_COUNT, KEEP_ALIVE_DURATION, TimeUnit.MINUTES))

    @Provides
    fun provideRemoteDataSource(composeDexApi: ComposeDexApi): RemoteDataSource =
        RemoteDataSource(composeDexApi)
}
