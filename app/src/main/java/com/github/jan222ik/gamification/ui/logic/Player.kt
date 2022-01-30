package com.github.jan222ik.gamification.ui.logic

import androidx.compose.ui.graphics.Color

data class Player(val name: String, val color: Color, val textColor: Color) {
    override fun toString(): String {
        return "Player(name=$name)"
    }
}