package com.example.androiddevchallenge


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*


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
    labelStyle: TextStyle = MaterialTheme.typography.overline,
    timeStyle: TextStyle = MaterialTheme.typography.h2,
    scope: CoroutineScope = rememberCoroutineScope()
) {

    val ticker by viewModel.ticker.collectAsState()

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
        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
        border = BorderStroke(2.dp, blueGrey), modifier = Modifier
            .width(cellWidth)
            .fillMaxHeight(0.66f)
    ) {}
    Button(onClick = {
        viewModel.startTimer(66 * 1000)
    }) {
        Text(text = "Start : $seconds")
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(2.0f, true))

        Label(R.string.label_hours, padding = padding, style = labelStyle)

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.weight(2.0f, true)
        ) {
            val scrollState = rememberLazyListState()
            LazyRow(state = scrollState) {
                items(1000) {
                    val fontSize = animatedFontSize(scrollState, it, cellWidthPx)
                    Text(
                        text = "$it",
                        modifier = Modifier.width(cellWidth),
                        style = timeStyle,
                        textAlign = TextAlign.Center,
                        fontSize = fontSize
                    )
                }
            }
            scope.launch { scrollState.scrollToItem(hoursIndex, hoursScroll) }
        }

        Label(R.string.label_minutes, padding = padding, style = labelStyle)

        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.weight(2.0f, true)) {
            val scrollState = rememberLazyListState()
            LazyRow(state = scrollState) {
                items(1000) {
                    val fontSize = animatedFontSize(scrollState, it, cellWidthPx)
                    Text(
                        text = "${it % 60}",
                        modifier = Modifier.width(cellWidth),
                        style = timeStyle,
                        textAlign = TextAlign.Center,
                        fontSize = fontSize
                    )
                }
            }
            scope.launch { scrollState.scrollToItem(minutesIndex, minutesScroll) }
        }

        Label(R.string.label_seconds, padding = padding, style = labelStyle)

        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.weight(2.0f, true)) {
            val scrollState = rememberLazyListState()
            LazyRow(state = scrollState) {
                items(1000) {
                    val fontSize = animatedFontSize(scrollState, it, cellWidthPx)
                    Text(
                        text = "${it % 60}",
                        modifier = Modifier.width(cellWidth),
                        style = timeStyle,
                        textAlign = TextAlign.Center,
                        fontSize = fontSize
                    )
                }
            }
            scope.launch { scrollState.scrollToItem(secondsIndex, scroll) }
        }

        Spacer(modifier = Modifier.weight(7.0f, true))
    }
}

@Composable
fun ColumnScope.Label(id: Int, padding: Dp = 16.dp, style: TextStyle = MaterialTheme.typography.overline) {
    Box(
        contentAlignment = Alignment.BottomCenter, modifier = Modifier
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

