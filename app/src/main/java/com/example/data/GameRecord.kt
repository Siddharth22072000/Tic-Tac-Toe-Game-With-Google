package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mode: String,          // "VS_AI" or "PASS_AND_PLAY"
    val difficulty: String,    // "EASY", "MEDIUM", "UNBEATABLE", "NONE"
    val boardSize: Int,        // 3, 4, 5
    val winner: String,        // "X", "O", "DRAW"
    val playerXName: String,
    val playerOName: String,
    val moveCount: Int,
    val durationSeconds: Long,
    val timestamp: Long = System.currentTimeMillis()
)
