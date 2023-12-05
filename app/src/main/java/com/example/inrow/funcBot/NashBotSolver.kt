package com.example.inrow.funcBot

import com.example.inrow.bot.NashItem
import com.example.inrow.game.GameViewModel

fun getNashBotMove(field: Array<Array<Int>>): GameViewModel.Move {
    val n = NashItem(field, null, 1, 2, 1)
    return n.bestMove().first.orEmpty()[0]!!;
}