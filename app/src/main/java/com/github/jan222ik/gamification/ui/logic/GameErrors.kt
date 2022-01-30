package com.github.jan222ik.gamification.ui.logic

sealed class GameErrors {
    sealed class PlayedCard {
        class HasSameReceiverSender(cardId: Int) :
            IllegalArgumentException("Card[id=$cardId] may not have the same sender and receiver")

        class AcceptsNoReceiver(cardId: Int) :
            IllegalArgumentException("Card[id=$cardId] accepts no receiver")

        class RequiresAReceiver(cardId: Int) :
            IllegalArgumentException("Card[id=$cardId] requires a receiver")
    }

    class NoSuchCard(
        cardId: Int
    ) : NoSuchElementException("Card[id=$cardId] does not exist")

    class WrongRoundState(
        expected: GameState,
        actual: GameState
    ) : IllegalArgumentException("Invalid GameState: actual=$actual expected=$expected")
}