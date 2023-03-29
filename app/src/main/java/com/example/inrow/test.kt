package com.example.inrow

import androidx.lifecycle.MutableLiveData
import com.example.inrow.game.GameViewModel

fun main() {

    val a = MutableLiveData(5)
    val b = MutableLiveData(5)
    print(a == b)
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