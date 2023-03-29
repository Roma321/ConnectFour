package com.example.inrow.game

import androidx.lifecycle.ViewModel

class GameViewModel(
    val height: Int = 8,
    val width: Int = 8,
    val withBot: Boolean = true
) : ViewModel() {


    var field: Array<Array<Int>> = Array(height) { Array(width) { 0 } }

    init {
        print()
    }

    var turn = 1


    fun print() {
        for (i in height - 1 downTo 0) {
            for (j in 0 until width) {
                print("${field[i][j]} ($i,$j) ")
            }
            println()
        }
        println()
    }

    fun onCellClicked(column: Int, row: Int) {
        println("> $row $column")
        if (column >= width || row >= height) return
        println('1')
        if (field[row][column] != 0) return
        println('2')
        if (row == 0 || field[row - 1][column] != 0) {
            field[row][column] = turn
            this.print()
            val win = checkWin(column, row)
            if (win) {
                println("WIN $turn")
                switchTurn()//TODO это хуйня
            } else {
                switchTurn()
                return
            }
        }

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
            if (field[row + leftOffset][column + leftOffset] == field[row + leftOffset + 1][column + leftOffset + 1]
                && field[row + leftOffset + 2][column + leftOffset + 2] == field[row + leftOffset + 1][column + leftOffset + 1]
                && field[row + leftOffset + 2][column + leftOffset + 3] == field[row + leftOffset + 2][column + leftOffset + 2]
                && field[row][column] == turn
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

            if (field[row - leftOffset][column + leftOffset] == turn &&
                field[row - leftOffset - 1][column + leftOffset + 1] == turn &&
                field[row - leftOffset - 2][column + leftOffset + 2] == turn &&
                field[row - leftOffset - 3][column + leftOffset + 3] == turn
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
            if (field[row][startColumn] == field[row][startColumn + 1]
                && field[row][startColumn + 1] == field[row][startColumn + 2]
                && field[row][startColumn + 2] == field[row][startColumn + 3]
                && field[row][column] == turn
            ) {
                return true
            }
        }
        return false
    }

    private fun checkVerticalWin(column: Int, row: Int): Boolean {
        println("v$turn")
        if (row >= 3) {
            if (field[row][column] == field[row - 1][column]
                && field[row - 2][column] == field[row - 1][column]
                && field[row - 2][column] == field[row - 3][column]
                && field[row][column] == turn
            ) {
                return true
            }
        }
        return false
    }
}