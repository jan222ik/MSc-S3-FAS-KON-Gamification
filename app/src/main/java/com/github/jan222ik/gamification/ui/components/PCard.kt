package com.github.jan222ik.gamification.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    .weight(1f)
                    .padding(all = 16.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 32.dp
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = buildAnnotatedString {
                        val senderTag = "<sender>"
                        val startIdx = playedCard.data.description.indexOf(senderTag)
                        if (startIdx != -1) {
                            append(playedCard.data.description.substring(startIndex = 0, endIndex = startIdx))
                            withStyle(SpanStyle(color = playedCard.sender.color)) {
                                append(playedCard.sender.name)
                            }
                            append(playedCard.data.description.substring(startIndex = startIdx + senderTag.length))
                        } else {
                            append(playedCard.data.description)
                        }
                    })
                    Text(
                        text = "ID:${playedCard.data.id}",
                        style = LocalTextStyle.current.copy(fontSize = 10.sp),
                        modifier= Modifier
                            .align(Alignment.BottomEnd)
                            .padding(5.dp)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val effectSender = playedCard.data.effectForSender
                val indication = when {
                    effectSender > 0 -> "+"
                    effectSender == 0 -> " "
                    else -> ""
                }
                Text(text = "Sender: $indication$effectSender")
                playedCard.recipient?.let {
                    val effectRecipient = playedCard.data.effectForReceiver
                    val indicationR = when {
                        effectRecipient > 0 -> "+"
                        effectRecipient == 0 -> " "
                        else -> ""
                    }
                    Text(text = "Empfänger: $indicationR$effectRecipient")
                }
            }
        }
    }
}