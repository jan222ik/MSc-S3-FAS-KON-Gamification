package com.github.jan222ik.gamification.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.jan222ik.gamification.importer.CSVImporter
import com.github.jan222ik.gamification.ui.logic.Game

@Composable
fun Game() {
    val game = rememberSaveable() {
        mutableStateOf<Game?>(null)
    }
    when (val g = game.value) {
        null -> NewGameSettings(onNextGame = { game.value = it })
        else -> RunningGame(g)
    }
}

@Composable
fun NewGameSettings(onNextGame: (Game) -> Unit) {
    Box(
        modifier = Modifier
            .wrapContentHeight(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            SelectPlayerCount(onPlayerCountSelected = {
                onNextGame.invoke(Game(it, CSVImporter.loadCards()))
            })
        }
    }
}

@Composable
fun SelectPlayerCount(
    onPlayerCountSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "New Game",
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.headlineMedium
        )
        var playerCount by rememberSaveable { mutableStateOf(2) }
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Select Amount of players!")
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                2.rangeTo(6).forEach {
                    Button(
                        enabled = it != playerCount,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            playerCount = it
                        }
                    ) {
                        Text(it.toString())
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = {
                onPlayerCountSelected.invoke(playerCount)
            },
            text = {
                Text(text = "Start Game with $playerCount players")
            }
        )
    }
}

@Composable
fun RunningGame(game: Game) {
    val player = remember(game.playerIdx) {
        game.activePlayers[game.playerIdx]
    }
    Column() {
        Text(text = "Current Players: ${player.name}")
    }
}

