package com.example.game

enum class CellMark {
    EMPTY, X, O;

    val opponent: CellMark
        get() = when (this) {
            X -> O
            O -> X
            EMPTY -> EMPTY
        }
}

enum class GameMode(val label: String) {
    VS_AI("vs AI"),
    PASS_AND_PLAY("2 Players")
}

enum class Difficulty(val label: String, val badgeColorHex: Long) {
    EASY("Easy", 0xFF10B981),          // Green
    MEDIUM("Medium", 0xFFF59E0B),      // Amber
    UNBEATABLE("Master", 0xFFEF4444)   // Red
}

enum class BoardDimension(val size: Int, val winLength: Int, val displayName: String) {
    SIZE_3X3(3, 3, "3 × 3"),
    SIZE_4X4(4, 4, "4 × 4"),
    SIZE_5X5(5, 4, "5 × 5");

    val totalCells: Int get() = size * size
}

sealed class GameResult {
    object InProgress : GameResult()
    data class Win(val winner: CellMark, val winningIndices: List<Int>) : GameResult()
    object Draw : GameResult()
}

data class Move(
    val index: Int,
    val player: CellMark
)

data class SessionScore(
    val xWins: Int = 0,
    val oWins: Int = 0,
    val draws: Int = 0
)
