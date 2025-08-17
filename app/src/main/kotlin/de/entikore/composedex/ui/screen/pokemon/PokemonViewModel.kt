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
package de.entikore.composedex.ui.screen.pokemon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import de.entikore.composedex.domain.WorkResult
import de.entikore.composedex.domain.model.pokemon.ChainLink
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.pokemon.Variety
import de.entikore.composedex.domain.usecase.GetPokemonUseCase
import de.entikore.composedex.domain.usecase.SaveImageData
import de.entikore.composedex.domain.usecase.SaveSoundData
import de.entikore.composedex.domain.usecase.SetFavouriteData
import de.entikore.composedex.domain.usecase.base.ParamsSuspendUseCase
import de.entikore.composedex.ui.ComposeDexTTS
import de.entikore.composedex.ui.screen.util.SUFFIX_ARTWORK
import de.entikore.composedex.ui.screen.util.SUFFIX_CRY
import de.entikore.composedex.ui.screen.util.SUFFIX_SPRITE
import de.entikore.composedex.ui.screen.util.retrieveAsset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages application state for the [PokemonScreen].
 */
@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val getPokemonUseCase: GetPokemonUseCase,
    private val saveRemoteImageUseCase: @JvmSuppressWildcards ParamsSuspendUseCase<SaveImageData, String>,
    private val saveRemoteCryUseCase: @JvmSuppressWildcards ParamsSuspendUseCase<SaveSoundData, String>,
    private val setAsFavouriteUseCase: @JvmSuppressWildcards ParamsSuspendUseCase<SetFavouriteData, Unit>,
    private val changeThemeUseCase: @JvmSuppressWildcards ParamsSuspendUseCase<String, Unit>,
    private val exoPlayer: ExoPlayer,
    private val tts: ComposeDexTTS
) : ViewModel() {

    private val _selectedPokemonFlow = MutableStateFlow<String?>(null)
    private val _selectedPokemon = MutableStateFlow(0)
    private val _selectedType = MutableStateFlow("")
    private val _displayedEvolutionType = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState: StateFlow<PokemonScreenState> =
        _selectedPokemonFlow
            .flatMapLatest { pokemonParameter ->
                pokemonParameter?.let {
                    getPokemonUseCase(pokemonParameter).map {
                        when (it) {
                            is WorkResult.Error -> {
                                PokemonScreenState.Error(
                                    errorMessage = "$ERROR_LOADING_POKEMON ${_selectedPokemonFlow.value}"
                                )
                            }

                            WorkResult.Loading -> PokemonScreenState.Loading
                            is WorkResult.Success -> {
                                val pokemon = it.data

                                viewModelScope.launch {
                                    retrieveAsset(
                                        pokemon.id,
                                        buildString {
                                            append(pokemon.name)
                                            append(SUFFIX_ARTWORK)
                                        },
                                        pokemon.artwork,
                                        pokemon.remoteArtwork,
                                        saveAssetUseCase = { id, url, fileName ->
                                            saveRemoteImageUseCase(SaveImageData(id, url, fileName, false))
                                        }
                                    )
                                }
                                viewModelScope.launch {
                                    retrieveAsset(
                                        pokemon.id,
                                        buildString {
                                            append(pokemon.name)
                                            append(SUFFIX_CRY)
                                        },
                                        pokemon.cry,
                                        pokemon.remoteCry,
                                        saveAssetUseCase = { id, url, fileName ->
                                            saveRemoteCryUseCase.invoke(
                                                SaveSoundData(
                                                    id,
                                                    url,
                                                    fileName
                                                )
                                            )
                                        }
                                    )
                                }
                                _selectedPokemon.value =
                                    pokemon.varieties.indexOfFirst { variety -> variety.varietyName == pokemon.name }
                                        .takeIf { index -> index != -1 } ?: 0
                                PokemonScreenState.Success(
                                    selectedPokemon = pokemon,
                                    varieties = listOf(pokemon)
                                )
                            }
                        }
                    }
                } ?: flowOf(PokemonScreenState.NoPokemonSelected)
            }.flatMapLatest { state ->
                when (state) {
                    is PokemonScreenState.Success -> {
                        combine(
                            flowOf(state),
                            lookUpEvolvesFromFlow(state.selectedPokemon.evolvesFrom)
                        ) { oldState, evolvesFrom ->
                            oldState.copy(evolvesFrom = evolvesFrom)
                        }
                    }

                    else -> flowOf(state)
                }
            }.flatMapLatest { state ->
                when (state) {
                    is PokemonScreenState.Success -> {
                        combine(
                            flowOf(state),
                            combine(
                                lookUpEvolvesToFlow(
                                    state.selectedPokemon.defaultName,
                                    state.selectedPokemon.evolutionChain
                                ).toList(),
                                ::combineEvolvesTo
                            )
                        ) { oldState, evolvesTo ->
                            oldState.copy(evolvesTo = evolvesTo)
                        }
                    }

                    else -> flowOf(state)
                }
            }.flatMapLatest { state ->
                when (state) {
                    is PokemonScreenState.Success -> {
                        combine(
                            flowOf(state),
                            combine(
                                lookUpVarietiesToFlow(
                                    state.selectedPokemon.varieties
                                        .filter { it.varietyName != state.selectedPokemon.name }
                                ).toList(),
                                ::combineVarieties
                            )
                        ) { oldState, varieties ->
                            oldState.copy(
                                varieties = oldState.varieties.plus(varieties).sortedBy { it.id }
                            )
                        }
                    }

                    else -> flowOf(state)
                }
            }.flatMapLatest { state ->
                when (state) {
                    is PokemonScreenState.Success -> {
                        combine(
                            flowOf(state),
                            _selectedPokemon
                        ) { pokemonState, index ->
                            val selectedVariety = pokemonState.varieties.elementAtOrNull(index)
                                ?: pokemonState.varieties.first()
                            val selectedType =
                                selectedVariety.types.firstOrNull { it == pokemonState.selectedType }
                                    ?: selectedVariety.types.first()
                            switchTheme(selectedType.name)
                            _selectedType.value = selectedType.name
                            pokemonState.copy(
                                selectedPokemon = selectedVariety,
                                selectedType = selectedType
                            )
                        }
                    }

                    else -> flowOf(state)
                }
            }.flatMapLatest { state ->
                when (state) {
                    is PokemonScreenState.Success -> {
                        combine(
                            flowOf(state),
                            _selectedType
                        ) { pokemonState, type ->
                            val selectedType =
                                pokemonState.selectedPokemon.types.firstOrNull { it.name == type }
                                    ?: pokemonState.selectedPokemon.types.first()
                            switchTheme(selectedType.name)
                            pokemonState.copy(selectedType = selectedType)
                        }
                    }

                    else -> flowOf(state)
                }
            }.flatMapLatest { state ->
                when (state) {
                    is PokemonScreenState.Success -> {
                        combine(
                            flowOf(state),
                            _displayedEvolutionType
                        ) { pokemonState, type ->
                            pokemonState.copy(displayedEvolution = type)
                        }
                    }

                    else -> flowOf(state)
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, PokemonScreenState.NoPokemonSelected)

    private fun combineEvolvesTo(vararg previews: PokemonPreview?): List<PokemonPreview> {
        return previews.filterNotNull().toList()
    }

    private fun combineVarieties(vararg varieties: Pokemon?): List<Pokemon> {
        return varieties.filterNotNull().toList()
    }

    init {
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
        tts.stopTTS()
    }

    fun lookUpPokemon(name: String) {
        Timber.d("Search for pokemon $name")
        _selectedPokemonFlow.value = name
    }

    fun updateFavourite(id: Int, isFavourite: Boolean) {
        Timber.d("Update favourite status for $id to $isFavourite")
        viewModelScope.launch { setAsFavouriteUseCase(SetFavouriteData(id, isFavourite)) }
    }

    fun selectVariety(pokemonIndex: Int) {
        Timber.d("Select variety with index $pokemonIndex")
        _selectedPokemon.value = pokemonIndex
    }

    fun selectType(typeName: String) {
        Timber.d("Select type $typeName")
        _selectedType.value = typeName
    }

    fun changeDisplayedEvolutionText(text: String) {
        Timber.d("Change displayed evolution text to $text")
        _displayedEvolutionType.value = text
    }

    fun playSound(soundUri: String?) {
        Timber.d("Play sound $soundUri")
        soundUri?.let {
            viewModelScope.launch {
                if (exoPlayer.isPlaying) {
                    exoPlayer.stop()
                }
                exoPlayer.clearMediaItems()
                exoPlayer.addMediaItem(MediaItem.fromUri(soundUri))
                exoPlayer.prepare()
                exoPlayer.play()
            }
        }
    }

    fun speakTextEntry(textEntry: String) {
        Timber.d("Speak text entry $textEntry")
        viewModelScope.launch {
            tts.startTTS(textEntry)
        }
    }

    private fun switchTheme(typeName: String) {
        viewModelScope.launch { changeThemeUseCase(typeName) }
    }

    private fun lookUpEvolvesFromFlow(name: String): Flow<PokemonPreview?> {
        return if (name.isEmpty()) {
            flowOf(null)
        } else {
            getPokemonUseCase(name).map {
                val evolutionText =
                    "Evolves from ${name.replaceFirstChar { char -> char.uppercaseChar() }}"
                when (it) {
                    is WorkResult.Error -> null
                    WorkResult.Loading -> PokemonPreview(
                        name = name,
                        isLoading = true,
                        evolutionText = evolutionText
                    )

                    is WorkResult.Success -> {
                        it.data.also { pokemon ->
                            retrieveAsset(
                                pokemon.id,
                                buildString {
                                    append(pokemon.name)
                                    append(SUFFIX_SPRITE)
                                },
                                pokemon.sprite,
                                pokemon.remoteSprite,
                                saveAssetUseCase = { id, url, fileName ->
                                    saveRemoteImageUseCase(SaveImageData(id, url, fileName, true))
                                }
                            )
                        }
                        if (it.data.sprite == null) {
                            PokemonPreview(
                                name = name,
                                url = it.data.remoteSprite,
                                types = it.data.types,
                                evolutionText = evolutionText,
                                isLoading = true
                            )
                        } else {
                            PokemonPreview(
                                name = name,
                                url = it.data.remoteSprite,
                                types = it.data.types,
                                sprite = it.data.sprite,
                                evolutionText = evolutionText,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun lookUpEvolvesToFlow(
        pokemonName: String,
        evolutionChain: Map<Int, List<ChainLink>>
    ): Array<Flow<PokemonPreview?>> {
        val evolvesToList = evolutionChain[
            evolutionChain.entries.firstOrNull { (_, chainLinks) ->
                chainLinks.any { it.name == pokemonName }
            }?.key?.plus(1)
        ] ?: return Array(1) { flowOf(null) }
        return Array(size = evolvesToList.size) {
            val evolutionText =
                "Evolves to ${evolvesToList[it].name.replaceFirstChar { char -> char.uppercaseChar() }}"
            getPokemonUseCase(evolvesToList[it].url).map { result ->
                when (result) {
                    is WorkResult.Error -> {
                        Timber.d("Error loading Pokemon ${evolvesToList[it].name}")
                        null
                    }
                    WorkResult.Loading -> PokemonPreview(
                        name = evolvesToList[it].name,
                        evolutionText = evolutionText,
                        isLoading = true
                    )

                    is WorkResult.Success -> {
                        result.data.also { pokemon ->
                            retrieveAsset(
                                pokemon.id,
                                buildString {
                                    append(pokemon.name)
                                    append(SUFFIX_SPRITE)
                                },
                                pokemon.sprite,
                                pokemon.remoteSprite,
                                saveAssetUseCase = { id, url, fileName ->
                                    saveRemoteImageUseCase(SaveImageData(id, url, fileName, true))
                                }
                            )
                        }

                        if (result.data.sprite == null) {
                            PokemonPreview(
                                name = result.data.name,
                                url = result.data.remoteSprite,
                                types = result.data.types,
                                evolutionText = evolutionText,
                                isLoading = true
                            )
                        } else {
                            PokemonPreview(
                                name = result.data.name,
                                types = result.data.types,
                                sprite = result.data.sprite,
                                evolutionText = evolutionText,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun lookUpVarietiesToFlow(varieties: List<Variety>): Array<Flow<Pokemon?>> {
        if (varieties.isEmpty()) {
            return Array(1) {
                flowOf(null)
            }
        }
        return Array(size = varieties.size) {
            getPokemonUseCase(varieties[it].varietyName).map { result ->
                when (result) {
                    is WorkResult.Success -> {
                        result.data.also { pokemon ->
                            viewModelScope.launch {
                                retrieveAsset(
                                    pokemon.id,
                                    buildString {
                                        append(pokemon.name)
                                        append(SUFFIX_ARTWORK)
                                    },
                                    pokemon.artwork,
                                    pokemon.remoteArtwork,
                                    saveAssetUseCase = { id, url, fileName ->
                                        saveRemoteImageUseCase(SaveImageData(id, url, fileName, false))
                                    }
                                )
                            }
                            viewModelScope.launch {
                                retrieveAsset(
                                    pokemon.id,
                                    buildString {
                                        append(pokemon.name)
                                        append(SUFFIX_CRY)
                                    },
                                    pokemon.cry,
                                    pokemon.remoteCry,
                                    saveAssetUseCase = { id, url, fileName ->
                                        saveRemoteCryUseCase.invoke(
                                            SaveSoundData(
                                                id,
                                                url,
                                                fileName
                                            )
                                        )
                                    }
                                )
                            }
                        }
                        if (result.data.artwork != null) {
                            result.data
                        } else {
                            Timber.d("Artwork for variety ${varieties[it].varietyName} is null")
                            null
                        }
                    }
                    else -> {
                        Timber.d("Fetching variety ${varieties[it].varietyName} was not successful")
                        null
                    }
                }
            }
        }
    }

    companion object {
        const val ERROR_LOADING_POKEMON = "Error loading Pokemon"
    }
}
