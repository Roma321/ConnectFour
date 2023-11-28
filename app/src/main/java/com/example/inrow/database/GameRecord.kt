package com.example.inrow.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.inrow.GameMode

@Entity(tableName = "Games")
data class GameRecord(
    @PrimaryKey(autoGenerate = true) val id: Long,
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
    val movesCount: Int,
) {
    fun totalLength() = timeSpent1 + timeSpent2
    override fun toString(): String {
        return "$date\n" +
                "Играли $player1 и $player2\n" +
                "${resultAsText()}\n" +
                "В качестве второго игрока выступал ${modeAsText()}\n" +
                "Играли с контролем времени $controlMinutes+$controlAddition/сек.\n" +
                "Первый игрок потратил ${timeSpent1 / 60}мин ${timeSpent1 % 60}сек\n" +
                "Второй игрок потратил ${timeSpent2 / 60}мин ${timeSpent2 % 60}сек\n" +
                "Всего игра длилась ${totalLength() / 60}мин ${totalLength() % 60}сек и $movesCount ходов\n" +
                "Игровое поле: $width*$height\n" +
                "Запись игры:\n" +
                "$gameString"
    }

    fun resultAsText(): String {
        return when (result) {
            0 -> "Ничья"
            1 -> "Первый игрок выиграл"
            2 -> "Второй игрок выиграл"
            3 -> "Первый игрок выиграл по времени"
            4 -> "Второй игрок выиграл по времени"
            else -> "ИГРА НЕ БЫЛА ДОИГРАНА"
        }
    }

    fun modeAsText(): String {
        return when (mode) {
            GameMode.TWO_PLAYERS -> "Человек"
            GameMode.RANDOM_BOT -> "Бот-рандомайзер"
            GameMode.SMART_BOT -> "Умный бот"
            GameMode.NASH_BOT -> "Бот Нэша"
        }
    }
}
