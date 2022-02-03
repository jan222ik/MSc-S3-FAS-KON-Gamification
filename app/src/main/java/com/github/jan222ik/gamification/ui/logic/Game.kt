package com.github.jan222ik.gamification.ui.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.github.jan222ik.gamification.data.CardData
import kotlin.math.max
import kotlin.math.min


class Game(
    amountPlayers: Int,
    allCards: List<CardData>
) {

    val playableCards: List<CardData> =
        allCards.filter { it.activateOnCards.let { ids -> ids.size == 1 && ids[0] == it.id } }
    val crossEffectCards: List<CardData> =
        allCards.filterNot { it.activateOnCards.let { ids -> ids.size == 1 && ids[0] == it.id } }

    companion object {
        val players = listOf(
            Player("Rot", Color.Red, Color.Black),
            Player("Blau", Color.Blue, Color.White),
            Player("Grün", Color.Green, Color.Black),
            Player("Gelb", Color.Yellow, Color.Black),
            Player("Weiß", Color.White, Color.Black),
            Player("Schwarz", Color.Black, Color.White),
        )
    }

    val activePlayers: List<Player> = players.take(min(max(amountPlayers, 3), players.size))

    var round by mutableStateOf(0)
    var playerIdx by mutableStateOf(0)
    var gameState by mutableStateOf(GameState.ROUND_PLAY_CARDS)

    val played = mutableStateMapOf<Int, List<PlayedCard>>()
    private val playedIDs = mutableSetOf<Int>()
    val scheduled = mutableStateMapOf<Int, List<PlayedCard>>()
    val historyPerRound = mutableStateMapOf<Int, RoundHistory>()

    fun playCardByID(cardId: Int, recipient: Player?): PlayedCard {
        val cardData = playableCards.find { it.id == cardId } ?: throw GameErrors.NoSuchCard(cardId)
        return playCard(
            card = cardData,
            recipient = recipient
        )
    }

    fun playCard(card: CardData, recipient: Player?, isCrossEffect: Boolean = false): PlayedCard {
        if (gameState != GameState.ROUND_PLAY_CARDS) throw GameErrors.WrongRoundState(
            expected = GameState.ROUND_PLAY_CARDS,
            actual = gameState
        )
        val sender = activePlayers[playerIdx]
        if (card.hasReceiver && recipient == null) throw GameErrors.PlayedCard.RequiresAReceiver(
            card.id
        )
        if (!card.hasReceiver && recipient != null) throw GameErrors.PlayedCard.AcceptsNoReceiver(
            card.id
        )
        if (sender == recipient) throw GameErrors.PlayedCard.HasSameReceiverSender(card.id)
        val playedCard = PlayedCard(card, sender, recipient, isCrossEffect = isCrossEffect)

        val listOfPlayedThisRound = played[round] ?: emptyList()
        played[round] = listOfPlayedThisRound + playedCard
        playedIDs.add(playedCard.data.id)

        val effectTargetRound = round + max(0, card.effectDelay)
        val listOfEffectForCard = scheduled[effectTargetRound] ?: emptyList()
        scheduled[effectTargetRound] = listOfEffectForCard + playedCard

        if (!isCrossEffect) {
            checkCrossEffects(recipient)
        }
        return playedCard
    }

    private fun checkCrossEffects(recipient: Player?) {
        crossEffectCards
            .filterNot { crossCard -> playedIDs.contains(crossCard.id) }
            .forEach { crossCard ->
                if (crossCard.activateOnCards.all { playedIDs.contains(it) }) {
                    playCard(
                        card = crossCard,
                        recipient = recipient.takeIf { crossCard.hasReceiver },
                        isCrossEffect = true
                    )
                }
            }
    }

    private fun makeRoundSummary(): RoundHistory {
        val effects = scheduled[round] ?: emptyList()
        val map = mutableMapOf<Player, Int>()
        activePlayers.forEach { map[it] = 0 }
        effects.forEach { playedCard ->
            map[playedCard.sender] = map[playedCard.sender]!! + playedCard.data.effectForSender
            if (playedCard.data.hasReceiver && playedCard.recipient != null) {
                map[playedCard.recipient] =
                    map[playedCard.recipient]!! + playedCard.data.effectForReceiver
            }
        }
        val roundHistory = RoundHistory(
            round = round,
            effectsPerPlayer = map,
            effects = effects
        )
        historyPerRound[round] = roundHistory
        return roundHistory
    }

    fun nextPlayer() {
        if (gameState != GameState.ROUND_PLAY_CARDS) throw GameErrors.WrongRoundState(
            expected = GameState.ROUND_PLAY_CARDS,
            actual = gameState
        )
        if (playerIdx.inc() == activePlayers.size) {
            gameState = GameState.ROUND_EVALUATE_EFFECTS
            makeRoundSummary()
        } else {
            playerIdx += 1
        }
    }

    fun nextRound() {
        if (gameState == GameState.ROUND_PLAY_CARDS) throw GameErrors.WrongRoundState(
            expected = GameState.ROUND_EVALUATE_EFFECTS,
            actual = gameState
        )
        round += 1
        playerIdx = 0
        gameState = GameState.ROUND_PLAY_CARDS
    }

    fun getCardForID(cardId: Int) : CardData {
        return playableCards.find { it.id == cardId } ?: throw GameErrors.NoSuchCard(cardId)
    }

    fun endGame() {
        gameState = GameState.HISTORY
    }

    fun continueGame() {
        gameState = GameState.ROUND_EVALUATE_EFFECTS
    }
}

