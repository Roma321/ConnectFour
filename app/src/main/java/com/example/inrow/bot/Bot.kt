package com.example.inrow.bot

import com.example.inrow.game.GameViewModel

abstract class Bot(val field: Array<Array<Int>>, val width: Int, val height: Int) {
    protected val tempField = List(height) { MutableList(width) { 0 } }
    protected val botSide = 2
    protected val playerSide = 1

    fun setUserMove(column: Int, row: Int) {
        field[row][column] = playerSide
        tempField[row][column] = playerSide
    }

    fun setBotMove(column: Int, row: Int) {
        field[row][column] = botSide
        tempField[row][column] = botSide
    }

    protected fun getAllMoves(): MutableList<GameViewModel.Move> {
        val moves = mutableListOf<GameViewModel.Move>()
        for (rowNumber in height - 1 downTo 0) {
            for (columnNumber in 0 until width) {
                if (field[rowNumber][columnNumber] == 0) {
                    if (rowNumber == 0 || field[rowNumber - 1][columnNumber] != 0)
                        moves.add(GameViewModel.Move(rowNumber, columnNumber))
                }
            }
            println()
        }
        return moves
    }



    abstract fun getMove(): GameViewModel.Move
}