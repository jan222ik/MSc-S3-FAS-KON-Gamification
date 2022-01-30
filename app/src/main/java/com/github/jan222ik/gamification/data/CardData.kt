package com.github.jan222ik.gamification.data

data class CardData(
    val id: Int,
    val effectDelay: Int,
    val effectForReceiver: Int,
    val effectForSender: Int,
    val activateOnCards: List<Int>,
    val hasReceiver: Boolean,
    val description: String,
)