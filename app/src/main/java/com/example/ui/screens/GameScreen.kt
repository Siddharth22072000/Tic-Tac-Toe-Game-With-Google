package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.CellMark
import com.example.game.GameResult
import com.example.ui.TicTacToeViewModel
import com.example.ui.components.BoardView
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.GameControls
import com.example.ui.components.ScoreBoard
import com.example.ui.theme.LocalAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: TicTacToeViewModel,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val history by viewModel.historyRecords.collectAsStateWithLifecycle()
    val totalGames by viewModel.dbTotalGames.collectAsStateWithLifecycle()
    val xWins by viewModel.dbXWins.collectAsStateWithLifecycle()
    val oWins by viewModel.dbOWins.collectAsStateWithLifecycle()
    val draws by viewModel.dbDraws.collectAsStateWithLifecycle()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            theme.background,
            theme.surface.copy(alpha = 0.95f),
            theme.background
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.testTag("app_title_row")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(theme.xColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = theme.xColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TIC TAC TOE",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = theme.onBackground
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(theme.oColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = theme.oColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        // Sound Toggle
                        IconButton(
                            onClick = { viewModel.toggleSound() },
                            modifier = Modifier.testTag("sound_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (uiState.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Toggle Sound",
                                tint = if (uiState.soundEnabled) theme.primary else theme.onBackground.copy(alpha = 0.4f)
                            )
                        }

                        // Stats Sheet
                        IconButton(
                            onClick = { viewModel.openStats(true) },
                            modifier = Modifier.testTag("stats_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Statistics",
                                tint = theme.onBackground
                            )
                        }

                        // Settings Sheet
                        IconButton(
                            onClick = { viewModel.openSettings(true) },
                            modifier = Modifier.testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = theme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    )
                )
            }
        ) { innerPadding ->
            val navBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = navBottomPadding + 16.dp)
                        .widthIn(max = 520.dp)
                        .align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Scoreboard
                    ScoreBoard(
                        currentPlayer = uiState.currentPlayer,
                        sessionScore = uiState.sessionScore,
                        playerXName = uiState.playerXName,
                        playerOName = uiState.playerOName,
                        isAiThinking = uiState.isAiThinking
                    )

                    // Game Board
                    BoardView(
                        board = uiState.board,
                        dimension = uiState.dimension,
                        gameResult = uiState.gameResult,
                        hintIndex = uiState.hintIndex,
                        onCellClick = { viewModel.onCellClicked(it) }
                    )

                    // Controls & Modes
                    GameControls(
                        gameMode = uiState.gameMode,
                        difficulty = uiState.difficulty,
                        dimension = uiState.dimension,
                        gameResult = uiState.gameResult,
                        canUndo = uiState.moveHistory.isNotEmpty() && !uiState.isAiThinking,
                        onModeChange = { viewModel.onModeChanged(it) },
                        onDifficultyChange = { viewModel.onDifficultyChanged(it) },
                        onDimensionChange = { viewModel.onDimensionChanged(it) },
                        onUndoClick = { viewModel.onUndoRequested() },
                        onHintClick = { viewModel.onHintRequested() },
                        onResetClick = { viewModel.onResetGame() },
                        playerXName = uiState.playerXName,
                        playerOName = uiState.playerOName
                    )
                }

                // Victory Confetti
                ConfettiEffect(
                    trigger = uiState.gameResult is GameResult.Win
                )
            }
        }

        // Stats & Match History Dialog
        StatsAndHistoryDialog(
            isOpen = uiState.isStatsSheetOpen,
            onDismiss = { viewModel.openStats(false) },
            totalGames = totalGames,
            xWins = xWins,
            oWins = oWins,
            draws = draws,
            history = history,
            onDeleteRecord = { viewModel.deleteHistoryRecord(it) },
            onClearAll = { viewModel.clearAllHistory() }
        )

        // Settings Dialog
        SettingsDialog(
            isOpen = uiState.isSettingsOpen,
            onDismiss = { viewModel.openSettings(false) },
            playerXName = uiState.playerXName,
            playerOName = uiState.playerOName,
            onUpdatePlayerNames = { x, o -> viewModel.setPlayerNames(x, o) },
            currentTheme = uiState.currentTheme,
            onSelectTheme = { viewModel.setTheme(it) },
            soundEnabled = uiState.soundEnabled,
            onToggleSound = { viewModel.toggleSound() },
            hapticsEnabled = uiState.hapticsEnabled,
            onToggleHaptics = { viewModel.toggleHaptics() }
        )
    }
}
