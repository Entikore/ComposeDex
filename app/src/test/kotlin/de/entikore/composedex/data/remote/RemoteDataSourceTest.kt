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
package de.entikore.composedex.data.remote

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.entikore.composedex.MainCoroutineRule
import de.entikore.sharedtestcode.GENERATIONS_FILE
import de.entikore.sharedtestcode.GEN_II_FILE
import de.entikore.sharedtestcode.GEN_II_ID
import de.entikore.sharedtestcode.GEN_II_NAME
import de.entikore.sharedtestcode.GEN_I_FILE
import de.entikore.sharedtestcode.GEN_I_ID
import de.entikore.sharedtestcode.GEN_I_NAME
import de.entikore.sharedtestcode.GEN_VI_FILE
import de.entikore.sharedtestcode.GEN_VI_ID
import de.entikore.sharedtestcode.GEN_VI_NAME
import de.entikore.sharedtestcode.POKEMON_DITTO_ID
import de.entikore.sharedtestcode.POKEMON_DITTO_NAME
import de.entikore.sharedtestcode.POKEMON_GLOOM_ID
import de.entikore.sharedtestcode.POKEMON_GLOOM_NAME
import de.entikore.sharedtestcode.POKEMON_LAPRAS_ID
import de.entikore.sharedtestcode.POKEMON_LAPRAS_NAME
import de.entikore.sharedtestcode.POKEMON_ODDISH_NAME
import de.entikore.sharedtestcode.TYPES_FILE
import de.entikore.sharedtestcode.TYPE_GRASS_FILE
import de.entikore.sharedtestcode.TYPE_GRASS_NAME
import de.entikore.sharedtestcode.TYPE_ICE_FILE
import de.entikore.sharedtestcode.TYPE_ICE_NAME
import de.entikore.sharedtestcode.TYPE_NORMAL_FILE
import de.entikore.sharedtestcode.TYPE_NORMAL_NAME
import de.entikore.sharedtestcode.TYPE_POISON_FILE
import de.entikore.sharedtestcode.TYPE_POISON_NAME
import de.entikore.sharedtestcode.TYPE_WATER_FILE
import de.entikore.sharedtestcode.TYPE_WATER_NAME
import de.entikore.sharedtestcode.TestModelFactory.Companion.getGenerationListRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getGenerationRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getPokemonInfoRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTestModel
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTypeListRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.getTypeRemote
import de.entikore.sharedtestcode.TestModelFactory.Companion.readFileWithoutNewLineFromResources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.stream.Stream

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineRule::class)
class RemoteDataSourceTest {

    private lateinit var api: ComposeDexApi
    private lateinit var mockWebServer: MockWebServer
    private lateinit var moshi: Moshi
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var remoteDataSource: RemoteDataSource

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        mockWebServer = MockWebServer()
        api =
            Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(mockWebServer.url("/"))
                .build()
                .create(ComposeDexApi::class.java)
        remoteDataSource = RemoteDataSource(api, testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @ParameterizedTest
    @MethodSource("providePokemonNamesAndId")
    fun `getPokemonInfoRemoteBySpeciesName returns Success with expected result on correct response`(
        name: String
    ) = runTest {
        val testModel = getTestModel(name)
        val expectedPokemonInfoRemote = getPokemonInfoRemote(testModel)
        mockWebServer.apply {
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.speciesFile))
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.pokemonFile))
            for (type in testModel.typeFiles) {
                this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(type))
            }
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.chainFile))
        }

        val fullPokemonInfo = remoteDataSource.getPokemonInfoRemoteBySpeciesName(name)

        assertThat(fullPokemonInfo).isInstanceOf(ApiResponse.Success::class.java)
        assertThat((fullPokemonInfo as ApiResponse.Success).data).isEqualTo(
            expectedPokemonInfoRemote
        )
    }

    @Test
    fun `getPokemonInfoRemoteBySpeciesName returns Error with RemoteDataSourceException on malformed response`() =
        runTest {
            mockWebServer.buildAndEnqueueMockResponse(DEFINITELY_NOT_A_JSON)

            val fullPokemonInfo =
                remoteDataSource.getPokemonInfoRemoteBySpeciesName(POKEMON_ODDISH_NAME)

            assertThat(fullPokemonInfo).isInstanceOf(ApiResponse.Error::class.java)
            assertThat((fullPokemonInfo as ApiResponse.Error).exception).isInstanceOf(
                RemoteDataSourceException::class.java
            )
            assertThat(fullPokemonInfo.userMessage).isEqualTo(
                RemoteDataSource.ERROR_POKEMON_NOT_FOUND.format(
                    POKEMON_ODDISH_NAME
                )
            )
        }

    @ParameterizedTest
    @MethodSource("provideHttpCodeAndTestModel")
    fun `getPokemonInfoRemoteBySpeciesName returns Error with RemoteDataSourceException on different HTTP Codes`(
        code: Int,
        name: String
    ) = runTest {
        val testModel = getTestModel(name)
        mockWebServer.apply {
            this.buildAndEnqueueMockResponse(
                readFileWithoutNewLineFromResources(testModel.speciesFile),
                code
            )
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.pokemonFile))
            for (type in testModel.typeFiles) {
                this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(type))
            }
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.chainFile))
        }

        val fullPokemonInfo = remoteDataSource.getPokemonInfoRemoteBySpeciesName(name)

        assertThat(fullPokemonInfo).isInstanceOf(ApiResponse.Error::class.java)
        assertThat((fullPokemonInfo as ApiResponse.Error).exception).isInstanceOf(
            RemoteDataSourceException::class.java
        )
        assertThat(fullPokemonInfo.userMessage).isEqualTo(
            RemoteDataSource.ERROR_POKEMON_NOT_FOUND.format(
                name
            )
        )
    }

    @ParameterizedTest
    @MethodSource("providePokemonNamesAndId")
    fun `getPokemonInfoRemoteByName returns Success with expected result on correct response`(
        name: String
    ) = runTest {
        val testModel = getTestModel(name)
        val expectedPokemonInfoRemote = getPokemonInfoRemote(testModel)
        mockWebServer.apply {
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.pokemonFile))
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.speciesFile))
            for (type in testModel.typeFiles) {
                this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(type))
            }
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.chainFile))
        }

        val fullPokemonInfo = remoteDataSource.getPokemonInfoRemoteByName(name)

        assertThat(fullPokemonInfo).isInstanceOf(ApiResponse.Success::class.java)
        assertThat((fullPokemonInfo as ApiResponse.Success).data).isEqualTo(
            expectedPokemonInfoRemote
        )
    }

    @Test
    fun `getPokemonInfoRemoteByName returns Error with RemoteDataSourceException on malformed response`() =
        runTest {
            mockWebServer.buildAndEnqueueMockResponse(DEFINITELY_NOT_A_JSON)

            val fullPokemonInfo = remoteDataSource.getPokemonInfoRemoteByName(POKEMON_ODDISH_NAME)

            assertThat(fullPokemonInfo).isInstanceOf(ApiResponse.Error::class.java)
            assertThat((fullPokemonInfo as ApiResponse.Error).exception).isInstanceOf(
                RemoteDataSourceException::class.java
            )
            assertThat(fullPokemonInfo.userMessage).isEqualTo(
                RemoteDataSource.ERROR_POKEMON_NOT_FOUND.format(
                    POKEMON_ODDISH_NAME
                )
            )
        }

    @ParameterizedTest
    @MethodSource("provideHttpCodeAndTestModel")
    fun `getPokemonInfoRemoteByName returns Error with RemoteDataSourceException on different HTTP Codes`(
        code: Int,
        name: String
    ) = runTest {
        val testModel = getTestModel(name)
        mockWebServer.apply {
            this.buildAndEnqueueMockResponse(
                readFileWithoutNewLineFromResources(testModel.pokemonFile),
                code
            )
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.speciesFile))
            for (type in testModel.typeFiles) {
                this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(type))
            }
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.chainFile))
        }

        val fullPokemonInfo = remoteDataSource.getPokemonInfoRemoteByName(name)

        assertThat(fullPokemonInfo).isInstanceOf(ApiResponse.Error::class.java)
        assertThat((fullPokemonInfo as ApiResponse.Error).exception).isInstanceOf(
            RemoteDataSourceException::class.java
        )
        assertThat(fullPokemonInfo.userMessage).isEqualTo(
            RemoteDataSource.ERROR_POKEMON_NOT_FOUND.format(
                name
            )
        )
    }

    @ParameterizedTest
    @MethodSource("providePokemonNamesAndId")
    fun `getPokemonInfoRemoteById returns Success with expected result on correct response`(
        name: String,
        id: Int
    ) = runTest {
        val testModel = getTestModel(name)
        val expectedPokemonInfoRemote = getPokemonInfoRemote(testModel)
        mockWebServer.apply {
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.pokemonFile))
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.speciesFile))
            for (type in testModel.typeFiles) {
                this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(type))
            }
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.chainFile))
        }

        val fullPokemonInfo = remoteDataSource.getPokemonInfoRemoteById(id)

        assertThat(fullPokemonInfo).isInstanceOf(ApiResponse.Success::class.java)
        assertThat((fullPokemonInfo as ApiResponse.Success).data).isEqualTo(
            expectedPokemonInfoRemote
        )
        assertThat(fullPokemonInfo.data.pokemon.id).isEqualTo(id)
    }

    @Test
    fun `getPokemonInfoRemoteById returns Error with RemoteDataSourceException on malformed response`() =
        runTest {
            mockWebServer.buildAndEnqueueMockResponse(DEFINITELY_NOT_A_JSON)

            val fullPokemonInfo = remoteDataSource.getPokemonInfoRemoteById(POKEMON_GLOOM_ID)

            assertThat(fullPokemonInfo).isInstanceOf(ApiResponse.Error::class.java)
            assertThat((fullPokemonInfo as ApiResponse.Error).exception).isInstanceOf(
                RemoteDataSourceException::class.java
            )
            assertThat(fullPokemonInfo.userMessage).isEqualTo(
                RemoteDataSource.ERROR_POKEMON_ID_NOT_FOUND.format(
                    POKEMON_GLOOM_ID
                )
            )
        }

    @ParameterizedTest
    @MethodSource("provideHttpCodeAndTestModel")
    fun `getPokemonInfoRemoteById returns Error with RemoteDataSourceException on different HTTP Codes`(
        code: Int,
        name: String,
        id: Int
    ) = runTest {
        val testModel = getTestModel(name)
        mockWebServer.apply {
            this.buildAndEnqueueMockResponse(
                readFileWithoutNewLineFromResources(testModel.pokemonFile),
                code
            )
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.speciesFile))
            for (type in testModel.typeFiles) {
                this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(type))
            }
            this.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(testModel.chainFile))
        }

        val fullPokemonInfo = remoteDataSource.getPokemonInfoRemoteById(id)

        assertThat(fullPokemonInfo).isInstanceOf(ApiResponse.Error::class.java)
        assertThat((fullPokemonInfo as ApiResponse.Error).exception).isInstanceOf(
            RemoteDataSourceException::class.java
        )
        assertThat(fullPokemonInfo.userMessage).isEqualTo(
            RemoteDataSource.ERROR_POKEMON_ID_NOT_FOUND.format(
                id
            )
        )
    }

    @Test
    fun `getPokemonTypes returns Success with expected result on correct response`() =
        runTest {
            val expectedTypeListRemote = getTypeListRemote()
            mockWebServer.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources("type/type.json"))

            val typeListRemote = remoteDataSource.getPokemonTypes()

            assertThat(typeListRemote).isInstanceOf(ApiResponse.Success::class.java)
            assertThat((typeListRemote as ApiResponse.Success).data).isEqualTo(
                expectedTypeListRemote
            )
        }

    @Test
    fun `getPokemonTypes returns Error with RemoteDataSourceException on malformed response`() =
        runTest {
            mockWebServer.buildAndEnqueueMockResponse(DEFINITELY_NOT_A_JSON)

            val typeListRemote = remoteDataSource.getPokemonTypes()

            assertThat(typeListRemote).isInstanceOf(ApiResponse.Error::class.java)
            assertThat((typeListRemote as ApiResponse.Error).exception).isInstanceOf(
                RemoteDataSourceException::class.java
            )
            assertThat(typeListRemote.userMessage).isEqualTo(RemoteDataSource.ERROR_TYPES)
        }

    @ParameterizedTest
    @MethodSource("provideGetPokemonTypesTestData")
    fun `getPokemonTypes returns Error with RemoteDataSourceException on different HTTP Codes`(
        responseCode: Int,
        body: String
    ) = runTest {
        val expectedErrorMessage =
            RemoteDataSource.ERROR_TYPES
        mockWebServer.buildAndEnqueueMockResponse(body, responseCode)

        val typeListRemote = remoteDataSource.getPokemonTypes()

        assertThat(typeListRemote).isInstanceOf(ApiResponse.Error::class.java)
        assertThat((typeListRemote as ApiResponse.Error).exception).isInstanceOf(RemoteDataSourceException::class.java)
        assertThat(typeListRemote.userMessage).isEqualTo(expectedErrorMessage)
    }

    @ParameterizedTest
    @MethodSource("providePokemonTypeNamesAndFiles")
    fun `getPokemonTypeByName returns Success with expected result on correct response`(
        name: String,
        file: String
    ) = runTest {
        val expectedTypeRemote = getTypeRemote(file)
        mockWebServer.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(file))

        val actualTypeRemote = remoteDataSource.getPokemonTypeByName(name)

        assertThat(actualTypeRemote).isInstanceOf(ApiResponse.Success::class.java)
        assertThat((actualTypeRemote as ApiResponse.Success).data).isEqualTo(
            expectedTypeRemote
        )
    }

    @Test
    fun `getPokemonTypeByName returns Error with RemoteDataSourceException on malformed response`() =
        runTest {
            mockWebServer.buildAndEnqueueMockResponse(DEFINITELY_NOT_A_JSON)

            val typeRemote = remoteDataSource.getPokemonTypeByName(TYPE_ICE_NAME)

            assertThat(typeRemote).isInstanceOf(ApiResponse.Error::class.java)
            assertThat((typeRemote as ApiResponse.Error).exception).isInstanceOf(
                RemoteDataSourceException::class.java
            )
            assertThat(typeRemote.userMessage).isEqualTo(
                RemoteDataSource.ERROR_TYPE_NOT_FOUND.format(
                    TYPE_ICE_NAME
                )
            )
        }

    @ParameterizedTest
    @MethodSource("provideHttpCodeTypeFileAndName")
    fun `getPokemonTypeByName returns Error with RemoteDataSourceException on different HTTP Codes`(
        responseCode: Int,
        file: String,
        name: String
    ) = runTest {
        val expectedErrorMessage = RemoteDataSource.ERROR_TYPE_NOT_FOUND.format(name)
        mockWebServer.buildAndEnqueueMockResponse(
            readFileWithoutNewLineFromResources(file),
            responseCode
        )

        val actualTypeRemote = remoteDataSource.getPokemonTypeByName(name)

        assertThat(actualTypeRemote).isInstanceOf(ApiResponse.Error::class.java)
        assertThat((actualTypeRemote as ApiResponse.Error).exception).isInstanceOf(
            RemoteDataSourceException::class.java
        )
        assertThat(actualTypeRemote.userMessage).isEqualTo(expectedErrorMessage)
    }

    @Test
    fun `getGenerations returns Success with expected result on correct response`() =
        runTest {
            val expectedGenerationListRemote = getGenerationListRemote()
            mockWebServer.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources("generation/generation.json"))

            val generationListRemote = remoteDataSource.getGenerations()

            assertThat(generationListRemote).isInstanceOf(ApiResponse.Success::class.java)
            assertThat((generationListRemote as ApiResponse.Success).data).isEqualTo(
                expectedGenerationListRemote
            )
        }

    @Test
    fun `getGenerations returns Error with RemoteDataSourceException on malformed response`() =
        runTest {
            mockWebServer.buildAndEnqueueMockResponse(DEFINITELY_NOT_A_JSON)

            val generationListRemote = remoteDataSource.getGenerations()

            assertThat(generationListRemote).isInstanceOf(ApiResponse.Error::class.java)
            assertThat((generationListRemote as ApiResponse.Error).exception).isInstanceOf(
                RemoteDataSourceException::class.java
            )
            assertThat(generationListRemote.userMessage).isEqualTo(RemoteDataSource.ERROR_GENERATIONS)
        }

    @ParameterizedTest
    @MethodSource("provideGetGenerationsTestData")
    fun `getGenerations returns Error with RemoteDataSourceException on different HTTP Codes`(
        responseCode: Int,
        file: String
    ) = runTest {
        val expectedErrorMessage = RemoteDataSource.ERROR_GENERATIONS
        mockWebServer.buildAndEnqueueMockResponse(file, responseCode)

        val generationListRemote = remoteDataSource.getGenerations()

        assertThat(generationListRemote).isInstanceOf(ApiResponse.Error::class.java)
        assertThat((generationListRemote as ApiResponse.Error).exception).isInstanceOf(
            RemoteDataSourceException::class.java
        )
        assertThat(generationListRemote.userMessage).isEqualTo(expectedErrorMessage)
    }

    @ParameterizedTest
    @MethodSource("provideGenerationIdsNamesAndFiles")
    fun `getGenerationByName returns Success with expected result on correct response`(
        id: Int,
        name: String,
        file: String
    ) = runTest {
        val expectedTypeRemote = getGenerationRemote(file)
        mockWebServer.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(file))

        val actualGenerationRemote = remoteDataSource.getGenerationByName(name)

        assertThat(actualGenerationRemote).isInstanceOf(ApiResponse.Success::class.java)
        assertThat((actualGenerationRemote as ApiResponse.Success).data).isEqualTo(
            expectedTypeRemote
        )
        assertThat(actualGenerationRemote.data.id).isEqualTo(id)
        assertThat(actualGenerationRemote.data.name).isEqualTo(name)
    }

    @Test
    fun `getGenerationByName returns Error with RemoteDataSourceException on malformed response`() =
        runTest {
            mockWebServer.buildAndEnqueueMockResponse(DEFINITELY_NOT_A_JSON)

            val generationListRemote =
                remoteDataSource.getGenerationByName(GEN_I_NAME)

            assertThat(generationListRemote).isInstanceOf(ApiResponse.Error::class.java)
            assertThat((generationListRemote as ApiResponse.Error).exception).isInstanceOf(
                RemoteDataSourceException::class.java
            )
            assertThat(generationListRemote.userMessage).isEqualTo(
                RemoteDataSource.ERROR_GENERATION_NOT_FOUND.format(
                    GEN_I_NAME
                )
            )
        }

    @ParameterizedTest
    @MethodSource("provideHttpCodeGenerationFileAndName")
    fun `getGenerationByName returns Error with RemoteDataSourceException on different HTTP Codes`(
        responseCode: Int,
        file: String,
        name: String
    ) = runTest {
        val expectedErrorMessage = RemoteDataSource.ERROR_GENERATION_NOT_FOUND.format(name)
        mockWebServer.buildAndEnqueueMockResponse(
            readFileWithoutNewLineFromResources(file),
            responseCode
        )

        val actualTypeRemote = remoteDataSource.getGenerationByName(name)

        assertThat(actualTypeRemote).isInstanceOf(ApiResponse.Error::class.java)
        assertThat((actualTypeRemote as ApiResponse.Error).exception).isInstanceOf(
            RemoteDataSourceException::class.java
        )
        assertThat(actualTypeRemote.userMessage).isEqualTo(expectedErrorMessage)
    }

    @ParameterizedTest
    @MethodSource("provideGenerationIdsNamesAndFiles")
    fun `getGenerationById returns Success with expected result on correct response`(
        id: Int,
        name: String,
        file: String
    ) = runTest {
        val expectedTypeRemote = getGenerationRemote(file)
        mockWebServer.buildAndEnqueueMockResponse(readFileWithoutNewLineFromResources(file))

        val actualGenerationRemote = remoteDataSource.getGenerationById(id)

        assertThat(actualGenerationRemote).isInstanceOf(ApiResponse.Success::class.java)
        assertThat((actualGenerationRemote as ApiResponse.Success).data).isEqualTo(
            expectedTypeRemote
        )
        assertThat(actualGenerationRemote.data.id).isEqualTo(id)
        assertThat(actualGenerationRemote.data.name).isEqualTo(name)
    }

    @Test
    fun `getGenerationById returns Error with RemoteDataSourceException on malformed response`() =
        runTest {
            mockWebServer.buildAndEnqueueMockResponse(DEFINITELY_NOT_A_JSON)

            val generationListRemote = remoteDataSource.getGenerationById(GEN_VI_ID)

            assertThat(generationListRemote).isInstanceOf(ApiResponse.Error::class.java)
            assertThat((generationListRemote as ApiResponse.Error).exception).isInstanceOf(
                RemoteDataSourceException::class.java
            )
            assertThat(generationListRemote.userMessage).isEqualTo(
                RemoteDataSource.ERROR_GENERATION_ID_NOT_FOUND.format(
                    GEN_VI_ID
                )
            )
        }

    @ParameterizedTest
    @MethodSource("provideHttpCodeGenerationFile")
    fun `getGenerationById returns Error with RemoteDataSourceException on different HTTP Codes`(
        responseCode: Int,
        file: String,
        id: Int
    ) = runTest {
        val expectedErrorMessage = RemoteDataSource.ERROR_GENERATION_ID_NOT_FOUND.format(id)
        mockWebServer.buildAndEnqueueMockResponse(
            readFileWithoutNewLineFromResources(file),
            responseCode
        )

        val actualTypeRemote = remoteDataSource.getGenerationById(id)

        assertThat(actualTypeRemote).isInstanceOf(ApiResponse.Error::class.java)
        assertThat((actualTypeRemote as ApiResponse.Error).exception).isInstanceOf(
            RemoteDataSourceException::class.java
        )
        assertThat(actualTypeRemote.userMessage).isEqualTo(expectedErrorMessage)
    }

    @Suppress("UnusedPrivateMember")
    companion object {

        private const val DEFINITELY_NOT_A_JSON = "definitely not a json"
        private const val HTTP_OK = 200
        private const val HTTP_BAD_REQUEST = 400
        private const val HTTP_NOT_FOUND = 404
        private const val HTTP_INTERNAL_SERVER_ERROR = 500

        @JvmStatic
        private fun providePokemonNamesAndId(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(POKEMON_GLOOM_NAME, POKEMON_GLOOM_ID),
                Arguments.of(POKEMON_DITTO_NAME, POKEMON_DITTO_ID),
                Arguments.of(POKEMON_LAPRAS_NAME, POKEMON_LAPRAS_ID),
            )
        }

        @JvmStatic
        private fun providePokemonTypeNamesAndFiles(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(TYPE_GRASS_NAME, TYPE_GRASS_FILE),
                Arguments.of(TYPE_ICE_NAME, TYPE_ICE_FILE),
                Arguments.of(TYPE_NORMAL_NAME, TYPE_NORMAL_FILE),
                Arguments.of(TYPE_POISON_NAME, TYPE_POISON_FILE),
                Arguments.of(TYPE_WATER_NAME, TYPE_WATER_FILE)
            )
        }

        @JvmStatic
        private fun provideGenerationIdsNamesAndFiles(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(GEN_I_ID, GEN_I_NAME, GEN_I_FILE),
                Arguments.of(GEN_II_ID, GEN_II_NAME, GEN_II_FILE),
                Arguments.of(GEN_VI_ID, GEN_VI_NAME, GEN_VI_FILE),
            )
        }

        @JvmStatic
        private fun provideHttpCodeAndTestModel(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(HTTP_BAD_REQUEST, POKEMON_DITTO_NAME, POKEMON_DITTO_ID),
                Arguments.of(HTTP_NOT_FOUND, POKEMON_DITTO_NAME, POKEMON_DITTO_ID),
                Arguments.of(HTTP_INTERNAL_SERVER_ERROR, POKEMON_DITTO_NAME, POKEMON_DITTO_ID)
            )
        }

        @JvmStatic
        private fun provideHttpCodeTypeFileAndName(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(HTTP_BAD_REQUEST, TYPE_ICE_FILE, TYPE_ICE_NAME),
                Arguments.of(HTTP_NOT_FOUND, TYPE_ICE_FILE, TYPE_ICE_NAME),
                Arguments.of(HTTP_INTERNAL_SERVER_ERROR, TYPE_ICE_FILE, TYPE_ICE_NAME)
            )
        }

        @JvmStatic
        private fun provideHttpCodeGenerationFile(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(HTTP_BAD_REQUEST, GEN_VI_FILE, GEN_VI_ID),
                Arguments.of(HTTP_NOT_FOUND, GEN_VI_FILE, GEN_VI_ID),
                Arguments.of(HTTP_INTERNAL_SERVER_ERROR, GEN_VI_FILE, GEN_VI_ID)
            )
        }

        @JvmStatic
        private fun provideHttpCodeGenerationFileAndName(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(HTTP_BAD_REQUEST, GEN_VI_FILE, GEN_VI_NAME, GEN_VI_ID),
                Arguments.of(HTTP_NOT_FOUND, GEN_VI_FILE, GEN_VI_NAME, GEN_VI_ID),
                Arguments.of(HTTP_INTERNAL_SERVER_ERROR, GEN_VI_FILE, GEN_VI_NAME, GEN_VI_ID)
            )
        }

        @JvmStatic
        private fun provideGetPokemonTypesTestData() =
            provideHttpCodesAndResponseBodies(readFileWithoutNewLineFromResources(TYPES_FILE))

        @JvmStatic
        private fun provideGetGenerationsTestData() =
            provideHttpCodesAndResponseBodies(readFileWithoutNewLineFromResources(GENERATIONS_FILE))

        @JvmStatic
        private fun provideHttpCodesAndResponseBodies(responseBody: String): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    HTTP_BAD_REQUEST,
                    responseBody
                ),
                Arguments.of(
                    HTTP_NOT_FOUND,
                    responseBody
                ),
                Arguments.of(
                    HTTP_INTERNAL_SERVER_ERROR,
                    responseBody
                )
            )
        }
    }

    private fun MockWebServer.buildAndEnqueueMockResponse(body: String, responseCode: Int = HTTP_OK) =
        this.enqueue(
            MockResponse().setBody(body)
                .setResponseCode(responseCode)
        )
}
