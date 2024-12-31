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
package de.entikore.composedex.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.entikore.composedex.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HorizontalPageIndicator(pagerState: PagerState, modifier: Modifier = Modifier) {
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val alpha =
                if (pagerState.currentPage == iteration) 1f else 0.3f
            Image(
                painter = painterResource(id = R.drawable.ic_ball_red),
                contentDescription = stringResource(R.string.page_indicator_icon),
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.medium_indicator_size))
                    .padding(horizontal = dimensionResource(id = R.dimen.small_padding))
                    .alpha(alpha)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VerticalPageIndicator(pagerState: PagerState, modifier: Modifier = Modifier) {
    FlowColumn(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        val times = pagerState.pageCount
        if (times > 1) {
            repeat(times) { iteration ->
                val alpha = if (pagerState.currentPage == iteration) 1f else 0.3f
                Image(
                    painter = painterResource(id = R.drawable.ic_ball_red),
                    contentDescription = stringResource(R.string.page_indicator_icon),
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.medium_indicator_size))
                        .padding(vertical = dimensionResource(id = R.dimen.small_padding))
                        .alpha(alpha)
                )
            }
        }
    }
}
