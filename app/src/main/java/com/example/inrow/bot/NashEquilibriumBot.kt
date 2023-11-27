package com.example.inrow.bot

import com.example.inrow.game.GameViewModel
import kotlin.math.abs

class NashEquilibriumBot(field: Array<Array<Int>>, width: Int, height: Int) :
    Bot(field, width, height) {
    override fun getMove(): GameViewModel.Move {
        TODO("Not yet implemented")
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
        this.score = score(player, opponent, field)
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
        val worstForOpponent = children.minBy { it.bestMove().second }
        val opponentsBest = worstForOpponent.bestMove()

        return Pair(
            opponentsBest.first.orEmpty().plus(worstForOpponent.previousMove),
            -opponentsBest.second
        )
    }
}

fun score(player: Int, opponent: Int, field: Array<Array<Int>>): Float {

    return 0f
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

class Constants {
    companion object {
        val MAX_NASH_DEPTH = 9
    }
}