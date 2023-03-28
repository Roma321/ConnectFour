package com.example.inrow

import Game
import kotlin.random.Random

fun main() {
    val game = Game()
    game.print()
//    game.onCellClicked(4, 0)
//    game.onCellClicked(4, 1)
//    game.onCellClicked(7,0)
//    game.onCellClicked(6,0)
//    game.onCellClicked(7,1)
//    game.onCellClicked(6,1)
//    game.onCellClicked(7,2)
//    game.onCellClicked(6,2)
//    game.onCellClicked(7,3)

    game.onCellClicked(4, 0)
    game.onCellClicked(3, 0)

    game.onCellClicked(3, 1)
    game.onCellClicked(2, 0)

    game.onCellClicked(1, 0)
    game.onCellClicked(2, 1)

    game.onCellClicked(2, 2)
    game.onCellClicked(1, 1)

    game.onCellClicked(1, 2)
    game.onCellClicked(6, 0)

    game.onCellClicked(1, 3)


    game.print()
}