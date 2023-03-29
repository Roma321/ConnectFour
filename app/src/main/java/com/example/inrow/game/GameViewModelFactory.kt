package com.example.inrow.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(
    private val height: Int = 8,
    private val width: Int = 8,
    private val withBot: Boolean = true
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(height, width, withBot) as T
    }
}