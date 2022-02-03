package com.github.jan222ik.gamification.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.jan222ik.gamification.data.CardData
import com.github.jan222ik.gamification.ui.components.PlayerNameChip
import com.github.jan222ik.gamification.ui.logic.Game
import com.github.jan222ik.gamification.ui.logic.GameErrors


@Composable
fun PlayCardScreen(game: Game) {
    val player = remember(game.playerIdx) {
        game.activePlayers[game.playerIdx]
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        Text(text = "Runde: ${game.round.inc()}")
        Text(text = "Spieler:")
        PlayerNameChip(player = player)
    }
    var cardNumber by remember(game.playerIdx) {
        mutableStateOf("")
    }
    var hasError by remember(game.playerIdx) {
        mutableStateOf<String?>(null)
    }
    var selectedCard by remember(game.playerIdx) {
        mutableStateOf<CardData?>(null)
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(120.dp)
                    .align(Alignment.CenterHorizontally),
                value = cardNumber,
                onValueChange = {
                    cardNumber = it
                    hasError = null
                    try {
                        val cardId = it.toIntOrNull()
                        selectedCard = if (cardId != null) {
                            game.getCardForID(cardId)
                        } else {
                            null
                        }

                    } catch (e: GameErrors.NoSuchCard) {
                        hasError = "Dies ist keine Karte."
                        selectedCard = null
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                decorationBox = { innerTF ->
                    Surface(
                        tonalElevation = 32.dp,
                        modifier = Modifier.padding(4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Kartennummer:")
                            Surface(
                                tonalElevation = (-32).dp,
                                modifier = Modifier.padding(8.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    innerTF()
                                    if (cardNumber == "") {
                                        Text(text = "Zahl eingeben!")
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = hasError != null
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = null
                                    )
                                    Text(text = hasError.toString())
                                }
                            }
                        }
                    }
                },
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(Color.White)
            )
            AnimatedVisibility(visible = selectedCard != null) {
                if (selectedCard?.hasReceiver == true) {
                    Column() {
                        Text(
                            text = "Empfänger ist:",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        game.activePlayers
                            .filterNot { it == game.activePlayers[game.playerIdx] }
                            .chunked(3)
                            .forEach { l ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        5.dp,
                                        Alignment.CenterHorizontally
                                    )
                                ) {
                                    l.forEach {
                                        Button(
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = it.color,
                                                contentColor = it.textColor
                                            ),
                                            onClick = {
                                                game.playCard(selectedCard!!, it)
                                                game.nextPlayer()
                                            }
                                        ) {
                                            Text(text = it.name)
                                        }
                                    }
                                }
                            }

                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Button(
                            onClick = {
                                selectedCard?.let {
                                    game.playCard(selectedCard!!, null)
                                    game.nextPlayer()
                                }
                            }
                        ) {
                            Text(text = "Karte spielen!")
                        }
                    }
                }
            }
        }
    }
}