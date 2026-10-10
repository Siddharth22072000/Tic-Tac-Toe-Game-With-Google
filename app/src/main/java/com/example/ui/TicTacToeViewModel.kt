package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GameRecord
import com.example.data.GameRepository
import com.example.game.BoardDimension
import com.example.game.CellMark
import com.example.game.Difficulty
import com.example.game.GameMode
import com.example.game.GameResult
import com.example.game.Move
import com.example.game.SessionScore
import com.example.game.TicTacToeEngine
import com.example.ui.theme.AppTheme
import com.example.util.SoundAndHapticManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicTacToeUiState(
    val board: List<CellMark> = List(BoardDimension.SIZE_3X3.totalCells) { CellMark.EMPTY },
    val dimension: BoardDimension = BoardDimension.SIZE_3X3,
    val gameMode: GameMode = GameMode.VS_AI,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val currentPlayer: CellMark = CellMark.X,
    val gameResult: GameResult = GameResult.InProgress,
    val moveHistory: List<Move> = emptyList(),
    val isAiThinking: Boolean = false,
    val hintIndex: Int? = null,
    val sessionScore: SessionScore = SessionScore(),
    val playerXName: String = "You",
    val playerOName: String = "AI Bot",
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val currentTheme: AppTheme = AppTheme.CYBER_NEON,
    val gameStartTime: Long = System.currentTimeMillis(),
    val isStatsSheetOpen: Boolean = false,
    val isSettingsOpen: Boolean = false
)

class TicTacToeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = GameRepository(database.gameRecordDao())
    val soundHaptic = SoundAndHapticManager(application)

    private val _uiState = MutableStateFlow(TicTacToeUiState())
    val uiState: StateFlow<TicTacToeUiState> = _uiState.asStateFlow()

    val historyRecords: StateFlow<List<GameRecord>> = repository.recentRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dbTotalGames: StateFlow<Int> = repository.totalGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val dbXWins: StateFlow<Int> = repository.xWins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val dbOWins: StateFlow<Int> = repository.oWins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val dbDraws: StateFlow<Int> = repository.draws
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private var aiJob: Job? = null

    fun onCellClicked(index: Int) {
        val state = _uiState.value
        if (state.gameResult !is GameResult.InProgress) return
        if (state.isAiThinking) return
        if (index !in state.board.indices || state.board[index] != CellMark.EMPTY) return

        // Place mark
        val player = state.currentPlayer
        val newBoard = state.board.toMutableList()
        newBoard[index] = player

        val newHistory = state.moveHistory + Move(index, player)

        soundHaptic.playMoveSound(state.soundEnabled)
        soundHaptic.vibrateMove(state.hapticsEnabled)

        val result = TicTacToeEngine.checkGameResult(newBoard, state.dimension)

        if (result !is GameResult.InProgress) {
            handleGameEnd(newBoard, newHistory, result)
        } else {
            val nextPlayer = player.opponent
            _uiState.update {
                it.copy(
                    board = newBoard,
                    currentPlayer = nextPlayer,
                    moveHistory = newHistory,
                    hintIndex = null
                )
            }

            // If VS AI and next player is O, trigger AI
            if (state.gameMode == GameMode.VS_AI && nextPlayer == CellMark.O) {
                triggerAiMove(newBoard)
            }
        }
    }

    private fun triggerAiMove(currentBoard: List<CellMark>) {
        aiJob?.cancel()
        _uiState.update { it.copy(isAiThinking = true) }

        aiJob = viewModelScope.launch {
            // Humanized delay
            delay(350)

            val currentState = _uiState.value
            if (currentState.gameResult !is GameResult.InProgress) {
                _uiState.update { it.copy(isAiThinking = false) }
                return@launch
            }

            val aiMoveIndex = TicTacToeEngine.getBestMove(
                currentBoard,
                currentState.dimension,
                CellMark.O,
                currentState.difficulty
            )

            if (aiMoveIndex in currentBoard.indices && currentBoard[aiMoveIndex] == CellMark.EMPTY) {
                val newBoard = currentBoard.toMutableList()
                newBoard[aiMoveIndex] = CellMark.O
                val newHistory = currentState.moveHistory + Move(aiMoveIndex, CellMark.O)

                soundHaptic.playMoveSound(currentState.soundEnabled)
                soundHaptic.vibrateMove(currentState.hapticsEnabled)

                val result = TicTacToeEngine.checkGameResult(newBoard, currentState.dimension)
                _uiState.update { it.copy(isAiThinking = false) }

                if (result !is GameResult.InProgress) {
                    handleGameEnd(newBoard, newHistory, result)
                } else {
                    _uiState.update {
                        it.copy(
                            board = newBoard,
                            currentPlayer = CellMark.X,
                            moveHistory = newHistory,
                            hintIndex = null
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isAiThinking = false) }
            }
        }
    }

    private fun handleGameEnd(
        finalBoard: List<CellMark>,
        history: List<Move>,
        result: GameResult
    ) {
        val state = _uiState.value
        val duration = (System.currentTimeMillis() - state.gameStartTime) / 1000

        var newSessionScore = state.sessionScore
        var winnerStr = "DRAW"

        when (result) {
            is GameResult.Win -> {
                soundHaptic.playWinSound(state.soundEnabled)
                soundHaptic.vibrateWin(state.hapticsEnabled)
                if (result.winner == CellMark.X) {
                    newSessionScore = newSessionScore.copy(xWins = newSessionScore.xWins + 1)
                    winnerStr = "X"
                } else {
                    newSessionScore = newSessionScore.copy(oWins = newSessionScore.oWins + 1)
                    winnerStr = "O"
                }
            }
            is GameResult.Draw -> {
                soundHaptic.playDrawSound(state.soundEnabled)
                soundHaptic.vibrateDraw(state.hapticsEnabled)
                newSessionScore = newSessionScore.copy(draws = newSessionScore.draws + 1)
                winnerStr = "DRAW"
            }
            GameResult.InProgress -> {}
        }

        _uiState.update {
            it.copy(
                board = finalBoard,
                moveHistory = history,
                gameResult = result,
                sessionScore = newSessionScore,
                isAiThinking = false,
                hintIndex = null
            )
        }

        // Save record to DB
        viewModelScope.launch {
            repository.saveGame(
                GameRecord(
                    mode = state.gameMode.name,
                    difficulty = if (state.gameMode == GameMode.VS_AI) state.difficulty.name else "NONE",
                    boardSize = state.dimension.size,
                    winner = winnerStr,
                    playerXName = state.playerXName,
                    playerOName = state.playerOName,
                    moveCount = history.size,
                    durationSeconds = duration
                )
            )
        }
    }

    fun onHintRequested() {
        val state = _uiState.value
        if (state.gameResult !is GameResult.InProgress || state.isAiThinking) return
        val hint = TicTacToeEngine.getHint(state.board, state.dimension, state.currentPlayer)
        if (hint != null) {
            soundHaptic.playClickSound(state.soundEnabled)
            _uiState.update { it.copy(hintIndex = hint) }
        }
    }

    fun onUndoRequested() {
        val state = _uiState.value
        if (state.isAiThinking) return
        if (state.moveHistory.isEmpty()) return

        soundHaptic.playClickSound(state.soundEnabled)

        val newBoard = MutableList(state.dimension.totalCells) { CellMark.EMPTY }
        val newHistory: List<Move>

        if (state.gameMode == GameMode.VS_AI) {
            // In VS AI, if it was X's turn, undo both AI and player moves (2 moves)
            // If game ended, undo 1 or 2 moves
            val countToRemove = if (state.gameResult !is GameResult.InProgress) {
                if (state.moveHistory.last().player == CellMark.O) 2 else 1
            } else 2

            newHistory = state.moveHistory.dropLast(countToRemove.coerceAtMost(state.moveHistory.size))
        } else {
            // In 2 players, undo last single move
            newHistory = state.moveHistory.dropLast(1)
        }

        for (m in newHistory) {
            newBoard[m.index] = m.player
        }

        val nextPlayer = if (newHistory.isEmpty()) {
            CellMark.X
        } else {
            newHistory.last().player.opponent
        }

        _uiState.update {
            it.copy(
                board = newBoard,
                moveHistory = newHistory,
                currentPlayer = nextPlayer,
                gameResult = GameResult.InProgress,
                hintIndex = null
            )
        }
    }

    fun onResetGame() {
        aiJob?.cancel()
        soundHaptic.playClickSound(_uiState.value.soundEnabled)
        _uiState.update {
            it.copy(
                board = List(it.dimension.totalCells) { CellMark.EMPTY },
                currentPlayer = CellMark.X,
                gameResult = GameResult.InProgress,
                moveHistory = emptyList(),
                isAiThinking = false,
                hintIndex = null,
                gameStartTime = System.currentTimeMillis()
            )
        }
    }

    fun onDimensionChanged(dimension: BoardDimension) {
        if (_uiState.value.dimension == dimension) return
        aiJob?.cancel()
        soundHaptic.playClickSound(_uiState.value.soundEnabled)
        _uiState.update {
            it.copy(
                dimension = dimension,
                board = List(dimension.totalCells) { CellMark.EMPTY },
                currentPlayer = CellMark.X,
                gameResult = GameResult.InProgress,
                moveHistory = emptyList(),
                isAiThinking = false,
                hintIndex = null,
                sessionScore = SessionScore(),
                gameStartTime = System.currentTimeMillis()
            )
        }
    }

    fun onModeChanged(mode: GameMode) {
        if (_uiState.value.gameMode == mode) return
        aiJob?.cancel()
        soundHaptic.playClickSound(_uiState.value.soundEnabled)
        val defaultO = if (mode == GameMode.VS_AI) "AI Bot" else "Player 2"
        val defaultX = if (mode == GameMode.VS_AI) "You" else "Player 1"
        _uiState.update {
            it.copy(
                gameMode = mode,
                playerXName = defaultX,
                playerOName = defaultO,
                board = List(it.dimension.totalCells) { CellMark.EMPTY },
                currentPlayer = CellMark.X,
                gameResult = GameResult.InProgress,
                moveHistory = emptyList(),
                isAiThinking = false,
                hintIndex = null,
                sessionScore = SessionScore(),
                gameStartTime = System.currentTimeMillis()
            )
        }
    }

    fun onDifficultyChanged(difficulty: Difficulty) {
        if (_uiState.value.difficulty == difficulty) return
        soundHaptic.playClickSound(_uiState.value.soundEnabled)
        _uiState.update { it.copy(difficulty = difficulty) }
    }

    fun setPlayerNames(xName: String, oName: String) {
        _uiState.update {
            it.copy(
                playerXName = xName.trim().ifEmpty { if (it.gameMode == GameMode.VS_AI) "You" else "Player 1" },
                playerOName = oName.trim().ifEmpty { if (it.gameMode == GameMode.VS_AI) "AI Bot" else "Player 2" }
            )
        }
    }

    fun setTheme(theme: AppTheme) {
        soundHaptic.playClickSound(_uiState.value.soundEnabled)
        _uiState.update { it.copy(currentTheme = theme) }
    }

    fun toggleSound() {
        _uiState.update { it.copy(soundEnabled = !it.soundEnabled) }
    }

    fun toggleHaptics() {
        val next = !_uiState.value.hapticsEnabled
        _uiState.update { it.copy(hapticsEnabled = next) }
        if (next) soundHaptic.vibrateMove(true)
    }

    fun openStats(open: Boolean) {
        _uiState.update { it.copy(isStatsSheetOpen = open) }
    }

    fun openSettings(open: Boolean) {
        _uiState.update { it.copy(isSettingsOpen = open) }
    }

    fun deleteHistoryRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteGame(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundHaptic.release()
    }
}
