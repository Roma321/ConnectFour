package com.example.inrow.bot

import com.example.inrow.game.GameViewModel
import kotlin.math.abs

class NashEquilibriumBot(field: Array<Array<Int>>, width: Int, height: Int) :
    Bot(field, width, height) {
    fun innerGetMove(): GameViewModel.Move {
        val n = NashItem(field, null, 1, 2, 1)
        val a = n.bestMove()
        return a.first?.last() ?: GameViewModel.Move(0, 0)
    }

    override fun getMove(): GameViewModel.Move {
        val move = innerGetMove()
        setBotMove(move.column, move.row)
        return move
    }
}

class NashItem(
    field: Array<Array<Int>>,
    private var previousMove: GameViewModel.Move?,
    private var level: Int,
    private var player: Int,
    private var opponent: Int
) {
    private var children: List<NashItem>
    private var score: Float

    init {
        this.score = score(player, opponent, field, previousMove)
        children = if (level > Constants.MAX_NASH_DEPTH || abs(this.score) == 1f) {
            emptyList()
        } else {
            val moves = possibleMoves(field)
            moves.map {
                val newField = setMoveInArray(field, it, player)
                NashItem(newField, it, level + 1, opponent, player)
            }
        }
    }

    override fun toString(): String {
        return "NashItem(score=$score, previousMove=$previousMove, level=$level, player=$player, opponent=$opponent, children=${
            children.joinToString(
                "\n" + " ".repeat(level * 4),
                prefix = if (children.isEmpty()) "" else "\n" + " ".repeat(level * 4)
            )
        })"
    }

    fun bestMove(): Pair<List<GameViewModel.Move?>?, Float> {
        if (children.isEmpty()) return Pair(null, this.score)
        val worstForOpponent = children.shuffled().minBy { it.bestMove().second }
        val opponentsBest = worstForOpponent.bestMove()

        return Pair(
            opponentsBest.first.orEmpty().plus(worstForOpponent.previousMove),
            -opponentsBest.second
        )
    }
}

fun score(
    player: Int,
    opponent: Int,
    field: Array<Array<Int>>,
    previousMove: GameViewModel.Move?
): Float {
    if (previousMove == null) return 0f

    if (checkWin(previousMove.column, previousMove.row, player, field)) return 1f
    if (checkWin(previousMove.column, previousMove.row, opponent, field)) return -1f
    var rating = 0f
    val coef = 0.5f / field.flatten().size
    for ((i, row) in field.withIndex())
        for ((j, cell) in row.withIndex()) {
            rating += (row.size / 2 - abs(row.size / 2 - i) / row.size) * coef * when (cell) {
                player -> 1
//                opponent -> -1
                else -> 0
            }

            rating += (row.size / 2 - abs(row.size / 2 - i) / row.size) * coef / 2 * when (cell) {
                player -> 1
//                opponent -> -1
                else -> 0
            }
        }
    if (abs(rating) >= 1) {
        println(rating)
    }
    return rating
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

fun setMoveInArray(
    field: Array<Array<Int>>,
    move: GameViewModel.Move,
    player: Int
): Array<Array<Int>> {
    val newField = field.map { it.clone() }.toTypedArray()
    newField[move.row][move.column] = player
    return newField
}

private fun checkWin(
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

private fun getDecreasingDiagonalsForCell(
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

private fun getIncreasingDiagonalsForCell(
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

private fun getHorizontalsForCell(
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

private fun getVerticalForCell(
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

class Constants {
    companion object {
        val MAX_NASH_DEPTH = 5
    }
}