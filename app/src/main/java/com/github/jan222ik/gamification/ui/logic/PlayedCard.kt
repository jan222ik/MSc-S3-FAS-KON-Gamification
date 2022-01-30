package com.github.jan222ik.gamification.ui.logic

import com.github.jan222ik.gamification.data.CardData

data class PlayedCard(val data: CardData, val sender: Player, val recipient: Player?, val isCrossEffect: Boolean)