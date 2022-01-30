package com.github.jan222ik.gamification

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.jan222ik.gamification.ui.components.SingeRoundEval
import com.github.jan222ik.gamification.ui.logic.Game
import com.github.jan222ik.gamification.ui.logic.GameState
import com.github.jan222ik.gamification.ui.screens.GameHistory
import com.github.jan222ik.gamification.ui.screens.NewGameSettings
import com.github.jan222ik.gamification.ui.screens.PlayCardScreen
import com.github.jan222ik.gamification.ui.screens.RoundEvaluation
import com.github.jan222ik.gamification.ui.theme.GamificationTheme
import com.google.accompanist.pager.ExperimentalPagerApi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamificationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Game()
                }
            }
        }
    }

    override fun onBackPressed() {}
}

@Composable
fun Game() {
    val game = remember {
        mutableStateOf<Game?>(null)
    }
    when (val g = game.value) {
        null -> NewGameSettings(onNextGame = { game.value = it })
        else -> RunningGame(g, nextGame = { game.value = null })
    }
}


@Composable
fun RunningGame(game: Game, nextGame: () -> Unit) {
    Column {
        when (game.gameState) {
            GameState.ROUND_PLAY_CARDS -> PlayCardScreen(game)
            GameState.ROUND_EVALUATE_EFFECTS -> RoundEvaluation(game = game)
            GameState.HISTORY -> GameHistory(game = game, nextGame)
        }
    }
}


