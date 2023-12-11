package com.example.inrow.funcBot

import com.example.inrow.bot.Constants
import com.example.inrow.bot.NashItem
import com.example.inrow.bot.score
import com.example.inrow.bot.setMoveInArray
import com.example.inrow.game.GameViewModel
import kotlin.math.abs

fun getNashBotMove(field: Array<Array<Int>>): GameViewModel.Move {
    val n = NashItemFunc(field, null, 1, 2, 1)
    return bestFromNashTree(n).first!![0]!!
}

fun bestFromNashTree(item: NashItemFunc):  Pair<List<GameViewModel.Move?>?, Float> {
    if (item.children.isEmpty()) return Pair(null, item.score)
    val worstForOpponent = item.children.shuffled().minBy { bestFromNashTree(it).second }

    val opponentsBest = bestFromNashTree(worstForOpponent)

    return Pair(
        opponentsBest.first.orEmpty().plus(worstForOpponent.previousMove),
        -opponentsBest.second
    )
}

class NashItemFunc(
    field: Array<Array<Int>>,
    var previousMove: GameViewModel.Move?,
    private var level: Int,
    private var player: Int,
    private var opponent: Int
) {
    var children: List<NashItemFunc>
    var score: Float

    init {
        this.score = score(player, opponent, field, previousMove)
        children = if (level > Constants.MAX_NASH_DEPTH || abs(this.score) == 1f) {
            emptyList()
        } else {
            val moves = possibleMoves(field)
            moves.map {
                val newField = setMoveInArray(field, it, player)
                NashItemFunc(newField, it, level + 1, opponent, player)
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

}