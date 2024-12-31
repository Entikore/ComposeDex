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
package de.entikore.composedex

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule

/**
 * Finds a semantics node with the given string resource id.
 *
 * The [onNodeWithTag] finder provided by compose ui test API, doesn't support usage of
 * string resource id to find the semantics node. This extension function accesses string resource
 * using underlying activity property and passes it to [onNodeWithTag] function as argument and
 * returns the [SemanticsNodeInteraction] object.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithTagStringId(
    @StringRes id: Int
): SemanticsNodeInteraction {
    return onNodeWithTag(activity.getString(id))
}

/**
 * Finds a semantics node with the given string resource id.
 *
 * The [onNodeWithText] finder provided by compose ui test API, doesn't support usage of
 * string resource id to find the semantics node. This extension function accesses string resource
 * using underlying activity property and passes it to [onNodeWithText] function as argument and
 * returns the [SemanticsNodeInteraction] object.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithTextStringId(
    @StringRes id: Int,
    ignoreCase: Boolean = false
): SemanticsNodeInteraction {
    return onNodeWithText(activity.getString(id), ignoreCase = ignoreCase)
}

/**
 * Semantics matcher with given string resource id for test tags.
 *
 * The [hasTestTag] finder provided by compose ui test API, doesn't support usage of
 * string resource id to build the semantics matcher. This extension function accesses string
 * resource using underlying activity property and passes it to [hasTestTag] function as argument
 * and returns the [SemanticsMatcher] object.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.hasTestTagStringId(
    @StringRes id: Int
): SemanticsMatcher {
    return hasTestTag(activity.getString(id))
}

/**
 * Semantics matcher with given string resource id for content description.
 *
 * The [hasContentDescription] finder provided by compose ui test API, doesn't support usage of
 * string resource id to build the semantics matcher. This extension function accesses string
 * resource using underlying activity property and passes it to [hasContentDescription] function as
 * argument and returns the [SemanticsMatcher] object.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.hasContentDescriptionStringId(
    @StringRes id: Int,
    substring: Boolean = false,
    ignoreCase: Boolean = false,
    vararg args: Any
): SemanticsMatcher {
    return hasContentDescription(activity.getString(id, *args), substring, ignoreCase)
}

/**
 * Semantics matcher with given string resource id for click label.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.hasClickLabel(@StringRes id: Int): SemanticsMatcher {
    val label = activity.getString(id)
    return SemanticsMatcher("Clickable action with label: $label") {
        it.config.getOrNull(
            SemanticsActions.OnClick
        )?.label == label
    }
}
