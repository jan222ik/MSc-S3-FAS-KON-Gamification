package com.github.jan222ik.gamification.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.jan222ik.gamification.ui.logic.RoundHistory
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.lang.Integer.max
import java.lang.Integer.min

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ColumnScope.SingeRoundEval(roundHistory: RoundHistory, nextRound: (() -> Unit)?) {
    val count = roundHistory.effects.size + 1
    val pagerState = rememberPagerState()
    HorizontalPager(
        count = count,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        state = pagerState
    ) { page ->
        Box(modifier = Modifier.fillMaxHeight(0.9f)) {
            if (page in 0..roundHistory.effects.size.dec()) {
                val eff = roundHistory.effects[page]
                PCard(playedCard = eff)
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 16.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterVertically
                        )
                    ) {
                        roundHistory.effectsPerPlayer.entries.forEach {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    PlayerNameChip(player = it.key)
                                }
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    val indication = when {
                                        it.value > 0 -> "+"
                                        it.value == 0 -> " "
                                        else -> ""
                                    }
                                    Text(
                                        text = "$indication${it.value} Felder",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }
                        }
                        if (nextRound != null) {
                            Button(
                                onClick = nextRound
                            ) {
                                Text(text = "Nächste Runde")
                            }
                        }
                    }
                }
            }
        }
    }
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
                            max(
                                0,
                                pagerState.currentPage.dec()
                            )
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = null)
                }
            }
        }
        Text(
            text = "Karte: ${pagerState.currentPage.inc()} / ${pagerState.pageCount}"
        )
        Row(Modifier.width(50.dp)) {
            AnimatedVisibility(visible = pagerState.currentPage < pagerState.pageCount.dec()) {
                IconButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            min(
                                pagerState.pageCount.dec(),
                                pagerState.currentPage.inc()
                            )
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}
