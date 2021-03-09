/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.blueGrey
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onEach
import java.util.Locale
import kotlin.math.roundToInt

@Preview
@Composable
fun PreviewCountdownPage() {
    CountdownPage()
}

@Composable
fun CountdownPage(
    viewModel: CountdownViewModel = viewModel(),
    cellWidth: Dp = 84.dp,
    cellWidthPx: Float = LocalDensity.current.run { cellWidth.toPx() },
    padding: Dp = 16.dp,
    labelStyle: TextStyle = MaterialTheme.typography.overline
) {

    val ticker by viewModel.ticker.collectAsState()
    val counting by viewModel.counting.collectAsState()

    val seconds = (ticker / 1000).toInt()

    val scroll = (ticker % 1000 * cellWidthPx / 1000).toInt()

    val secondsIndex = seconds % 60
    val minutesIndex = seconds / 60 % 60
    val hoursIndex = seconds / 3600

    val minutesScroll = when {
        seconds % 60 == 59 -> scroll
        else -> 0
    }

    val hoursScroll = when {
        seconds % 3600 == 3599 -> scroll
        else -> 0
    }

    Card(
        shape = RoundedCornerShape(bottomEnd = 24.dp),
        border = BorderStroke(2.dp, blueGrey),
        modifier = Modifier
            .width(cellWidth)
            .fillMaxHeight(0.66f)
    ) {}

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(2.0f, true))

        Label(R.string.label_hours, padding = padding, style = labelStyle)
        TimeView(
            index = hoursIndex,
            scroll = hoursScroll,
            counting = counting,
            text = { "$it" },
            onScrolled = {
                viewModel.hours = it
            }
        )

        Label(R.string.label_minutes, padding = padding, style = labelStyle)
        TimeView(
            index = minutesIndex,
            scroll = minutesScroll,
            counting = counting,
            text = { "${it % 60}" },
            onScrolled = {
                viewModel.minutes = it % 60
            }
        )

        Label(R.string.label_seconds, padding = padding, style = labelStyle)
        TimeView(
            index = secondsIndex,
            scroll = scroll,
            counting = counting,
            text = { "${it % 60}" },
            onScrolled = {
                viewModel.seconds = it % 60
            }
        )

        Spacer(modifier = Modifier.weight(7.0f, true))
    }
}

@Composable
fun ColumnScope.TimeView(
    index: Int,
    scroll: Int,
    counting: Boolean = false,
    cellWidth: Dp = 84.dp,
    cellWidthPx: Float = LocalDensity.current.run { cellWidth.toPx() },
    timeStyle: TextStyle = MaterialTheme.typography.h2,
    text: (Int) -> String,
    onScrolled: (Int) -> Unit
) {

    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.weight(2.0f, true)) {
        val scrollState = key(index, scroll) { rememberLazyListState(index, scroll) }
        LazyRow(state = scrollState) {
            items(1000) {

                Text(
                    text = text(it),
                    modifier = Modifier.width(cellWidth),
                    style = timeStyle,
                    textAlign = TextAlign.Center,
                    fontSize = animatedFontSize(scrollState, it, cellWidthPx)
                )
            }
        }

        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.firstVisibleItemIndex + (scrollState.firstVisibleItemScrollOffset / cellWidthPx).roundToInt() }
                .distinctUntilChanged()
                .filterNot { counting }
                .onEach {
                    onScrolled(it)
                }
                .collect()
        }
    }
}

@Composable
fun ColumnScope.Label(id: Int, padding: Dp = 16.dp, style: TextStyle = MaterialTheme.typography.overline) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .weight(1.0f, true)
            .padding(start = padding)
    ) {
        Text(stringResource(id = id).toUpperCase(Locale.ROOT), style = style)
    }
}

fun animatedFontSize(state: LazyListState, currentIndex: Int, cellWidthPx: Float): TextUnit {
    return when (currentIndex) {
        state.firstVisibleItemIndex -> 60.sp
        state.firstVisibleItemIndex + 1 -> (12 + (60 - 12) * (state.firstVisibleItemScrollOffset * 1.8 / cellWidthPx)).coerceAtMost(60.0).sp
        else -> 12.sp
    }
}
