package com.example.inrow.bot

import com.example.inrow.funcBot.checkWin
import com.example.inrow.funcBot.possibleMoves
import com.example.inrow.game.GameViewModel
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class NashEquilibriumBot(field: Array<Array<Int>>, width: Int, height: Int) :
    Bot(field, width, height) {
    fun innerGetMove(): GameViewModel.Move {
        val n = NashItem(field, null, 1, 2, 1)
        val a = n.bestMove()
        println(a.second)
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
    val distToCenterMax = (field.size + field[0].size) / 2

    if (checkWin(previousMove.column, previousMove.row, player, field)) return 1f
    if (checkWin(previousMove.column, previousMove.row, opponent, field)) return -1f
    var rating = 0f
    val coef = (1f / field.flatten().size).pow(2)
    for ((i, row) in field.withIndex()) {
        for ((j, cell) in row.withIndex()) {
            val thisDistToCenter = abs(i - field.size / 2) + abs(j - row.size / 2)
            rating += (distToCenterMax - thisDistToCenter) * coef * when (cell) {
                player -> 1
                else -> 0
            }

        }
    }
    if (abs(rating) >= 1) {
        println(rating)
    }
    return rating
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
        val MAX_NASH_DEPTH = 5
    }
}


//(-abs(4 - 4) / 4 + 1) * coef