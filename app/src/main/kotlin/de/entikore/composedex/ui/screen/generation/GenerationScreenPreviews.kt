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
package de.entikore.composedex.ui.screen.generation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.entikore.composedex.domain.model.generation.Generation
import de.entikore.composedex.ui.screen.shared.PokemonUiState

@Composable
@Preview
private fun GenerationScreenLoadingPreview() {
    GenerationScreenContent(
        screenState = GenerationScreenUiState.Loading,
        {},
        { _, _ -> },
        {}
    )
}

@Composable
@Preview
private fun GenerationScreenErrorPreview() {
    GenerationScreenContent(
        screenState = GenerationScreenUiState.Error,
        {},
        { _, _ -> },
        {}
    )
}

@Composable
@Preview
private fun GenerationScreenSuccessPreview() {
    GenerationScreenContent(
        screenState = GenerationScreenUiState.Success(
            generations = listOf(
                Generation(1, "Generation-1", emptyList(), 0),
                Generation(2, "Generation-2", emptyList(), 0),
                Generation(3, "Generation-3", emptyList(), 0),
                Generation(4, "Generation-4", emptyList(), 0)
            )
        ),
        {},
        { _, _ -> },
        {}
    )
}

@Composable
@Preview
private fun GenerationScreenSuccessSelectedPreview() {
    GenerationScreenContent(
        screenState = GenerationScreenUiState.Success(
            generations = listOf(
                Generation(1, "Generation-1", emptyList(), 0),
                Generation(2, "Generation-2", emptyList(), 0),
                Generation(3, "Generation-3", emptyList(), 0),
                Generation(4, "Generation-4", emptyList(), 0)
            ),
            selectedGeneration = SelectedGenerationUiState.Loading
        ),
        {},
        { _, _ -> },
        {}
    )
}

@Composable
@Preview
private fun GenerationScreenSuccessSelected2Preview() {
    GenerationScreenContent(
        screenState = GenerationScreenUiState.Success(
            generations = listOf(
                Generation(1, "Generation-1", emptyList(), 0),
                Generation(2, "Generation-2", emptyList(), 0),
                Generation(3, "Generation-3", emptyList(), 0),
                Generation(4, "Generation-4", emptyList(), 0)
            ),
            selectedGeneration = SelectedGenerationUiState.Error
        ),
        {},
        { _, _ -> },
        {}
    )
}

@Composable
@Preview
private fun GenerationScreenSuccessSelected3Preview() {
    GenerationScreenContent(
        screenState = GenerationScreenUiState.Success(
            generations = listOf(
                Generation(1, "Generation-1", emptyList(), 0),
                Generation(2, "Generation-2", emptyList(), 0),
                Generation(3, "Generation-3", emptyList(), 0),
                Generation(4, "Generation-4", emptyList(), 0)
            ),
            selectedGeneration = SelectedGenerationUiState.Success(
                selectedGeneration = Generation(1, "Generation-1", emptyList(), 0),
                pokemonState = PokemonUiState.Loading,
                showLoadingItem = true
            )
        ),
        {},
        { _, _ -> },
        {}
    )
}

@Composable
@Preview
private fun GenerationScreenSuccessSelected4Preview() {
    GenerationScreenContent(
        screenState = GenerationScreenUiState.Success(
            generations = listOf(
                Generation(1, "Generation-1", emptyList(), 0),
                Generation(2, "Generation-2", emptyList(), 0),
                Generation(3, "Generation-3", emptyList(), 0),
                Generation(4, "Generation-4", emptyList(), 0)
            ),
            selectedGeneration = SelectedGenerationUiState.Success(
                selectedGeneration = Generation(1, "Generation-1", emptyList(), 0),
                pokemonState = PokemonUiState.Error,
                showLoadingItem = true
            )
        ),
        {},
        { _, _ -> },
        {}
    )
}
