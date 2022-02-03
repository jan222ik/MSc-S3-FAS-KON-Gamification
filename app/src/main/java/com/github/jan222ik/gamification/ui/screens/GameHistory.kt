package com.github.jan222ik.gamification.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.jan222ik.gamification.ui.components.SingeRoundEval
import com.github.jan222ik.gamification.ui.logic.Game
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ColumnScope.GameHistory(game: Game, nextGame: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = { game.continueGame() }
        ) {
            Text(text = "Spiel fortsetzen")
        }
        TextButton(
            onClick = nextGame
        ) {
            Text(text = "Nächstes Spiel")
        }
    }
    val pagerState = rememberPagerState()
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        val scope = rememberCoroutineScope()

        Row(Modifier.width(50.dp)) {
            AnimatedVisibility(visible = pagerState.currentPage > 0) {
                IconButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            Integer.max(
                                0,
                                pagerState.currentPage.dec()
                            )
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Filled.RemoveCircleOutline, contentDescription = null)
                }
            }
        }
        Text(
            text = "Runde: ${pagerState.currentPage.inc()} / ${pagerState.pageCount}"
        )
        Row(Modifier.width(50.dp)) {
            AnimatedVisibility(visible = pagerState.currentPage < pagerState.pageCount.dec()) {
                IconButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            Integer.min(
                                pagerState.pageCount.dec(),
                                pagerState.currentPage.inc()
                            )
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Filled.AddCircleOutline, contentDescription = null)
                }
            }
        }
    }
    VerticalPager(count = game.historyPerRound.size, state = pagerState) { page ->
        val hist = game.historyPerRound[page]
        Column {
            SingeRoundEval(roundHistory = hist!!, nextRound = null)
        }
    }
}