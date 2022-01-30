package com.github.jan222ik.gamification

import com.github.jan222ik.gamification.importer.CSVImporter
import com.github.jan222ik.gamification.ui.logic.*
import org.junit.Test

import org.junit.Assert.*
import kotlin.math.max


class GameTest {
    @Test
    fun checkImportCSV() {
        CSVImporter.loadCards()
    }

    @Test
    fun allCrossEffectPlayable() {
        val g = Game(3, CSVImporter.loadCards(CSVImporter.cards))
        g.crossEffectCards.forEach {
            g.playCard(it, null, true)
            g.assertCrossCardPlayed(it.id)
        }
    }

    @Test
    fun gamePlayerCountConstraints() {
        val over = Game(amountPlayers=7, allCards = emptyList())
        assertEquals(6, over.activePlayers.count())
        val under = Game(amountPlayers=2, allCards = emptyList())
        assertEquals(3, under.activePlayers.count())
        val upperIn = Game(amountPlayers=6, allCards = emptyList())
        assertEquals(6, upperIn.activePlayers.count())
        val lowerIn = Game(amountPlayers=3, allCards = emptyList())
        assertEquals(3, lowerIn.activePlayers.count())
    }

    @Test
    fun gamePlayCardConstraints() {
        val game = Game(amountPlayers = 3, allCards = CSVImporter.loadCards(testCards))
        val (red, blue) = game.activePlayers
        game.assertGameState(GameState.ROUND_PLAY_CARDS)
        game.assertCurrentPlayer(red)
        assertThrows(GameErrors.NoSuchCard::class.java) {
            game.playCardByID(0, blue)
        }
        assertThrows(GameErrors.PlayedCard.HasSameReceiverSender::class.java) {
            game.playCardByID(1, red)
        }
        assertThrows(GameErrors.PlayedCard.RequiresAReceiver::class.java) {
            game.playCardByID(1, null)
        }
        assertThrows(GameErrors.PlayedCard.AcceptsNoReceiver::class.java) {
            game.playCardByID(3, blue)
        }
        assertThrows(GameErrors.WrongRoundState::class.java) {
            game.nextRound()
        }
        assertThrows(GameErrors.WrongRoundState::class.java) {
            game.nextRound()
        }
    }

    private val testCards = """
        |id,activateOnCards,effectDelay,effectForReceiver,effectForSender,description,hasReceiver
        |1,1,0,-1,0,Playable: ID=1,TRUE
        |2,2,0,-2,0,Playable: ID=2,TRUE
        |3,3,0,-1,0,Playable: ID=3,FALSE
        |4,1,0,-1,0,Cross: If ID=1 is played,FALSE
        |5,"2,3",0,-1,0,"Cross: If ID={2,3} are played",FALSE
        |6,6,0,-1,0,Playable: ID=6,FALSE
        |7,7,0,-1,0,Playable: ID=7,FALSE
        |8,8,0,-1,0,Playable: ID=8,FALSE
        |9,9,0,-1,0,Playable: ID=9,FALSE
        |10,10,0,-1,0,Playable: ID=10,FALSE
    """.trimMargin()

    @Test
    fun gameTestRound() {
        val game = Game(amountPlayers = 2, allCards = CSVImporter.loadCards(testCards))
        // START Round 1
        with(game) {
            println("Playable   :\n\t" + playableCards.joinToString(",\n\t"))
            println("CrossEffect:\n\t" + crossEffectCards.joinToString(",\n\t"))
            val (red, blue, green) = activePlayers
            // START Round 1
            assertGameState(GameState.ROUND_PLAY_CARDS)
            // RED
            assertCurrentPlayer(red)
            val pc1 = playCardByID(cardId = 1, recipient = green)
            assertCardPlayed(pc1)
            assertCrossCardPlayed(cardID = 4)
            nextPlayer()
            // BLUE
            assertCurrentPlayer(blue)
            val pc2 = playCardByID(cardId = 2, recipient = red)
            assertCardPlayed(pc2)
            nextPlayer()
            // GREEN
            assertCurrentPlayer(green)
            val pc3 = playCardByID(cardId = 3, recipient = null)
            assertCardPlayed(pc3)
            assertCrossCardPlayed(cardID = 5)
            nextPlayer()
            // END Playing Cards -> Evaluate
            assertGameState(GameState.ROUND_EVALUATE_EFFECTS)
            println(historyPerRound[round])
            // End of ROUND 1
            // Start of ROUND 2
            nextRound()
            assertGameState(GameState.ROUND_PLAY_CARDS)
            // RED
            assertCurrentPlayer(red)
            val pc6 = playCardByID(cardId = 6, recipient = null)
            assertCardPlayed(pc6)
            nextPlayer()
            // BLUE
            assertCurrentPlayer(blue)
            val pc7 = playCardByID(cardId = 7, recipient = null)
            assertCardPlayed(pc7)
            nextPlayer()
            // GREEN
            assertCurrentPlayer(green)
            val pc8 = playCardByID(cardId = 8, recipient = null)
            assertCardPlayed(pc8)
            nextPlayer()
            // END Playing Cards -> Evaluate
            assertGameState(GameState.ROUND_EVALUATE_EFFECTS)
            println(historyPerRound[round])
        }
    }





    private fun Game.assertGameState(expected: GameState) {
        assertEquals(expected, gameState)
    }

    private fun Game.assertCurrentPlayer(expected: Player) {
        assertEquals(expected, activePlayers[playerIdx])
    }

    private fun Game.assertCardPlayed(card: PlayedCard, expectedSender: Player = activePlayers[playerIdx]) {
        assertNotNull(played[round])
        assertTrue(played[round]!!.contains(card))
        val effectRound = round + max(0, card.data.effectDelay)
        assertTrue(scheduled[effectRound]!!.contains(card))
        assertEquals(expectedSender, card.sender)
    }

    private fun Game.assertCrossCardPlayed(cardID: Int, expectedSender: Player = activePlayers[playerIdx], expectedReceiver: Player? = null) {
        val card = crossEffectCards.find { cardData -> cardData.id == cardID }!!
        assertNotNull(played[round])
        assertTrue(played[round]!!.any { playedCard -> playedCard.data == card })
        val effectRound = round + max(0, card.effectDelay)
        assertTrue(scheduled[effectRound]!!.any { playedCard -> playedCard.data == card })
        assertEquals(expectedSender, played[round]!!.find { playedCard -> playedCard.data == card }!!.sender)
        assertEquals(expectedReceiver, played[round]!!.find { playedCard -> playedCard.data == card }!!.recipient)
    }


}