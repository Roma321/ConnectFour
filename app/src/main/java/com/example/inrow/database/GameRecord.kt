package com.example.inrow.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.inrow.GameMode

@Entity(tableName = "Games")
data class GameRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val result: Int, // 0-ничья, 1-победа 1, 2-победа 2, 3-победа 1 по времени, 4-победа 2 по времени
    val player1: String,
    val player2: String,
    val mode: GameMode,
    val controlMinutes: Int,
    val controlAddition: Int,
    val timeSpent1: Int, //в секундах
    val timeSpent2: Int,
    val date: String,
    val gameString: String,
    val width: Int,
    val height: Int,
    val movesCount:Int,
)
