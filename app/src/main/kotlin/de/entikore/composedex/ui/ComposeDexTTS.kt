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
package de.entikore.composedex.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Provides Text-to-Speech (TTS) functionality.
 *
 * This class initializes and manages a [TextToSpeech] instance to speak
 * the provided text. It uses coroutines for asynchronous operations and ensures that only one TTS
 * instance is active at a time.
 *
 * @param context The application context used to initialize the `TextToSpeech` engine.
 * @param ioDispatcher The coroutine dispatcher used for TTS operations.
 */
class ComposeDexTTS @Inject constructor(
    @ActivityContext context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                Timber.d("Successfully initializing TTS")
            } else {
                Timber.d("Error initializing TTS")
            }
        }
    }

    suspend fun startTTS(message: String) {
        withContext(ioDispatcher) {
            if (tts?.isSpeaking == true) {
                tts?.stop()
            }
            val speech: CharSequence = message
            tts?.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stopTTS() {
        tts?.stop()
    }
}
