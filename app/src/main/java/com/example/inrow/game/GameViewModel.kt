package com.example.inrow.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel(
    val height: Int = 8,
    val width: Int = 8,
    val useBot: Boolean = true
) : ViewModel() {


    var field: Array<Array<MutableLiveData<Int>>> =
        Array(height) { Array(width) { MutableLiveData(0) } }

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
        println('1')
        if (field[row][column].value != 0) return
        println('2')
        if (row == 0 || field[row - 1][column].value != 0) {
            field[row][column].value = turn
            this.print()
            val win = checkWin(column, row)
            if (win) {
                println("WIN $turn")
                _win.value = turn
//                switchTurn()
            } else {
                _movesCount.value = _movesCount.value!! + 1
                switchTurn()
                println("here, $useBot $turn")
                if (useBot && turn == 2) {
                    val (columnFromBot, rowFromBot) = getRandomMove()
                    onCellClicked(columnFromBot, rowFromBot)
                }

            }
        }

    }

    private fun getRandomMove(): Pair<Int, Int> {
        val moves = mutableListOf<Pair<Int, Int>>()
        for (rowNumber in height - 1 downTo 0) {
            for (columnNumber in 0 until width) {
                if (field[rowNumber][columnNumber].value == 0) {
                    if (rowNumber == 0 || field[rowNumber - 1][columnNumber].value != 0)
                        moves.add(Pair(columnNumber, rowNumber))
                }
            }
            println()
        }
        return moves.shuffled()[0]
    }

    private fun switchTurn() {
        turn = if (turn == 1) 2 else 1
    }

    private fun checkWin(column: Int, row: Int): Boolean {
        print('3')
        if (checkVerticalWin(column, row)) return true
        if (checkHorizontalWin(column, row)) return true
        if (checkDiagonalWin(column, row)) return true
        return false
    }

    private fun checkDiagonalWin(column: Int, row: Int): Boolean {
        println("d$turn")
        /* That diagonal:
        * 0 0 0 x
        * 0 0 x 0
        * 0 x 0 0
        * x 0 0 0
        * */
        for (leftOffset in -3..0) {

            if (column + leftOffset < 0
                || row + leftOffset < 0
                || column + leftOffset + 3 >= width
                || row + leftOffset + 3 >= height
            ) continue

            if (listOf(
                    field[row + leftOffset][column + leftOffset].value,
                    field[row + leftOffset + 1][column + leftOffset + 1].value,
                    field[row + leftOffset + 2][column + leftOffset + 2].value,
                    field[row + leftOffset + 3][column + leftOffset + 3].value
                ).all { it == turn }
            ) {
                println("ПРЯМАЯ")
                return true
            }
        }


        /* That diagonal:
         * x 0 0 0
         * 0 x 0 0
         * 0 0 x 0
         * 0 0 0 x
         */
        for (leftOffset in -3..0) {
            if (column + leftOffset < 0
                || row - leftOffset >= height
                || column + leftOffset + 3 >= width
                || row - leftOffset - 3 < 0
            ) continue

            if (field[row - leftOffset][column + leftOffset].value == turn &&
                field[row - leftOffset - 1][column + leftOffset + 1].value == turn &&
                field[row - leftOffset - 2][column + leftOffset + 2].value == turn &&
                field[row - leftOffset - 3][column + leftOffset + 3].value == turn
            ) {
                println("ОБРАТНВЯ")
                return true
            }
        }
        return false
    }

    private fun checkHorizontalWin(column: Int, row: Int): Boolean {
        println("h$turn")
        for (startColumn in column - 3..column) {
            if (startColumn < 0 || startColumn >= width - 3) continue
            if (field[row][startColumn].value == field[row][startColumn + 1].value
                && field[row][startColumn + 1].value == field[row][startColumn + 2].value
                && field[row][startColumn + 2].value == field[row][startColumn + 3].value
                && field[row][column].value == turn
            ) {
                return true
            }
        }
        return false
    }

    private fun checkVerticalWin(column: Int, row: Int): Boolean {
        println("v$turn")
        if (row >= 3) {
            if (field[row][column].value == field[row - 1][column].value
                && field[row - 2][column].value == field[row - 1][column].value
                && field[row - 2][column].value == field[row - 3][column].value
                && field[row][column].value == turn
            ) {
                return true
            }
        }
        return false
    }

}