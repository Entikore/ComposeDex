/*
 * Copyright 2026 Entikore
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
import de.entikore.composedex.domain.model.pokemon.ChainLink
import de.entikore.composedex.domain.model.pokemon.Pokemon
import de.entikore.composedex.domain.model.pokemon.Variety
import de.entikore.composedex.domain.usecase.FetchPokemonUseCase
import de.entikore.composedex.domain.usecase.SaveImageData
import de.entikore.composedex.domain.usecase.SaveSoundData
import de.entikore.composedex.domain.usecase.SetFavouriteData
import de.entikore.composedex.domain.usecase.base.BaseSuspendUseCase
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages application state for the [PokemonScreen].
 */
@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val getPokemonUseCase: FetchPokemonUseCase,
    private val saveRemoteImageUseCase: @JvmSuppressWildcards BaseSuspendUseCase<SaveImageData, String>,
    private val saveRemoteCryUseCase: @JvmSuppressWildcards BaseSuspendUseCase<SaveSoundData, String>,
    private val setAsFavouriteUseCase: @JvmSuppressWildcards BaseSuspendUseCase<SetFavouriteData, Unit>,
    private val changeThemeUseCase: @JvmSuppressWildcards BaseSuspendUseCase<String, Unit>,
    private val exoPlayer: ExoPlayer,
    private val tts: ComposeDexTTS
) : ViewModel() {

    private val _selectedPokemonFlow = MutableStateFlow<String?>(null)
    private val _selectedPokemon = MutableStateFlow(0)
    private val _selectedType = MutableStateFlow("")
    private val _displayedEvolutionType = MutableStateFlow("")
    private var lastThemeType: String? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState: StateFlow<PokemonScreenState> =
        _selectedPokemonFlow
            .flatMapLatest { pokemonParameter ->
                if (pokemonParameter == null) {
                    flowOf(PokemonScreenState.NoPokemonSelected)
                } else {
                    getPokemonUseCase(pokemonParameter).flatMapLatest {
                        handlePokemonResult(it, pokemonParameter)
                    }
                }
            }
            .onEach {
                if (it is PokemonScreenState.Success) {
                    switchTheme(it.selectedType.name)
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, PokemonScreenState.NoPokemonSelected)

    private fun handlePokemonResult(result: Result<Pokemon>, name: String): Flow<PokemonScreenState> {
        return when {
            result.isFailure -> flowOf(
                PokemonScreenState.Error(
                    errorMessage = "$ERROR_LOADING_POKEMON $name"
                )
            )

            result.isSuccess -> {
                val pokemon = result.getOrThrow()

                launchAssetRetrieval(pokemon)
                updateInitialSelection(pokemon)

                val evolvesFromFlow = lookUpEvolvesFromFlow(pokemon.evolvesFrom)
                val evolvesToFlow = combine(
                    lookUpEvolvesToFlow(
                        pokemon.defaultName,
                        pokemon.evolutionChain
                    ).toList(),
                    ::combineEvolvesTo
                )
                val extraVarietiesFlow = combine(
                    lookUpVarietiesToFlow(
                        pokemon.varieties.filter { it.varietyName != pokemon.name }
                    ).toList(),
                    ::combineVarieties
                )

                val detailsFlow = combine(
                    evolvesFromFlow,
                    evolvesToFlow,
                    extraVarietiesFlow
                ) { evolvesFrom, evolvesTo, extraVarieties ->
                    EnrichedPokemonData(evolvesFrom, evolvesTo, extraVarieties)
                }

                combine(
                    detailsFlow,
                    _selectedPokemon,
                    _selectedType,
                    _displayedEvolutionType
                ) { details, varietyIndex, typeName, evolutionText ->
                    val (evolvesFrom, evolvesTo, extraVarieties) = details
                    val allVarieties = (listOf(pokemon) + extraVarieties).sortedBy { it.id }
                    val selectedVariety = allVarieties.elementAtOrNull(varietyIndex)
                        ?: allVarieties.first()
                    val selectedType = selectedVariety.types.firstOrNull { it.name == typeName }
                        ?: selectedVariety.types.first()

                    PokemonScreenState.Success(
                        selectedPokemon = selectedVariety,
                        varieties = allVarieties,
                        evolvesFrom = evolvesFrom,
                        evolvesTo = evolvesTo,
                        selectedType = selectedType,
                        displayedEvolution = evolutionText
                    )
                }
            }

            else -> flowOf(PokemonScreenState.Loading)
        }
    }

    private data class EnrichedPokemonData(
        val evolvesFrom: PokemonPreview?,
        val evolvesTo: List<PokemonPreview>,
        val extraVarieties: List<Pokemon>
    )

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
        if (lastThemeType != typeName) {
            lastThemeType = typeName
            viewModelScope.launch { changeThemeUseCase(typeName) }
        }
    }

    private fun updateInitialSelection(pokemon: Pokemon) {
        val index = pokemon.varieties.indexOfFirst { it.varietyName == pokemon.name }
            .takeIf { it != -1 } ?: 0
        if (_selectedPokemon.value != index) {
            _selectedPokemon.value = index
        }
    }

    private fun launchAssetRetrieval(pokemon: Pokemon, isSprite: Boolean = false) {
        viewModelScope.launch {
            if (isSprite) {
                retrieveAsset(
                    pokemon.id,
                    "${pokemon.name}$SUFFIX_SPRITE",
                    pokemon.sprite,
                    pokemon.remoteSprite,
                    saveAssetUseCase = { id, url, fileName ->
                        saveRemoteImageUseCase(SaveImageData(id, url, fileName, true))
                    }
                )
            } else {
                retrieveAsset(
                    pokemon.id,
                    "${pokemon.name}$SUFFIX_ARTWORK",
                    pokemon.artwork,
                    pokemon.remoteArtwork,
                    saveAssetUseCase = { id, url, fileName ->
                        saveRemoteImageUseCase(SaveImageData(id, url, fileName, false))
                    }
                )
                retrieveAsset(
                    pokemon.id,
                    "${pokemon.name}$SUFFIX_CRY",
                    pokemon.cry,
                    pokemon.remoteCry,
                    saveAssetUseCase = { id, url, fileName ->
                        saveRemoteCryUseCase(SaveSoundData(id, url, fileName))
                    }
                )
            }
        }
    }

    private fun lookUpEvolvesFromFlow(name: String): Flow<PokemonPreview?> {
        return if (name.isEmpty()) {
            flowOf(null)
        } else {
            getPokemonUseCase(name).map { result ->
                val evolutionText =
                    "Evolves from ${name.replaceFirstChar { char -> char.uppercaseChar() }}"
                result.fold(
                    onSuccess = { pokemon ->
                        launchAssetRetrieval(pokemon, isSprite = true)
                        PokemonPreview(
                            name = name,
                            url = pokemon.remoteSprite,
                            types = pokemon.types,
                            sprite = pokemon.sprite ?: "",
                            evolutionText = evolutionText,
                            isLoading = pokemon.sprite == null
                        )
                    },
                    onFailure = { throwable ->
                        Timber.e(throwable, "Error loading Pokemon $name")
                        null
                    }
                ) ?: PokemonPreview(
                    name = name,
                    isLoading = true,
                    evolutionText = evolutionText
                )
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
            val name = evolvesToList[it].name
            val evolutionText =
                "Evolves to ${name.replaceFirstChar { char -> char.uppercaseChar() }}"
            getPokemonUseCase(evolvesToList[it].url).map { result ->
                result.fold(
                    onSuccess = { pokemon ->
                        launchAssetRetrieval(pokemon, isSprite = true)
                        PokemonPreview(
                            name = pokemon.name,
                            types = pokemon.types,
                            sprite = pokemon.sprite ?: "",
                            url = pokemon.remoteSprite,
                            evolutionText = evolutionText,
                            isLoading = pokemon.sprite == null
                        )
                    },
                    onFailure = { throwable ->
                        Timber.e(throwable, "Error loading Pokemon $name")
                        null
                    }
                ) ?: PokemonPreview(
                    name = name,
                    evolutionText = evolutionText,
                    isLoading = true
                )
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
                result.fold(
                    onSuccess = { pokemon ->
                        launchAssetRetrieval(pokemon)
                        if (pokemon.artwork != null) {
                            pokemon
                        } else {
                            Timber.d("Artwork for variety ${varieties[it].varietyName} is null")
                            null
                        }
                    },
                    onFailure = { throwable ->
                        Timber.e(
                            throwable,
                            "Fetching variety ${varieties[it].varietyName} was not successful"
                        )
                        null
                    }
                )
            }
        }
    }

    companion object {
        const val ERROR_LOADING_POKEMON = "Error loading Pokemon"
    }
}
