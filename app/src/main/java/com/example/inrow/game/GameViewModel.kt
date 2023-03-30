package com.example.inrow.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inrow.GameMode

class GameViewModel(
    val height: Int = 8,
    val width: Int = 8,
    private val mode: GameMode
) : ViewModel() {


    var field: Array<Array<MutableLiveData<Int>>> =
        Array(height) { Array(width) { MutableLiveData(0) } }

    private val tempField = List(height) { MutableList(width) { 0 } }

    init {
        print()
    }

    private var _movesCount = MutableLiveData(0)
    val movesCount: LiveData<Int>
        get() = _movesCount


    private var turn = 1
    private var _win = MutableLiveData(0)
    val win: LiveData<Int>
        get() = _win
    private var dangerousLines = mutableListOf<Move>()

    fun print() {
        for (i in height - 1 downTo 0) {
            for (j in 0 until width) {
                print("${field[i][j].value} ($i,$j) ")
            }
            println()
        }
        println()
    }

    fun onCellClicked(column: Int, row: Int) {
        println("> $row $column")
        if (column >= width || row >= height) return
//        println('1')
        if (field[row][column].value != 0) return
//        println('2')
        if (row == 0 || field[row - 1][column].value != 0) {
            field[row][column].value = turn
            tempField[row][column] = turn
            this.print()
            val win = checkWin(column, row, turn)
            if (win) {
                println("WIN $turn")
                _win.value = turn
            } else {
                _movesCount.value = _movesCount.value!! + 1
                switchTurn()
                if (mode != GameMode.TWO_PLAYERS && turn == 2) {
                    var moves = getAllMoves()
                    if (mode == GameMode.RANDOM_BOT) {
                        val move = moves.shuffled()[0]
                        onCellClicked(move.column, move.row)
                    } else {
                        if (checkOneMoveWin(moves)) return
                        if (checkOneMoveDefence(moves)) return
                        moves = filterLosingMoves(moves)
                        if (checkCanUse2InRow()) return
                        val move = moves.shuffled()[0]
                        onCellClicked(move.column, move.row)
                    }
                }
            }
        }
    }

    private fun filterLosingMoves(
        moves: MutableList<Move>,
    ): MutableList<Move> {
        val nonLosingMoves = mutableListOf<Move>()
        for (move in moves) {
            if (move.row == height - 1) continue
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

                        with automatic win text move


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

    fun countDanger(player: Int) {
//        if (collectDangerousLines) {
//            if (a.count { it == player } == 3 && a.count { it == 0 } == 1)
//                dangerousLines.add(Move(row = row, column = column))
//        }
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
        if (a.isNotEmpty() && a.all { it == player }) {
            return true
        }
        return false
    }

    private fun getVerticalForCell(row: Int, column: Int): List<Int> {
        if (row >= 3) {
            return listOf(
                tempField[row][column],
                tempField[row - 1][column],
                tempField[row - 2][column],
                tempField[row - 3][column]
            )
        }
        return emptyList()
    }

    data class Move(val row: Int, val column: Int)

}