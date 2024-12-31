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
package de.entikore.composedex.domain.usecase.base

/**
 * Represents a generic use case that accepts parameters.
 *
 * @param P The type of parameters accepted by the use case.
 * @param T The type of result returned by the use case.
 */
abstract class ParamsUseCase<in P, out T> {
    abstract operator fun invoke(params: P): T
}
