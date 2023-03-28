class Game {

    var turn = 1

    val height = 9
    val width = 8

    val field = Array(height) { Array(width) { 0 } }

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
        if (column >= width || row >= height) return
        if (row == 0 || field[row - 1][column] != 0) {
            field[row][column] = turn
            val win = checkWin(column, row)
            if (!win) {
                switchTurn()
                return
            } else {
                println("WIN $turn")
                switchTurn()//TODO это хуйня
            }





        }

    }

    private fun switchTurn() {
        turn = if (turn == 1) 2 else 1
    }

    private fun checkWin(column: Int, row: Int): Boolean {
        if (checkVerticalWin(column, row)) return true
        if (checkHorizontalWin(column, row)) return true
        return false
    }

    private fun checkHorizontalWin(column: Int, row: Int): Boolean {
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
        println(row)
        println(column)
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