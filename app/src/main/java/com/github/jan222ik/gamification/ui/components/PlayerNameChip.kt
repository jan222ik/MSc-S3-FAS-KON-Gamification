package com.github.jan222ik.gamification.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.jan222ik.gamification.ui.logic.Player

@Composable
fun PlayerNameChip(modifier: Modifier = Modifier, player: Player) {
    Surface(
        modifier = modifier,
        contentColor = player.textColor,
        color = player.color,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null)
            Text(text = player.name)
        }
    }
}