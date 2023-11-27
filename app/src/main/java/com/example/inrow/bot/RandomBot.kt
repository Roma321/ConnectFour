package com.example.inrow.bot

import com.example.inrow.game.GameViewModel

class RandomBot(field: Array<Array<Int>>, width: Int, height: Int) : Bot(field, width, height) {
    override fun getMove(): GameViewModel.Move {
        val move = getAllMoves().shuffled()[0]
        setBotMove(move.column, move.row)
        return move
    }
}