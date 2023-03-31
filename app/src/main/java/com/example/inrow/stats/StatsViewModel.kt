package com.example.inrow.stats

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inrow.database.GameDatabaseDao
import com.example.inrow.database.GameRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class StatsViewModel(dao: GameDatabaseDao, application: Application) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val allGames = dao.getAll()

}