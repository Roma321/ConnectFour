package com.example.inrow.bot

import com.example.inrow.game.GameViewModel

class RulesBasedBot(field: Array<Array<Int>>, width: Int, height: Int) : Bot(field, width, height) {
    fun innerGetMove(): GameViewModel.Move {
        var moves = getAllMoves()

        val oneMoveWin = checkOneMoveWin(moves)
        if (oneMoveWin != null) return oneMoveWin

        val oneMoveDefence = checkOneMoveDefence(moves)
        if (oneMoveDefence != null) return oneMoveDefence
        moves = filterLosingMoves(moves)
        moves.shuffle()

        val attackingMove = checkCanMakeAttackingMove(moves)
        if (attackingMove != null) return attackingMove

        val moveUsing2InRow = checkCanUse2InRow()
        if (moveUsing2InRow != null) return moveUsing2InRow
        val move = moves[0]
        return move
    }

    private var turn = 2


    private fun checkWin(
        column: Int,
        row: Int,
        player: Int
    ): Boolean {
        if (checkVerticalWin(column, row, player)) return true
        if (checkHorizontalWin(column, row, player)) return true
        if (checkDiagonalWin(column, row, player)) return true
        return false
    }

    private fun checkOneMoveWin(moves: MutableList<GameViewModel.Move>): GameViewModel.Move? {
        for (move in moves) {
            tempField[move.row][move.column] = 2
            if (checkWin(move.column, move.row, turn)) {
                println("Делаю победный ход!")
                return move
            }
            tempField[move.row][move.column] = 0
        }
        return null
    }

    private fun checkOneMoveDefence(moves: MutableList<GameViewModel.Move>): GameViewModel.Move? {
        for (move in moves) {
            val enemyMove = if (turn == 1) 2 else 1
            tempField[move.row][move.column] = enemyMove
            if (checkWin(move.column, move.row, enemyMove)) {
                println("Защищаюсь!")
//                onCellClicked(move.column, move.row)
                return move
//                return true
            }
            tempField[move.row][move.column] = 0
        }
        return null
    }

    private fun filterLosingMoves(
        moves: MutableList<GameViewModel.Move>,
    ): MutableList<GameViewModel.Move> {
        val nonLosingMoves = mutableListOf<GameViewModel.Move>()
        for (move in moves) {
            if (move.row == height - 1) {
                nonLosingMoves.add(move)
                continue
            }
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

    private fun checkCanMakeAttackingMove(moves: MutableList<GameViewModel.Move>): GameViewModel.Move? {
        var mostDangerousMove = moves[0]
        var maxDanger = -1
        for (move in moves) {
            tempField[move.row][move.column] = turn
            val lines = getAllLinesForCell(move.column, move.row)
            val danger =
                lines.count { list -> list.count { it == 0 } == 1 && list.count { it == turn } == 3 }
            println("Опасность хода (${move.row}, ${move.column}) = $danger")
            if (danger > maxDanger) {
                maxDanger = danger
                mostDangerousMove = move
            }
            tempField[move.row][move.column] = 0
        }
        if (maxDanger > 0) {
            return mostDangerousMove
        }
        return null
    }

    private fun checkCanUse2InRow(): GameViewModel.Move? {
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
        for (i in 0 until height) {
            for (j in 1 until width - 2) {
                if ((tempField[i][j] == tempField[i][j + 1]) && tempField[i][j] != 0) { // есть 2 камня одного цвета в ряд в строке
                    println("Есть 2 в ряд: $i $j")
                    if (tempField[i][j - 1] == 0 && tempField[i][j + 2] == 0) { // и по краям от этих двух пусто
                        if (i == 0 || tempField[i - 1][j - 1] != 0 && tempField[i - 1][j + 2] != 0) { //и оба этих пустых поля доступны для хода
                            if (tempField[i][j] == turn) {
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

    private fun getAllLinesForCell(column: Int, row: Int): List<List<Int>> {
        return getDecreasingDiagonalsForCell(column, row)
            .plus(getIncreasingDiagonalsForCell(column, row))
            .plus(getHorizontalsForCell(column, row))
            .plus(getVerticalForCell(row, column))

    }

    private fun checkDiagonalWin(column: Int, row: Int, player: Int): Boolean {

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
        return a.isNotEmpty() && a.any { vertical -> vertical.all { it == player } }
    }

    private fun getVerticalForCell(row: Int, column: Int): MutableList<List<Int>> {
        val res = mutableListOf<List<Int>>()
        for (downOffset in -3..0) {
            if (row + downOffset >= 0 && row + downOffset + 3 < height) {
                val a = listOf(
                    tempField[row + downOffset][column],
                    tempField[row + downOffset + 1][column],
                    tempField[row + downOffset + 2][column],
                    tempField[row + downOffset + 3][column]
                )
                res.add(a)
            }
        }
        return res
    }

    override fun getMove(): GameViewModel.Move {
        println("IN GET MOVE (rule)")
        print()
        val move = innerGetMove()
        setBotMove(move.column, move.row)
        return move
    }

    private fun print() {
        for (i in height - 1 downTo 0) {
            for (j in 0 until width) {
                print("${field[i][j]} ($i,$j) ")
            }
            println()
        }
        println()
    }
}