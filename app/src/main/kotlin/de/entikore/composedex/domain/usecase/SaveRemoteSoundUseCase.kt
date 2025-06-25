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
package de.entikore.composedex.domain.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.entikore.composedex.domain.repository.PokemonRepository
import de.entikore.composedex.domain.usecase.base.ParamsSuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * This use case downloads an sound file and save it to the local storage.
 */
class SaveRemoteSoundUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pokemonRepository: PokemonRepository,
    httpClientBuilder: OkHttpClient.Builder,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ParamsSuspendUseCase<SaveSoundData, String>() {

    private var client: OkHttpClient = httpClientBuilder.build()

    override suspend operator fun invoke(
        params: SaveSoundData
    ): String =
        withContext(ioDispatcher) {
            return@withContext downloadAndSaveSound(params.soundAddress, params.fileName, params.id)
                ?: params.soundAddress
        }

    private suspend fun downloadAndSaveSound(soundAddress: String, dataName: String, id: Int): String? {
        val request = Request.Builder().url(soundAddress).build()
        return withContext(ioDispatcher) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Timber.e("Unsuccessful response: ${response.code} for $soundAddress")
                        return@withContext null
                    }
                    response.body?.byteStream()?.use { inputStream ->
                        context.openFileOutput(dataName, Context.MODE_PRIVATE).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    val fileLocation = "${context.filesDir}/$dataName"
                    pokemonRepository.updatePokemonCry(id, fileLocation)
                    fileLocation
                }
            } catch (e: SocketTimeoutException) {
                Timber.e("Timeout downloading sound from $soundAddress. $e")
                null
            } catch (e: ConnectException) {
                Timber.e("Connection error downloading sound from $soundAddress. $e")
                null
            } catch (e: UnknownHostException) {
                Timber.e("Unknown host error downloading sound from $soundAddress. $e")
                null
            } catch (e: HttpException) {
                Timber.e("HTTP error downloading sound from $soundAddress. $e")
                null
            } catch (e: IOException) {
                Timber.e("IO error downloading sound from $soundAddress. $e")
                null
            }
        }
    }
}

/**
 * Data class representing information needed to save a sound file.
 *
 * This class encapsulates the necessary data for saving a sound file, including
 * the Pokémon ID, the sound file URL and the desired file name.
 *
 * @property id The ID of the Pokémon associated with the sound file.
 * @property soundAddress The URL of the sound file to be downloaded and saved.
 * @property fileName The desired file name for the saved sound file.
 */
data class SaveSoundData(
    val id: Int,
    val soundAddress: String,
    val fileName: String
)
