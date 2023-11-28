package com.example.inrow.funcBot

import androidx.lifecycle.MutableLiveData
import com.example.inrow.game.GameViewModel

fun checkWin(
    column: Int,
    row: Int,
    player: Int,
    field: Array<Array<Int>>
): Boolean {
    if (checkVerticalWin(column, row, player, field)) return true
    if (checkHorizontalWin(column, row, player, field)) return true
    if (checkDiagonalWin(column, row, player, field)) return true
    return false
}

private fun checkDiagonalWin(
    column: Int,
    row: Int,
    player: Int,
    field: Array<Array<Int>>
): Boolean {

    val list = getIncreasingDiagonalsForCell(column, row, field)

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

    val list2 = getDecreasingDiagonalsForCell(column, row, field)
    if (list2.any { it.all { a -> a == player } }) {
        println("ОБРАТНВЯ")
        return true
    }
    return false
}

fun getDecreasingDiagonalsForCell(
    column: Int,
    row: Int,
    field: Array<Array<Int>>
): MutableList<List<Int>> {
    val decreasingDiagonals = mutableListOf<List<Int>>()
    for (leftOffset in -3..0) {
        if (column + leftOffset < 0
            || row - leftOffset >= field.size
            || column + leftOffset + 3 >= field[0].size
            || row - leftOffset - 3 < 0
        ) continue

        val a = listOf(
            field[row - leftOffset][column + leftOffset],
            field[row - leftOffset - 1][column + leftOffset + 1],
            field[row - leftOffset - 2][column + leftOffset + 2],
            field[row - leftOffset - 3][column + leftOffset + 3]
        )
        decreasingDiagonals.add(a)
    }

    return decreasingDiagonals
}

fun getIncreasingDiagonalsForCell(
    column: Int,
    row: Int,
    field: Array<Array<Int>>
): MutableList<List<Int>> {

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
            || column + leftOffset + 3 >= field.size
            || row + leftOffset + 3 >= field[0].size
        ) continue

        val a = listOf(
            field[row + leftOffset][column + leftOffset],
            field[row + leftOffset + 1][column + leftOffset + 1],
            field[row + leftOffset + 2][column + leftOffset + 2],
            field[row + leftOffset + 3][column + leftOffset + 3]
        )
        increaseDiagonals.add(a)
    }

    return increaseDiagonals
}

private fun checkHorizontalWin(
    column: Int,
    row: Int,
    player: Int,
    field: Array<Array<Int>>
): Boolean {
//        println("h$turn")
    val a = getHorizontalsForCell(column, row, field)
    if (a.isNotEmpty() && a.any { it.all { a -> a == player } }) {
        return true
    }
    return false
}

fun getHorizontalsForCell(
    column: Int,
    row: Int,
    field: Array<Array<Int>>
): MutableList<List<Int>> {
    val res = mutableListOf<List<Int>>()
    for (startColumn in column - 3..column) {
        if (startColumn < 0 || startColumn >= field[0].size - 3) continue
        val a = listOf(
            field[row][startColumn + 1],
            field[row][startColumn + 2],
            field[row][startColumn + 3],
            field[row][startColumn]
        )
        res.add(a)
    }
    return res
}

private fun checkVerticalWin(
    column: Int,
    row: Int,
    player: Int,
    field: Array<Array<Int>>
): Boolean {
//        println("v$player")
    val a = getVerticalForCell(row, column, field)
    return a.isNotEmpty() && a.any { vertical -> vertical.all { it == player } }
}

fun getVerticalForCell(
    row: Int,
    column: Int,
    field: Array<Array<Int>>
): MutableList<List<Int>> {
    val res = mutableListOf<List<Int>>()
    for (downOffset in -3..0) {
        if (row + downOffset >= 0 && row + downOffset + 3 < field.size) {
            val a = listOf(
                field[row + downOffset][column],
                field[row + downOffset + 1][column],
                field[row + downOffset + 2][column],
                field[row + downOffset + 3][column]
            )
            res.add(a)
        }
    }
    return res
}

fun possibleMoves(field: Array<Array<Int>>): MutableList<GameViewModel.Move> {
    val height = field.size
    val width = field[0].size
    val moves = mutableListOf<GameViewModel.Move>()
    for (rowNumber in height - 1 downTo 0) {
        for (columnNumber in 0 until width) {
            if (field[rowNumber][columnNumber] == 0) {
                if (rowNumber == 0 || field[rowNumber - 1][columnNumber] != 0)
                    moves.add(GameViewModel.Move(rowNumber, columnNumber))
            }
        }
    }
    return moves

}

fun convertLiveDataArray(array: Array<Array<MutableLiveData<Int>>>): Array<Array<Int>> {
    return array.map { innerArray ->
        innerArray.map { liveData ->
            liveData.value ?: 0 // use a default value if the MutableLiveData is null
        }.toTypedArray()
    }.toTypedArray()
}