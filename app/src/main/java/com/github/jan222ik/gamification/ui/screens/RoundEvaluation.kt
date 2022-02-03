package com.github.jan222ik.gamification.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.github.jan222ik.gamification.ui.components.SingeRoundEval
import com.github.jan222ik.gamification.ui.logic.Game
import com.google.accompanist.pager.ExperimentalPagerApi


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ColumnScope.RoundEvaluation(game: Game) {
    Row(
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { game.endGame() }
        ) {
            Text(text = "Historie und Spielende")
        }
    }
    game.historyPerRound[game.round]?.let {
        SingeRoundEval(roundHistory = it, nextRound = { game.nextRound() })
    }
}



