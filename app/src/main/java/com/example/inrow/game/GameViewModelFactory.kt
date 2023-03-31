package com.example.inrow.game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inrow.GameMode
import com.example.inrow.database.GameDatabaseDao

class GameViewModelFactory(
    private val height: Int = 8,
    private val width: Int = 8,
    private val mode: GameMode,
    val minutes: Int,
    val seconds: Int,
    val dao: GameDatabaseDao,
    val application: Application,
    val player1Name: String,
    val player2Name: String,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(
            height,
            width,
            mode,
            minutes,
            seconds,
            dao,
            application,
            player1Name,
            player2Name
        ) as T
    }
}