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
package de.entikore.sharedtestcode

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.entikore.composedex.data.remote.getUrlPath
import de.entikore.composedex.data.remote.model.PokemonInfoRemote
import de.entikore.composedex.data.remote.model.evolution.ChainRemote
import de.entikore.composedex.data.remote.model.evolution.EvolutionChainRemote
import de.entikore.composedex.data.remote.model.generation.GenerationListRemote
import de.entikore.composedex.data.remote.model.generation.GenerationRemote
import de.entikore.composedex.data.remote.model.pokemon.PokemonRemote
import de.entikore.composedex.data.remote.model.species.PokemonSpeciesRemote
import de.entikore.composedex.data.remote.model.type.TypeListRemote
import de.entikore.composedex.data.remote.model.type.TypeRemote
import de.entikore.composedex.domain.model.pokemon.ChainLink
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class TestModelFactory {

    companion object {

        fun getTestModel(name: String): TestModel =
            testModels[name] ?: error("No Testmodel with name $name found")

        fun readFileWithoutNewLineFromResources(fileName: String): String {
            val builder = StringBuilder()
            try {
                val inputStream: InputStream? =
                    TestModelFactory::class.java.classLoader?.getResourceAsStream(fileName)

                val reader = BufferedReader(InputStreamReader(inputStream))

                reader.useLines { lines ->
                    lines.forEach {
                        builder.append(it)
                    }
                }
            } catch (e: Exception) {
                throw IOException("Error loading json $fileName: $e")
            }
            return builder.toString()
        }

        private inline fun <reified T> getData(fileName: String): T {
            val json = readFileWithoutNewLineFromResources(fileName)
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter = moshi.adapter(T::class.java)
            return jsonAdapter.fromJson(json)!!
        }

        fun getPokemonInfoRemote(testModel: TestModel): PokemonInfoRemote = PokemonInfoRemote(
            getData<PokemonRemote>(testModel.pokemonFile),
            getData<PokemonSpeciesRemote>(testModel.speciesFile),
            mutableListOf<TypeRemote>().apply {
                for (type in testModel.typeFiles) {
                    this.add(getData<TypeRemote>(type))
                }
            },
            processChain(chain = getData<EvolutionChainRemote>(testModel.chainFile).chain)
        )

        fun getTypeListRemote(): TypeListRemote = getData<TypeListRemote>(TYPES_FILE)
        fun getTypeRemote(fileName: String): TypeRemote = getData<TypeRemote>(fileName)
        fun getGenerationListRemote(): GenerationListRemote = getData<GenerationListRemote>(
            GENERATIONS_FILE
        )
        fun getGenerationRemote(fileName: String): GenerationRemote = getData<GenerationRemote>(fileName)

        private fun processChain(
            map: Map<Int, List<ChainLink>> = mutableMapOf(),
            chain: ChainRemote?,
            rank: Int = 0
        ): Map<Int, List<ChainLink>> {
            val chainList = map.toMutableMap()
            if (chain == null) return chainList
            val oldList = chainList[rank].orEmpty().toMutableList().apply {
                add(ChainLink(chain.species.name, getUrlPath(chain.species.url), chain.isBaby))
            }
            chainList[rank] = oldList
            for (link in chain.evolvesTo) {
                chainList.putAll(processChain(chainList, link, rank + 1))
            }
            return chainList
        }
    }
}
