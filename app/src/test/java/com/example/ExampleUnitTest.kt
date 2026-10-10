package com.example

import com.example.game.BoardDimension
import com.example.game.CellMark
import com.example.game.Difficulty
import com.example.game.GameResult
import com.example.game.TicTacToeEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testInitialBoardInProgress() {
    val board = List(9) { CellMark.EMPTY }
    val result = TicTacToeEngine.checkGameResult(board, BoardDimension.SIZE_3X3)
    assertEquals(GameResult.InProgress, result)
  }

  @Test
  fun testHorizontalWin3x3() {
    val board = listOf(
      CellMark.X, CellMark.X, CellMark.X,
      CellMark.O, CellMark.O, CellMark.EMPTY,
      CellMark.EMPTY, CellMark.EMPTY, CellMark.EMPTY
    )
    val result = TicTacToeEngine.checkGameResult(board, BoardDimension.SIZE_3X3)
    assertTrue(result is GameResult.Win)
    val win = result as GameResult.Win
    assertEquals(CellMark.X, win.winner)
    assertEquals(listOf(0, 1, 2), win.winningIndices)
  }

  @Test
  fun testVerticalWin3x3() {
    val board = listOf(
      CellMark.O, CellMark.X, CellMark.EMPTY,
      CellMark.O, CellMark.X, CellMark.EMPTY,
      CellMark.O, CellMark.EMPTY, CellMark.EMPTY
    )
    val result = TicTacToeEngine.checkGameResult(board, BoardDimension.SIZE_3X3)
    assertTrue(result is GameResult.Win)
    val win = result as GameResult.Win
    assertEquals(CellMark.O, win.winner)
    assertEquals(listOf(0, 3, 6), win.winningIndices)
  }

  @Test
  fun testDiagonalWin3x3() {
    val board = listOf(
      CellMark.X, CellMark.O, CellMark.EMPTY,
      CellMark.O, CellMark.X, CellMark.EMPTY,
      CellMark.EMPTY, CellMark.EMPTY, CellMark.X
    )
    val result = TicTacToeEngine.checkGameResult(board, BoardDimension.SIZE_3X3)
    assertTrue(result is GameResult.Win)
    val win = result as GameResult.Win
    assertEquals(CellMark.X, win.winner)
    assertEquals(listOf(0, 4, 8), win.winningIndices)
  }

  @Test
  fun testDraw3x3() {
    val board = listOf(
      CellMark.X, CellMark.O, CellMark.X,
      CellMark.X, CellMark.O, CellMark.O,
      CellMark.O, CellMark.X, CellMark.X
    )
    val result = TicTacToeEngine.checkGameResult(board, BoardDimension.SIZE_3X3)
    assertEquals(GameResult.Draw, result)
  }

  @Test
  fun testUnbeatableAiBlocksWin() {
    // Human is about to win horizontally at index 2
    val board = listOf(
      CellMark.X, CellMark.X, CellMark.EMPTY,
      CellMark.O, CellMark.EMPTY, CellMark.EMPTY,
      CellMark.EMPTY, CellMark.EMPTY, CellMark.EMPTY
    )
    val aiMove = TicTacToeEngine.getBestMove(
      board,
      BoardDimension.SIZE_3X3,
      CellMark.O,
      Difficulty.UNBEATABLE
    )
    assertEquals(2, aiMove)
  }

  @Test
  fun testHintReturnsValidMove() {
    val board = listOf(
      CellMark.X, CellMark.EMPTY, CellMark.EMPTY,
      CellMark.EMPTY, CellMark.O, CellMark.EMPTY,
      CellMark.EMPTY, CellMark.EMPTY, CellMark.EMPTY
    )
    val hint = TicTacToeEngine.getHint(board, BoardDimension.SIZE_3X3, CellMark.X)
    assertNotNull(hint)
    assertTrue(hint in 0..8)
    assertEquals(CellMark.EMPTY, board[hint!!])
  }
}
