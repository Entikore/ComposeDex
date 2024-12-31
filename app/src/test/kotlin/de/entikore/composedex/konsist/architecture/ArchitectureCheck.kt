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
package de.entikore.composedex.konsist.architecture

import androidx.lifecycle.ViewModel
import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.constructors
import com.lemonappdev.konsist.api.ext.list.parameters
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withParentOf
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class ArchitectureCheck {

    @Test
    fun `clean architecture layers have correct dependencies`() {
        Konsist.scopeFromProduction()
            .assertArchitecture {
                val data = Layer("Data", "$PACKAGE_NAME.data..")
                val domain = Layer("Domain", "$PACKAGE_NAME.domain..")
                val ui = Layer("UI", "$PACKAGE_NAME.ui..")

                domain.dependsOnNothing()
                data.dependsOn(domain)
                ui.dependsOn(domain)
            }
    }

    @Test
    fun `Classes with 'UseCases' suffix should expose one public function with 'operator' modifier named 'invoke'`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .withNameEndingWith(USE_CASE)
            .assertTrue {
                val hasSingleInvokeOperatorMethod = it.hasFunction { function ->
                    function.name == "invoke" && function.hasPublicOrDefaultModifier && function.hasOperatorModifier
                }

                hasSingleInvokeOperatorMethod && it.countFunctions { item -> item.hasPublicOrDefaultModifier } == 1
            }
    }

    @Test
    fun `Classes with 'UseCase' suffix should reside in 'domain' and 'usecase' package`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .withNameEndingWith(USE_CASE)
            .assertTrue { it.resideInPackage("..domain..usecase..") }
    }

    @Test
    fun `ViewModels do not access repositories directly`() {
        val viewModels = Konsist.scopeFromProduction().classes().withNameEndingWith(VIEW_MODEL)

        viewModels.constructors.parameters.assertFalse(
            additionalMessage = "ViewModels should not access repositories directly and use UseCases instead"
        ) {
            it.type.resideInPackage("$PACKAGE_NAME.domain.repository") &&
                it.type.name.endsWith(REPOSITORY)
        }
    }

    @Test
    fun `Validate ViewModel has single constructor with private parameters`() {
        Konsist.scopeFromProduction().classes().withParentOf(
            ViewModel::class
        ).filter { !it.hasAbstractModifier }.assertTrue { declaration ->
            val hasSingleConstructor = declaration.constructors.size == 1
            val constructorParametersHavePrivateModifier = declaration.constructors.first().parameters.all {
                it.modifiers.isEmpty() ||
                    (it.hasValModifier && it.hasModifier(KoModifier.PRIVATE))
            }
            hasSingleConstructor && constructorParametersHavePrivateModifier
        }
    }

    companion object {
        const val PACKAGE_NAME = "de.entikore.composedex"
        const val USE_CASE = "UseCase"
        const val VIEW_MODEL = "ViewModel"
        const val REPOSITORY = "Repository"
    }
}
