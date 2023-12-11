package com.example.inrow.funcBot

import com.example.inrow.bot.setMoveInArray
import com.example.inrow.game.GameViewModel

fun getRuleBasedMove(field: Array<Array<Int>>): GameViewModel.Move {
    var moves = possibleMoves(field)

    val oneMoveWin = checkOneMoveWin(moves, field)
    if (oneMoveWin != null) return oneMoveWin

    val oneMoveDefence = checkOneMoveDefence(moves, field)
    if (oneMoveDefence != null) return oneMoveDefence
    moves = filterLosingMoves(moves, field)
    moves.shuffle()

    val attackingMove = checkCanMakeAttackingMove(moves, field)
    if (attackingMove != null) return attackingMove

    val moveUsing2InRow = checkCanUse2InRow(field)
    if (moveUsing2InRow != null) return moveUsing2InRow
    val move = moves[0]
    return move
}

private fun checkOneMoveWin(
    moves: MutableList<GameViewModel.Move>,
    field: Array<Array<Int>>
): GameViewModel.Move? {
    val botSide = 2
    for (move in moves) {
        val newField = setMoveInArray(field, move, botSide)
        if (checkWin(move.column, move.row, botSide, newField)) {
            return move
        }
    }
    return null
}

private fun checkOneMoveDefence(
    moves: MutableList<GameViewModel.Move>, field: Array<Array<Int>>
): GameViewModel.Move? {
    val opponentSide = 1

    for (move in moves) {
        val newField = setMoveInArray(field, move, opponentSide)

        if (checkWin(move.column, move.row, opponentSide, newField)) {
            return move
        }
    }
    return null
}

private fun filterLosingMoves(
    moves: MutableList<GameViewModel.Move>,
    field: Array<Array<Int>>
): MutableList<GameViewModel.Move> {
    val height = field.size
    val opponentSide = 1
    val botSide = 2
    val nonLosingMoves = moves.filter {
        if (it.row == height - 1) {
            return@filter true
        }
        val newField = setMoveInArray(field, it, botSide)
        newField[it.row + 1][it.column] = opponentSide
        return@filter !checkWin(it.column, it.row + 1, opponentSide, newField)
    }
    if (nonLosingMoves.isEmpty()) return moves
    return nonLosingMoves.toMutableList()
}

private fun checkCanMakeAttackingMove(
    moves: MutableList<GameViewModel.Move>,
    field: Array<Array<Int>>
): GameViewModel.Move? {
    var mostDangerousMove = moves[0]
    var maxDanger = -1
    val botSide = 2
    for (move in moves) {
        val newField = setMoveInArray(field, move, botSide)
        val lines = getAllLinesForCell(move.column, move.row, newField)
        val danger =
            lines.count { list -> list.count { it == 0 } == 1 && list.count { it == botSide } == 3 }
        println("Опасность хода (${move.row}, ${move.column}) = $danger")
        if (danger > maxDanger) {
            maxDanger = danger
            mostDangerousMove = move
        }
    }
    if (maxDanger > 0) {
        return mostDangerousMove
    }
    return null
}

fun getAllLinesForCell(column: Int, row: Int, field: Array<Array<Int>>): List<List<Int>> {
    return getDecreasingDiagonalsForCell(column, row, field)
        .plus(getIncreasingDiagonalsForCell(column, row, field))
        .plus(getHorizontalsForCell(column, row, field))
        .plus(getVerticalForCell(row, column, field))

}

private fun checkCanUse2InRow(field: Array<Array<Int>>): GameViewModel.Move? {
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
    val pairsOfEnemyPlayer = mutableListOf<Pair<GameViewModel.Move, GameViewModel.Move>>()
    val height = field.size
    val width = field[0].size
    val opponentSide = 1
    val botSide = 2
    for (i in 0 until height) {
        for (j in 1 until width - 2) {
            if ((field[i][j] == field[i][j + 1]) && field[i][j] != 0) { // есть 2 камня одного цвета в ряд в строке
                println("Есть 2 в ряд: $i $j")
                if (field[i][j - 1] == 0 && field[i][j + 2] == 0) { // и по краям от этих двух пусто
                    if (i == 0 || field[i - 1][j - 1] != 0 && field[i - 1][j + 2] != 0) { //и оба этих пустых поля доступны для хода
                        if (field[i][j] == botSide) {
                            println("Атакую")
                            return if (j > width / 2 - 1)//выбираем из двух ту, что ближе к центру
                            //                                    onCellClicked(row = i, column = j - 1)
                                GameViewModel.Move(i, j - 1)
                            else {
                                GameViewModel.Move(i, j + 2)
                            }
                        }
                        pairsOfEnemyPlayer.add(
                            Pair(
                                GameViewModel.Move(i, j),
                                GameViewModel.Move(i, j + 1)
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
            return GameViewModel.Move(i, j - 1)
        else {
            return GameViewModel.Move(i, j + 2)
        }
    }
    return null
}
