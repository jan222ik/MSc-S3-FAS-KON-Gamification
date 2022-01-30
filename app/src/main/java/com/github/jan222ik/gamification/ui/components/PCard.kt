package com.github.jan222ik.gamification.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.jan222ik.gamification.ui.logic.PlayedCard

@Composable
fun PCard(playedCard: PlayedCard) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
            .padding(end = 16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = when (playedCard.isCrossEffect) {
                        true -> "Nebeneffekt von: "
                        else -> "Gespielt von: "
                    }
                )
                PlayerNameChip(player = playedCard.sender)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                playedCard.recipient?.let {
                    Text(text = " an: ")
                    PlayerNameChip(player = it)
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 16.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 32.dp
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = playedCard.data.description)
                }
            }
        }
    }
}