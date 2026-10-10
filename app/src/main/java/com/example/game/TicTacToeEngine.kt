package com.example.game

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object TicTacToeEngine {

    /**
     * Checks if the board has a winner or draw based on the given dimensions.
     */
    fun checkGameResult(board: List<CellMark>, dimension: BoardDimension): GameResult {
        val size = dimension.size
        val winLen = dimension.winLength

        // 1. Horizontal check
        for (r in 0 until size) {
            for (c in 0..size - winLen) {
                val first = board[r * size + c]
                if (first != CellMark.EMPTY) {
                    var matches = true
                    val indices = ArrayList<Int>(winLen)
                    indices.add(r * size + c)
                    for (k in 1 until winLen) {
                        val idx = r * size + (c + k)
                        if (board[idx] != first) {
                            matches = false
                            break
                        }
                        indices.add(idx)
                    }
                    if (matches) return GameResult.Win(first, indices)
                }
            }
        }

        // 2. Vertical check
        for (c in 0 until size) {
            for (r in 0..size - winLen) {
                val first = board[r * size + c]
                if (first != CellMark.EMPTY) {
                    var matches = true
                    val indices = ArrayList<Int>(winLen)
                    indices.add(r * size + c)
                    for (k in 1 until winLen) {
                        val idx = (r + k) * size + c
                        if (board[idx] != first) {
                            matches = false
                            break
                        }
                        indices.add(idx)
                    }
                    if (matches) return GameResult.Win(first, indices)
                }
            }
        }

        // 3. Diagonal (Top-Left to Bottom-Right \)
        for (r in 0..size - winLen) {
            for (c in 0..size - winLen) {
                val first = board[r * size + c]
                if (first != CellMark.EMPTY) {
                    var matches = true
                    val indices = ArrayList<Int>(winLen)
                    indices.add(r * size + c)
                    for (k in 1 until winLen) {
                        val idx = (r + k) * size + (c + k)
                        if (board[idx] != first) {
                            matches = false
                            break
                        }
                        indices.add(idx)
                    }
                    if (matches) return GameResult.Win(first, indices)
                }
            }
        }

        // 4. Diagonal (Top-Right to Bottom-Left /)
        for (r in 0..size - winLen) {
            for (c in winLen - 1 until size) {
                val first = board[r * size + c]
                if (first != CellMark.EMPTY) {
                    var matches = true
                    val indices = ArrayList<Int>(winLen)
                    indices.add(r * size + c)
                    for (k in 1 until winLen) {
                        val idx = (r + k) * size + (c - k)
                        if (board[idx] != first) {
                            matches = false
                            break
                        }
                        indices.add(idx)
                    }
                    if (matches) return GameResult.Win(first, indices)
                }
            }
        }

        // 5. Check if board is full
        if (board.none { it == CellMark.EMPTY }) {
            return GameResult.Draw
        }

        return GameResult.InProgress
    }

    /**
     * Determines AI move based on chosen difficulty.
     */
    fun getBestMove(
        board: List<CellMark>,
        dimension: BoardDimension,
        aiMark: CellMark,
        difficulty: Difficulty
    ): Int {
        val emptyIndices = board.indices.filter { board[it] == CellMark.EMPTY }
        if (emptyIndices.isEmpty()) return -1
        if (emptyIndices.size == 1) return emptyIndices.first()

        return when (difficulty) {
            Difficulty.EASY -> getEasyMove(board, dimension, aiMark, emptyIndices)
            Difficulty.MEDIUM -> getMediumMove(board, dimension, aiMark, emptyIndices)
            Difficulty.UNBEATABLE -> getMasterMove(board, dimension, aiMark, emptyIndices)
        }
    }

    private fun getEasyMove(
        board: List<CellMark>,
        dimension: BoardDimension,
        aiMark: CellMark,
        emptyIndices: List<Int>
    ): Int {
        // 70% random, 30% smart move (take win if obvious)
        if (Random.nextFloat() > 0.3f) {
            return emptyIndices.random()
        }
        val winningMove = findImmediateWinningMove(board, dimension, aiMark, emptyIndices)
        if (winningMove != null) return winningMove
        return emptyIndices.random()
    }

    private fun getMediumMove(
        board: List<CellMark>,
        dimension: BoardDimension,
        aiMark: CellMark,
        emptyIndices: List<Int>
    ): Int {
        val humanMark = aiMark.opponent

        // 1. Check if AI can win immediately
        val winningMove = findImmediateWinningMove(board, dimension, aiMark, emptyIndices)
        if (winningMove != null) return winningMove

        // 2. Block human from winning immediately
        val blockMove = findImmediateWinningMove(board, dimension, humanMark, emptyIndices)
        if (blockMove != null) return blockMove

        // 3. Take center if available
        val centerIndex = (dimension.size / 2) * dimension.size + (dimension.size / 2)
        if (board[centerIndex] == CellMark.EMPTY && Random.nextFloat() < 0.8f) {
            return centerIndex
        }

        // 4. Take corners with some probability
        val corners = listOf(
            0,
            dimension.size - 1,
            dimension.size * (dimension.size - 1),
            dimension.totalCells - 1
        ).filter { board[it] == CellMark.EMPTY }
        if (corners.isNotEmpty() && Random.nextFloat() < 0.6f) {
            return corners.random()
        }

        return emptyIndices.random()
    }

    private fun getMasterMove(
        board: List<CellMark>,
        dimension: BoardDimension,
        aiMark: CellMark,
        emptyIndices: List<Int>
    ): Int {
        if (dimension == BoardDimension.SIZE_3X3) {
            // First move optimization for speed
            if (emptyIndices.size == 9) {
                // Pick corner or center
                val openings = listOf(0, 2, 4, 6, 8)
                return openings.random()
            }
            if (emptyIndices.size == 8 && board[4] == CellMark.EMPTY) {
                return 4 // Take center
            }

            var bestScore = Int.MIN_VALUE
            var bestMove = emptyIndices.first()

            val mutableBoard = board.toMutableList()
            for (idx in emptyIndices) {
                mutableBoard[idx] = aiMark
                val score = minimax3x3(mutableBoard, 0, false, aiMark, Int.MIN_VALUE, Int.MAX_VALUE)
                mutableBoard[idx] = CellMark.EMPTY
                if (score > bestScore) {
                    bestScore = score
                    bestMove = idx
                }
            }
            return bestMove
        } else {
            // For 4x4 and 5x5: Heuristic Alpha-Beta with depth limit
            return getHeuristicBestMove(board, dimension, aiMark, emptyIndices, maxDepth = 3)
        }
    }

    private fun minimax3x3(
        board: MutableList<CellMark>,
        depth: Int,
        isMaximizing: Boolean,
        aiMark: CellMark,
        alphaInit: Int,
        betaInit: Int
    ): Int {
        var alpha = alphaInit
        var beta = betaInit
        val humanMark = aiMark.opponent

        val result = checkGameResult(board, BoardDimension.SIZE_3X3)
        when (result) {
            is GameResult.Win -> {
                return if (result.winner == aiMark) 10 - depth else depth - 10
            }
            is GameResult.Draw -> return 0
            GameResult.InProgress -> {}
        }

        if (depth >= 6) return 0

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (i in board.indices) {
                if (board[i] == CellMark.EMPTY) {
                    board[i] = aiMark
                    val eval = minimax3x3(board, depth + 1, false, aiMark, alpha, beta)
                    board[i] = CellMark.EMPTY
                    maxEval = max(maxEval, eval)
                    alpha = max(alpha, eval)
                    if (beta <= alpha) break
                }
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (i in board.indices) {
                if (board[i] == CellMark.EMPTY) {
                    board[i] = humanMark
                    val eval = minimax3x3(board, depth + 1, true, aiMark, alpha, beta)
                    board[i] = CellMark.EMPTY
                    minEval = min(minEval, eval)
                    beta = min(beta, eval)
                    if (beta <= alpha) break
                }
            }
            return minEval
        }
    }

    private fun getHeuristicBestMove(
        board: List<CellMark>,
        dimension: BoardDimension,
        aiMark: CellMark,
        emptyIndices: List<Int>,
        maxDepth: Int
    ): Int {
        // Immediate win or block check first
        val humanMark = aiMark.opponent
        val winMove = findImmediateWinningMove(board, dimension, aiMark, emptyIndices)
        if (winMove != null) return winMove
        val blockMove = findImmediateWinningMove(board, dimension, humanMark, emptyIndices)
        if (blockMove != null) return blockMove

        // Sort moves by proximity to occupied cells
        val scoredMoves = emptyIndices.map { idx ->
            val proximity = getProximityScore(board, dimension, idx)
            idx to proximity
        }.sortedByDescending { it.second }.take(8)

        var bestScore = Int.MIN_VALUE
        var bestMove = emptyIndices.first()
        val mutableBoard = board.toMutableList()

        for ((idx, _) in scoredMoves) {
            mutableBoard[idx] = aiMark
            val score = alphaBetaEvaluation(mutableBoard, dimension, 1, false, aiMark, Int.MIN_VALUE, Int.MAX_VALUE, maxDepth)
            mutableBoard[idx] = CellMark.EMPTY
            if (score > bestScore) {
                bestScore = score
                bestMove = idx
            }
        }

        return bestMove
    }

    private fun alphaBetaEvaluation(
        board: MutableList<CellMark>,
        dimension: BoardDimension,
        depth: Int,
        isMaximizing: Boolean,
        aiMark: CellMark,
        alphaInit: Int,
        betaInit: Int,
        maxDepth: Int
    ): Int {
        var alpha = alphaInit
        var beta = betaInit
        val humanMark = aiMark.opponent

        when (val res = checkGameResult(board, dimension)) {
            is GameResult.Win -> {
                return if (res.winner == aiMark) 1000 - depth * 10 else -1000 + depth * 10
            }
            is GameResult.Draw -> return 0
            GameResult.InProgress -> {}
        }

        if (depth >= maxDepth) {
            return evaluateBoardHeuristic(board, dimension, aiMark)
        }

        val emptyIndices = board.indices.filter { board[it] == CellMark.EMPTY }
            .sortedByDescending { getProximityScore(board, dimension, it) }
            .take(6)

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (idx in emptyIndices) {
                board[idx] = aiMark
                val eval = alphaBetaEvaluation(board, dimension, depth + 1, false, aiMark, alpha, beta, maxDepth)
                board[idx] = CellMark.EMPTY
                maxEval = max(maxEval, eval)
                alpha = max(alpha, eval)
                if (beta <= alpha) break
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (idx in emptyIndices) {
                board[idx] = humanMark
                val eval = alphaBetaEvaluation(board, dimension, depth + 1, true, aiMark, alpha, beta, maxDepth)
                board[idx] = CellMark.EMPTY
                minEval = min(minEval, eval)
                beta = min(beta, eval)
                if (beta <= alpha) break
            }
            return minEval
        }
    }

    private fun getProximityScore(board: List<CellMark>, dimension: BoardDimension, idx: Int): Int {
        val size = dimension.size
        val r = idx / size
        val c = idx % size
        var score = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until size && nc in 0 until size) {
                    if (board[nr * size + nc] != CellMark.EMPTY) {
                        score += 2
                    }
                }
            }
        }
        // Center bias
        val center = (size - 1) / 2.0
        val distFromCenter = kotlin.math.abs(r - center) + kotlin.math.abs(c - center)
        score += max(0, (size - distFromCenter.toInt()))
        return score
    }

    private fun evaluateBoardHeuristic(board: List<CellMark>, dimension: BoardDimension, aiMark: CellMark): Int {
        // Quick line evaluation
        var score = 0
        val humanMark = aiMark.opponent
        val lines = getAllWinningLines(dimension)
        for (line in lines) {
            var aiCount = 0
            var humanCount = 0
            for (idx in line) {
                val mark = board[idx]
                if (mark == aiMark) {
                    aiCount++
                } else if (mark == humanMark) {
                    humanCount++
                }
            }
            if (aiCount > 0 && humanCount == 0) {
                score += when (aiCount) {
                    1 -> 1
                    2 -> 10
                    3 -> 80
                    else -> 500
                }
            } else if (humanCount > 0 && aiCount == 0) {
                score -= when (humanCount) {
                    1 -> 1
                    2 -> 12
                    3 -> 100
                    else -> 600
                }
            }
        }
        return score
    }

    private fun getAllWinningLines(dimension: BoardDimension): List<List<Int>> {
        val lines = mutableListOf<List<Int>>()
        val size = dimension.size
        val winLen = dimension.winLength

        // Horizontal
        for (r in 0 until size) {
            for (c in 0..size - winLen) {
                lines.add((0 until winLen).map { r * size + (c + it) })
            }
        }
        // Vertical
        for (c in 0 until size) {
            for (r in 0..size - winLen) {
                lines.add((0 until winLen).map { (r + it) * size + c })
            }
        }
        // Diagonal \
        for (r in 0..size - winLen) {
            for (c in 0..size - winLen) {
                lines.add((0 until winLen).map { (r + it) * size + (c + it) })
            }
        }
        // Diagonal /
        for (r in 0..size - winLen) {
            for (c in winLen - 1 until size) {
                lines.add((0 until winLen).map { (r + it) * size + (c - it) })
            }
        }
        return lines
    }

    private fun findImmediateWinningMove(
        board: List<CellMark>,
        dimension: BoardDimension,
        mark: CellMark,
        emptyIndices: List<Int>
    ): Int? {
        val mutable = board.toMutableList()
        for (idx in emptyIndices) {
            mutable[idx] = mark
            val result = checkGameResult(mutable, dimension)
            mutable[idx] = CellMark.EMPTY
            if (result is GameResult.Win && result.winner == mark) {
                return idx
            }
        }
        return null
    }

    /**
     * Calculates the best move for the current player to provide a hint.
     */
    fun getHint(board: List<CellMark>, dimension: BoardDimension, currentMark: CellMark): Int? {
        val emptyIndices = board.indices.filter { board[it] == CellMark.EMPTY }
        if (emptyIndices.isEmpty()) return null
        return getBestMove(board, dimension, currentMark, Difficulty.UNBEATABLE)
    }
}
