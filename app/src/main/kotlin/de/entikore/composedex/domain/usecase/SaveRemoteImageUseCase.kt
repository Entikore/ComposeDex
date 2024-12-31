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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
 * This use case downloads an image and save it to the local storage.
 */
class SaveRemoteImageUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pokemonRepository: PokemonRepository,
    httpClientBuilder: OkHttpClient.Builder,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ParamsSuspendUseCase<SaveImageData, String>() {

    private var client: OkHttpClient = httpClientBuilder.build()

    override suspend operator fun invoke(
        params: SaveImageData
    ): String =
        withContext(ioDispatcher) {
            val bitmap = downloadImage(params.imageAddress)
            return@withContext if (bitmap == null) {
                params.imageAddress
            } else {
                saveImage(params.id, params.fileName, bitmap, params.isSprite)
            }
        }

    private suspend fun downloadImage(imageAddress: String): Bitmap? {
        if (imageAddress.isEmpty()) return null
        val request: Request = Request.Builder().url(imageAddress).build()

        return withContext(ioDispatcher) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Timber.e("Unsuccessful response: ${response.code()} for $imageAddress")
                        return@withContext null
                    }
                    response.body()?.byteStream()?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }
            } catch (e: SocketTimeoutException) {
                Timber.e("Timeout downloading image from $imageAddress. $e")
                null
            } catch (e: ConnectException) {
                Timber.e("Connection error downloading image from $imageAddress. $e")
                null
            } catch (e: UnknownHostException) {
                Timber.e("Unknown host error downloading image from $imageAddress. $e")
                null
            } catch (e: HttpException) {
                Timber.e("HTTP error downloading image from $imageAddress. $e")
                null
            } catch (e: IllegalArgumentException) {
                Timber.e("Invalid image data or format for $imageAddress. $e")
                null
            } catch (e: OutOfMemoryError) {
                Timber.e("Out of memory error decoding image from $imageAddress. $e")
                null
            } catch (e: IOException) {
                Timber.e("IO error downloading image from $imageAddress. $e")
                null
            }
        }
    }

    private suspend fun saveImage(id: Int, fileName: String, bitmap: Bitmap?, isSprite: Boolean) =
        withContext(ioDispatcher) {
            val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            bitmap?.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY_PERCENT, fos)
            withContext(Dispatchers.IO) { fos.close() }
            val fileLocation = "${context.filesDir}/$fileName"
            if (isSprite) {
                pokemonRepository.updatePokemonSprite(id, fileLocation)
            } else {
                pokemonRepository.updatePokemonArtwork(id, fileLocation)
            }
            return@withContext fileLocation
        }

    companion object {
        private const val COMPRESSION_QUALITY_PERCENT = 100
    }
}

/**
 * Data class representing information needed to save an image.
 *
 * This class encapsulates the necessary data for saving an image, including
 * the Pokémon ID, the image URL, the desired file name, and whether it's
 * a sprite or artwork image.
 *
 * @property id The ID of the Pokémon associated with the image.
 * @property imageAddress The URL of the image to be downloaded and saved.
 * @property fileName The desired file name for the saved image.
 * @property isSprite A flag indicating whether the image is a sprite (true) or artwork (false). Defaults to false.
 */
data class SaveImageData(
    val id: Int,
    val imageAddress: String,
    val fileName: String,
    val isSprite: Boolean = false
)
