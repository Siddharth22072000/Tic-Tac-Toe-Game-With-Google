package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecordDao {
    @Query("SELECT * FROM game_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<GameRecord>>

    @Query("SELECT * FROM game_records ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<GameRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GameRecord): Long

    @Query("DELETE FROM game_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)

    @Query("DELETE FROM game_records")
    suspend fun clearAllRecords()

    @Query("SELECT COUNT(*) FROM game_records")
    fun getTotalGamesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM game_records WHERE winner = 'X'")
    fun getXWinsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM game_records WHERE winner = 'O'")
    fun getOWinsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM game_records WHERE winner = 'DRAW'")
    fun getDrawsCount(): Flow<Int>
}
