package com.example.inrow

import java.text.SimpleDateFormat
import java.util.*

fun main() {
    println(getCurrentDateTime())
}
//    val game = GameViewModel()
//    game.print()
////    game.onCellClicked(4, 0)
////    game.onCellClicked(4, 1)
////    game.onCellClicked(7,0)
////    game.onCellClicked(6,0)
////    game.onCellClicked(7,1)
////    game.onCellClicked(6,1)
////    game.onCellClicked(7,2)
////    game.onCellClicked(6,2)
////    game.onCellClicked(7,3)
//
//    game.onCellClicked(4, 0)
//    game.onCellClicked(3, 0)
//
//    game.onCellClicked(3, 1)
//    game.onCellClicked(2, 0)
//
//    game.onCellClicked(1, 0)
//    game.onCellClicked(2, 1)
//
//    game.onCellClicked(2, 2)
//    game.onCellClicked(1, 1)
//
//    game.onCellClicked(1, 2)
//    game.onCellClicked(6, 0)
//
//    game.onCellClicked(1, 3)
//
//
//    game.print()
//}

private fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd:HH-mm-ss")
    return sdf.format(Date())
}