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
package de.entikore.composedex.data.local.entity.variety.update

import de.entikore.composedex.data.local.entity.variety.VarietyEntity

/**
 * Represents an update to a [VarietyEntity]'s artwork.
 *
 * This data class holds information about the Variety whose artwork is being updated
 * and the local path to the new artwork file.
 *
 * @property varietyName The ID of the Variety whose artwork is being updated.
 * @property localArtwork The local path to the new artwork file.
 */
data class VarietyArtworkUpdate(val varietyName: String, val localArtwork: String)
