package com.example.inrow.stats

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inrow.database.GameDatabaseDao

class StatsViewModelFactory(
    private val dao: GameDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatsViewModel(dao, application) as T
    }
}