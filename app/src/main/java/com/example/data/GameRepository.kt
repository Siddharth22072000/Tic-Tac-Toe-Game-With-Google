package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: GameRecordDao) {
    val allRecords: Flow<List<GameRecord>> = dao.getAllRecords()
    val recentRecords: Flow<List<GameRecord>> = dao.getRecentRecords(50)

    val totalGames: Flow<Int> = dao.getTotalGamesCount()
    val xWins: Flow<Int> = dao.getXWinsCount()
    val oWins: Flow<Int> = dao.getOWinsCount()
    val draws: Flow<Int> = dao.getDrawsCount()

    suspend fun saveGame(record: GameRecord): Long {
        return dao.insertRecord(record)
    }

    suspend fun deleteGame(id: Long) {
        dao.deleteRecord(id)
    }

    suspend fun clearHistory() {
        dao.clearAllRecords()
    }
}
