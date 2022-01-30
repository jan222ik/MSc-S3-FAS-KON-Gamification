package com.github.jan222ik.gamification

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jan222ik.gamification.data.CardData
import com.github.jan222ik.gamification.importer.CSVImporter
import com.github.jan222ik.gamification.ui.logic.*
import com.github.jan222ik.gamification.ui.theme.GamificationTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.lang.Integer.max
import java.lang.Integer.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamificationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Game()
                }
            }
        }
    }

    override fun onBackPressed() {}
}

@Composable
fun Game() {
    val game = remember() {
        mutableStateOf<Game?>(null)
    }
    when (val g = game.value) {
        null -> NewGameSettings(onNextGame = { game.value = it })
        else -> RunningGame(g, nextGame = { game.value = null })
    }
}

@Composable
fun NewGameSettings(onNextGame: (Game) -> Unit) {
    Box(
        modifier = Modifier
            .wrapContentHeight(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            SelectPlayerCount(onPlayerCountSelected = {
                onNextGame.invoke(Game(it, CSVImporter.loadCards()))
            })
        }
    }
}

@Composable
fun SelectPlayerCount(
    onPlayerCountSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Neues Spiel",
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.headlineMedium
        )
        var playerCount by rememberSaveable { mutableStateOf(3) }
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Wähle die Anzahl der Spieler!")
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                3.rangeTo(6).forEach {
                    Button(
                        enabled = it != playerCount,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            playerCount = it
                        }
                    ) {
                        Text(it.toString())
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = {
                onPlayerCountSelected.invoke(playerCount)
            },
            text = {
                Text(text = "Spiel mit $playerCount Spielern starten")
            }
        )
    }
}

@Composable
fun RunningGame(game: Game, nextGame: () -> Unit) {
    Column {
        when (game.gameState) {
            GameState.ROUND_PLAY_CARDS -> PlayCardScreen(game)
            GameState.ROUND_EVALUATE_EFFECTS -> eval(game = game)
            GameState.HISTORY -> GameHistory(game = game, nextGame)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ColumnScope.GameHistory(game: Game, nextGame: () -> Unit) {
    TextButton(
        modifier = Modifier.align(Alignment.End),
        onClick = nextGame
    ) {
        Text(text = "Nächstes Spiel")
    }
    val pagerState = rememberPagerState()
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        val scope = rememberCoroutineScope()

        Row(Modifier.width(50.dp)) {
            AnimatedVisibility(visible = pagerState.currentPage > 0) {
                IconButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            max(
                                0,
                                pagerState.currentPage.dec()
                            )
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Filled.RemoveCircleOutline, contentDescription = null)
                }
            }
        }
        Text(
            text = "Runde: ${pagerState.currentPage.inc()} / ${pagerState.pageCount}"
        )
        Row(Modifier.width(50.dp)) {
            AnimatedVisibility(visible = pagerState.currentPage < pagerState.pageCount.dec()) {
                IconButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            min(
                                pagerState.pageCount.dec(),
                                pagerState.currentPage.inc()
                            )
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Filled.AddCircleOutline, contentDescription = null)
                }
            }
        }
    }
    VerticalPager(count = game.historyPerRound.size, state = pagerState) { page ->
        val hist = game.historyPerRound[page]
        Column() {
            SingeRoundEval(roundHistory = hist!!, nextRound = null)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ColumnScope.eval(game: Game) {
    Row(
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { game.endGame() }
        ) {
            Text(text = "Spielende")
        }
    }
    game.historyPerRound[game.round]?.let {
        SingeRoundEval(roundHistory = it, nextRound = { game.nextRound() })
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ColumnScope.SingeRoundEval(roundHistory: RoundHistory, nextRound: (() -> Unit)?) {
    val count = roundHistory.effects.size + 1
    val pagerState = rememberPagerState()
    HorizontalPager(
        count = count,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        state = pagerState
    ) { page ->
        Box(modifier = Modifier.fillMaxHeight(0.9f)) {
            if (page in 0..roundHistory.effects.size.dec()) {
                val eff = roundHistory.effects[page]
                PCard(playedCard = eff)
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 16.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterVertically
                        )
                    ) {
                        roundHistory.effectsPerPlayer.entries.forEach {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    PlayerNameChip(player = it.key)
                                }
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    val indication = when {
                                        it.value > 0 -> "+"
                                        it.value == 0 -> " "
                                        else -> ""
                                    }
                                    Text(
                                        text = "$indication${it.value} Felder",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }
                        }
                        if (nextRound != null) {
                            Button(
                                onClick = nextRound
                            ) {
                                Text(text = "Nächste Runde")
                            }
                        }
                    }
                }
            }
        }
    }
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        val scope = rememberCoroutineScope()

        Row(Modifier.width(50.dp)) {
            AnimatedVisibility(visible = pagerState.currentPage > 0) {
                IconButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            max(
                                0,
                                pagerState.currentPage.dec()
                            )
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = null)
                }
            }
        }
        Text(
            text = "Karte: ${pagerState.currentPage.inc()} / ${pagerState.pageCount}"
        )
        Row(Modifier.width(50.dp)) {
            AnimatedVisibility(visible = pagerState.currentPage < pagerState.pageCount.dec()) {
                IconButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            min(
                                pagerState.pageCount.dec(),
                                pagerState.currentPage.inc()
                            )
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}

@Preview(
    device = "id:pixel_5", showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun t() {
    GamificationTheme() {
        //PlayCardScreen(game = Game(3, CSVImporter.loadCards()))
        val game = Game(6, CSVImporter.loadCards())
        Column() {
            game.activePlayers.forEach {
                PlayerNameChip(player = it)
            }
        }
    }
}

@Composable
fun PlayCardScreen(game: Game) {
    val player = remember(game.playerIdx) {
        game.activePlayers[game.playerIdx]
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally)
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
                        if (cardId != null) {
                            selectedCard = game.getCardForID(cardId)
                        } else {
                            selectedCard = null
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
