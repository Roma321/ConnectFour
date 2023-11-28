package com.example.inrow.funcBot

import com.example.inrow.game.GameViewModel

fun getRandomMove(field: Array<Array<Int>>): GameViewModel.Move {
    return possibleMoves(field).shuffled()[0]
}