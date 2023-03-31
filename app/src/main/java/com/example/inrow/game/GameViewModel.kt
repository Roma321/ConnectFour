package com.example.inrow.game

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import com.example.inrow.GameMode
import com.example.inrow.database.GameDatabaseDao
import com.example.inrow.database.GameRecord
import kotlinx.coroutines.*
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
//        timer2.start()
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
        if (row == 0 || field[row - 1][column].value != 0) {
            switchTimers()
            history.add("($row,$column)")
            field[row][column].value = turn
            tempField[row][column] = turn

            this.print()
            val win = checkWin(column, row, turn)
            if (win) {
                println("WIN $turn")
                _win.value = turn
                _movesCount.value = _movesCount.value!! + 1
            } else {
                _movesCount.value = _movesCount.value!! + 1
                switchTurn()
                if (mode != GameMode.TWO_PLAYERS && turn == 2) {
                    var moves = getAllMoves()
                    if (mode == GameMode.RANDOM_BOT) {
                        val move = moves.shuffled()[0]
                        onCellClicked(move.column, move.row)
                    } else {
                        if (movesCount.value == width * height) return
                        if (checkOneMoveWin(moves)) return
                        if (checkOneMoveDefence(moves)) return
                        moves = filterLosingMoves(moves)
                        moves.shuffle()
                        if (checkCanMakeAttackingMove(moves)) return
                        if (checkCanUse2InRow()) return
                        val move = moves[0]
                        onCellClicked(move.column, move.row)
                    }
                }
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
        return false
    }

    private fun checkCanMakeAttackingMove(moves: MutableList<Move>): Boolean {
        var mostDangerousMove = moves[0]
        var maxDanger = -1
        for (move in moves) {
            tempField[move.row][move.column] = turn
            val lines = getAllLinesForCell(move.column, move.row)
            print(lines)
            val danger =
                lines.count { list -> list.count { it == 0 } == 1 && list.count { it == turn } == 3 }
            println("Опасность хода (${move.row}, ${move.column}) = $danger")
            if (danger > maxDanger) {
                maxDanger = danger
                mostDangerousMove = move
            }
            tempField[move.row][move.column] = 0
        }
        if (maxDanger > 0) {
            onCellClicked(mostDangerousMove.column, mostDangerousMove.row)
            return true
        }
        return false
    }

    private fun filterLosingMoves(
        moves: MutableList<Move>,
    ): MutableList<Move> {
        val nonLosingMoves = mutableListOf<Move>()
        for (move in moves) {
            if (move.row == height - 1) {
                nonLosingMoves.add(move)
                continue
            }
            val enemyMove = if (turn == 1) 2 else 1
            tempField[move.row + 1][move.column] = enemyMove
            if (checkWin(move.column, move.row + 1, enemyMove)) {
                println("Найден проигрывающий ход: ${move.row} ${move.column}")
            } else {
                nonLosingMoves.add(move)
            }
            tempField[move.row + 1][move.column] = 0
        }
        if (nonLosingMoves.isEmpty()) return moves
        return nonLosingMoves
    }

    private fun checkCanUse2InRow(): Boolean {
        /*
                        checks this situation:
                        0 0 0 0 0 0
                        0 0 x x 0 0

                        where next move could be
                        0 0 0 0 0 0
                        0 x x x 0 0

                        with win text move


                         */
        //                        val pairsOfPlayer = mutableListOf<Pair<Move,Move>>()
        val pairsOfEnemyPlayer = mutableListOf<Pair<Move, Move>>()
        for (i in 0 until height) {
            for (j in 1 until width - 2) {
                if ((tempField[i][j] == tempField[i][j + 1]) && tempField[i][j] != 0) { // есть 2 камня одного цвета в ряд в строке
                    println("Есть 2 в ряд: $i $j")
                    if (tempField[i][j - 1] == 0 && tempField[i][j + 2] == 0) { // и по краям от этих двух пусто
                        if (i == 0 || tempField[i - 1][j - 1] != 0 && tempField[i - 1][j + 2] != 0) { //и оба этих пустых поля доступны для хода
                            if (tempField[i][j] == turn) {
                                println("Атакую")
                                if (j > width / 2 - 1)//выбираем из двух ту, что ближе к центру
                                    onCellClicked(row = i, column = j - 1)
                                else {
                                    onCellClicked(row = i, column = j + 2)
                                }
                                return true
                            }
                            pairsOfEnemyPlayer.add(
                                Pair(
                                    Move(i, j),
                                    Move(i, j + 1)
                                )
                            )
                        }
                    }
                }
            }
        }

        if (pairsOfEnemyPlayer.isNotEmpty()) {
            val suspiciousRow = pairsOfEnemyPlayer[0]
            val j = suspiciousRow.first.column
            val i = suspiciousRow.first.row
            println("Подстраховываюсь")
            if (j > width / 2 - 1)//выбираем из двух ту, что ближе к центру
                onCellClicked(row = i, column = j - 1)
            else {
                onCellClicked(row = i, column = j + 2)
            }
            return true
        }
        return false
    }

    private fun checkOneMoveDefence(moves: MutableList<Move>): Boolean {
        for (move in moves) {
            val enemyMove = if (turn == 1) 2 else 1
            tempField[move.row][move.column] = enemyMove
            if (checkWin(move.column, move.row, enemyMove)) {
                println("Защищаюсь!")
                onCellClicked(move.column, move.row)
                return true
            }
            tempField[move.row][move.column] = 0
        }
        return false
    }

    private fun checkOneMoveWin(moves: MutableList<Move>): Boolean {
        for (move in moves) {
            tempField[move.row][move.column] = turn
            if (checkWin(move.column, move.row, turn)) {
                println("Делаю победный ход!")
                onCellClicked(move.column, move.row)
                return true
            }
            tempField[move.row][move.column] = 0
        }
        return false
    }

    private fun getAllMoves(): MutableList<Move> {
        val moves = mutableListOf<Move>()
        for (rowNumber in height - 1 downTo 0) {
            for (columnNumber in 0 until width) {
                if (field[rowNumber][columnNumber].value == 0) {
                    if (rowNumber == 0 || field[rowNumber - 1][columnNumber].value != 0)
                        moves.add(Move(rowNumber, columnNumber))
                }
            }
            println()
        }
        return moves
    }

    private fun switchTurn() {
        turn = if (turn == 1) 2 else 1
    }

    private fun checkWin(
        column: Int,
        row: Int,
        player: Int
    ): Boolean {
//        print('3')
        if (checkVerticalWin(column, row, player)) return true
        if (checkHorizontalWin(column, row, player)) return true
        if (checkDiagonalWin(column, row, player)) return true
        return false
    }

    private fun checkDiagonalWin(column: Int, row: Int, player: Int): Boolean {
//        println("d$turn")

        val list = getIncreasingDiagonalsForCell(column, row)

        if (list.any { it.all { a -> a == player } }) {
            println("ПРЯМАЯ")
            return true
        }


        /* That diagonal:
         * x 0 0 0
         * 0 x 0 0
         * 0 0 x 0
         * 0 0 0 x
         */

        val list2 = getDecreasingDiagonalsForCell(column, row)
        if (list2.any { it.all { a -> a == player } }) {
            println("ОБРАТНВЯ")
            return true
        }
        return false
    }

    private fun getAllLinesForCell(column: Int, row: Int): List<List<Int>> {
        return getDecreasingDiagonalsForCell(column, row)
            .plus(getIncreasingDiagonalsForCell(column, row))
            .plus(getHorizontalsForCell(column, row))
            .plus(getVerticalForCell(row, column))

    }

    private fun getDecreasingDiagonalsForCell(column: Int, row: Int): MutableList<List<Int>> {
        val decreasingDiagonals = mutableListOf<List<Int>>()
        for (leftOffset in -3..0) {
            if (column + leftOffset < 0
                || row - leftOffset >= height
                || column + leftOffset + 3 >= width
                || row - leftOffset - 3 < 0
            ) continue

            val a = listOf(
                tempField[row - leftOffset][column + leftOffset],
                tempField[row - leftOffset - 1][column + leftOffset + 1],
                tempField[row - leftOffset - 2][column + leftOffset + 2],
                tempField[row - leftOffset - 3][column + leftOffset + 3]
            )
            decreasingDiagonals.add(a)
        }

        return decreasingDiagonals
    }

    private fun getIncreasingDiagonalsForCell(column: Int, row: Int): MutableList<List<Int>> {

        /* That diagonal:
        * 0 0 0 x
        * 0 0 x 0
        * 0 x 0 0
        * x 0 0 0
        * */
        val increaseDiagonals = mutableListOf<List<Int>>()
        for (leftOffset in -3..0) {
            if (column + leftOffset < 0
                || row + leftOffset < 0
                || column + leftOffset + 3 >= width
                || row + leftOffset + 3 >= height
            ) continue

            val a = listOf(
                tempField[row + leftOffset][column + leftOffset],
                tempField[row + leftOffset + 1][column + leftOffset + 1],
                tempField[row + leftOffset + 2][column + leftOffset + 2],
                tempField[row + leftOffset + 3][column + leftOffset + 3]
            )
            increaseDiagonals.add(a)
        }

        return increaseDiagonals
    }

    private fun checkHorizontalWin(column: Int, row: Int, player: Int): Boolean {
//        println("h$turn")
        val a = getHorizontalsForCell(column, row)
        if (a.isNotEmpty() && a.any { it.all { a -> a == player } }) {
            return true
        }
        return false
    }

    private fun getHorizontalsForCell(column: Int, row: Int): MutableList<List<Int>> {
        val res = mutableListOf<List<Int>>()
        for (startColumn in column - 3..column) {
            if (startColumn < 0 || startColumn >= width - 3) continue
            val a = listOf(
                tempField[row][startColumn + 1],
                tempField[row][startColumn + 2],
                tempField[row][startColumn + 3],
                tempField[row][startColumn]
            )
            res.add(a)
        }
        return res
    }

    private fun checkVerticalWin(
        column: Int,
        row: Int,
        player: Int
    ): Boolean {
//        println("v$player")
        val a = getVerticalForCell(row, column)
        if (a.isNotEmpty() && a.any { vertical -> vertical.all { it == player } }) {
            return true
        }
        return false
    }

    private fun getVerticalForCell(row: Int, column: Int): MutableList<List<Int>> {
        val res = mutableListOf<List<Int>>()
        for (downOffset in -3..0) {
            if (row + downOffset >= 0 && row + downOffset + 3 < height) {
                val a = listOf(
                    tempField[row + downOffset][column],
                    tempField[row + downOffset + 1][column],
                    tempField[row + downOffset + 2][column],
                    tempField[row + downOffset + 3][column]
                )
                res.add(a)
            }
        }
        return res
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd:HH-mm-ss")
        return sdf.format(Date())
    }

    data class Move(val row: Int, val column: Int)

}