package com.example.inrow.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inrow.GameMode

class GameViewModelFactory(
    private val height: Int = 8,
    private val width: Int = 8,
    private val mode: GameMode,
    val minutes: Int,
    val seconds: Int
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(height, width, mode, minutes, seconds) as T
    }
}