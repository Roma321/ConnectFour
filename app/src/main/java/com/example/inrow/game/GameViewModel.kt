package com.example.inrow.game

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import com.example.inrow.GameMode
import com.example.inrow.bot.Bot
import com.example.inrow.bot.NashEquilibriumBot
import com.example.inrow.bot.RandomBot
import com.example.inrow.bot.RulesBasedBot
import com.example.inrow.database.GameDatabaseDao
import com.example.inrow.database.GameRecord
import com.example.inrow.funcBot.checkWin
import com.example.inrow.funcBot.convertLiveDataArray
import kotlinx.coroutines.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class GameViewModel(
    val height: Int = 8,
    val width: Int = 8,
    private val mode: GameMode,
    val minutes: Int,
    private val add: Int,
    val dao: GameDatabaseDao,
    application: Application,
    val player1Name: String,
    val player2Name: String,
) : AndroidViewModel(application) {

    var field: Array<Array<MutableLiveData<Int>>> =
        Array(height) { Array(width) { MutableLiveData(0) } }

    private val tempField = List(height) { MutableList(width) { 0 } }

    private val bot: Bot? = when (mode) {
        GameMode.SMART_BOT -> RulesBasedBot(
            Array(height) { Array(width) { 0 } },
            width,
            height
        )

        GameMode.RANDOM_BOT -> RandomBot(
            Array(height) { Array(width) { 0 } },
            width,
            height
        )

        GameMode.NASH_BOT -> NashEquilibriumBot(
            Array(height) { Array(width) { 0 } },
            width,
            height
        )

        else -> null
    }
    private var timer1: CountDownTimer
    private var _timeLeftForPlayer1 = MutableLiveData(minutes * 60L)
    val timeLeftForPlayer1: LiveData<Long>
        get() = _timeLeftForPlayer1
    private var _timeLeftForPlayer2 = MutableLiveData(minutes * 60L)
    val timeLeftForPlayer2: LiveData<Long>
        get() = _timeLeftForPlayer2
    private var timer2: CountDownTimer

    private var _win = MutableLiveData(0)
    private var _movesCount = MutableLiveData(0)
    val movesCount: LiveData<Int>
        get() = _movesCount

    val win: LiveData<Int>
        get() = _win

    private val history = mutableListOf<String>()

    private var gameModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + gameModelJob)

    override fun onCleared() {
        gameModelJob.cancel()
        super.onCleared()
    }

    init {
        print()
        print(mode)
        timer1 = getTimer1(minutes * 60L)
        timer1.start()

        timer2 = getTimer2(minutes * 60L)

        viewModelScope.launch {
            win.asFlow().collect {
                val timeSpent2 = when (_win.value) {
                    2, 4 -> (minutes * 60 - timeLeftForPlayer2.value!! + add * movesCount.value!!).toInt()
                    1, 3 -> (minutes * 60 - timeLeftForPlayer2.value!! + add * (movesCount.value!! - 1)).toInt()
                    else -> null
                } ?: return@collect
                val record = getGameRecord(it, timeSpent2)
                println(record)
                uiScope.launch {
                    insert(record)
                }
                cancelTimers()
            }
        }
        viewModelScope.launch {
            movesCount.asFlow().collect {
                if (it == width * height && win.value == 0) {
                    val timeSpent2 =
                        if (it % 2 == 0) (minutes * 60 - timeLeftForPlayer2.value!! + add * movesCount.value!!).toInt()
                        else (minutes * 60 - timeLeftForPlayer2.value!! + add * (movesCount.value!! - 1)).toInt()
                    val record = getGameRecord(0, timeSpent2)
                    println(record)
                    uiScope.launch {
                        insert(record)
                    }
                    cancelTimers()
                }
            }
        }
    }

    private fun cancelTimers() {
        timer1.cancel()
        timer2.cancel()
    }

    private suspend fun insert(record: GameRecord) {
        withContext(Dispatchers.IO) {
            dao.insert(record)
        }
    }

    private fun getGameRecord(
        result: Int,
        timeSpent2: Int
    ) = GameRecord(
        id = 0,
        result = result,
        player1 = player1Name,
        player2 = player2Name,
        controlMinutes = minutes,
        controlAddition = add,
        timeSpent1 = (minutes * 60 - timeLeftForPlayer1.value!! + add * movesCount.value!!).toInt(),
        timeSpent2 = timeSpent2,
        date = getCurrentDateTime(),
        gameString = history.joinToString(""),
        mode = mode,
        width = width,
        height = height,
        movesCount = movesCount.value!!
    )

    private var turn = 1


    private fun getTimer2(seconds: Long): CountDownTimer =
        object : CountDownTimer(seconds * 1000, 1_000) {

            override fun onTick(millisUntilFinished: Long) {
                _timeLeftForPlayer2.value = (millisUntilFinished / 1_000)
            }

            override fun onFinish() {
                _timeLeftForPlayer2.value = 0
                _win.value = 3
            }
        }

    private fun getTimer1(seconds: Long) = object : CountDownTimer(seconds * 1000, 1_000) {

        override fun onTick(millisUntilFinished: Long) {
            _timeLeftForPlayer1.value = (millisUntilFinished / 1_000)
        }

        override fun onFinish() {
            _timeLeftForPlayer1.value = 0
            _win.value = 4
        }
    }


    private fun print() {
        for (i in height - 1 downTo 0) {
            for (j in 0 until width) {
                print("${field[i][j].value} ($i,$j) ")
            }
            println()
        }
        println()
    }

    fun onCellClicked(column: Int, row: Int) {
        if (moveIsIllegal(column, row)) return
        println("> $row $column")

        if (turn == 1 && bot != null) bot.setUserMove(column, row)
        switchTimers()
        history.add("($row,$column)")
        field[row][column].value = turn
        tempField[row][column] = turn

        val win = checkWin(column, row, turn, convertLiveDataArray(field))
        if (win) {
            println("WIN $turn")
            _win.value = turn
            _movesCount.value = _movesCount.value!! + 1
        } else {
            _movesCount.value = _movesCount.value!! + 1
            switchTurn()
            if (mode != GameMode.TWO_PLAYERS && turn == 2) {
                println("asking for move")
                val move = bot!!.getMove()
                print("got move: ")
                println(move)
                onCellClicked(move.column, move.row)
            }
        }
    }

    private fun switchTimers() {
        if (turn == 1) {
            timer1.cancel()
            _timeLeftForPlayer1.value = _timeLeftForPlayer1.value?.plus(add)
            timer2 = getTimer2(timeLeftForPlayer2.value!!)
            timer2.start()
        } else {
            timer2.cancel()
            _timeLeftForPlayer2.value = _timeLeftForPlayer2.value?.plus(add)
            timer1 = getTimer1(timeLeftForPlayer1.value!!)
            timer1.start()
        }
    }

    private fun moveIsIllegal(column: Int, row: Int): Boolean {
        if (movesCount.value == width * height || win.value != 0) return true
        if (column >= width || row >= height) return true
        if (field[row][column].value != 0) return true
        if (row != 0 && field[row - 1][column].value == 0) return true
        return false
    }


    private fun switchTurn() {
        turn = if (turn == 1) 2 else 1
    }


    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd:HH-mm-ss")
        return sdf.format(Date())
    }

    data class Move(val row: Int, val column: Int)

}