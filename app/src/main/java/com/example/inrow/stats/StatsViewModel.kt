package com.example.inrow.stats

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.*
import com.example.inrow.GameMode
import com.example.inrow.database.GameDatabaseDao
import com.example.inrow.database.GameRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

class StatsViewModel(dao: GameDatabaseDao, application: Application) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var _filteredGames = listOf<GameRecord>()
    private val _allGames = dao.getAll()
    private lateinit var allGames: List<GameRecord>
    var filteredGamesLiveData = MutableLiveData(listOf<GameRecord>())



    init {
        viewModelScope.launch {
            _allGames.asFlow().collect {
                allGames = it
            }
        }
    }


    private val _totalGames = MutableLiveData("")
    val totalGames: LiveData<String>
        get() = _totalGames

    private val _win1Games = MutableLiveData("")
    val win1games: LiveData<String>
        get() = _win1Games

    private val _win2Games = MutableLiveData("")
    val win2games: LiveData<String>
        get() = _win2Games

    private val _draws = MutableLiveData("")
    val draws: LiveData<String>
        get() = _draws

    private val _avgLength = MutableLiveData("")
    val avgLength: LiveData<String>
        get() = _avgLength

    private val _longestGame = MutableLiveData("")
    val longestGame: LiveData<String>
        get() = _longestGame

    private val _sizesStat = MutableLiveData("")
    val sizesStat: LiveData<String>
        get() = _sizesStat


    fun setAllGames() {
        _filteredGames = allGames
        filteredGamesLiveData.value = _filteredGames
        calcStat()
    }

    fun setTwoPlayersGames() {
        _filteredGames = allGames.filter { it.mode == GameMode.TWO_PLAYERS }
        filteredGamesLiveData.value = _filteredGames
        calcStat()
    }

    fun setRandomBotGames() {
        _filteredGames = allGames.filter { it.mode == GameMode.RANDOM_BOT }
        filteredGamesLiveData.value = _filteredGames
        calcStat()
    }

    fun setSmartBotGames() {
        _filteredGames = allGames.filter { it.mode == GameMode.SMART_BOT }
        filteredGamesLiveData.value = _filteredGames
        calcStat()
    }

    private fun calcStat() {
        _totalGames.value = _filteredGames.size.toString()
        if (_filteredGames.isEmpty()) {
            setNoData()
            return
        }
        val count1 = _filteredGames.count { it.result == 1 || it.result == 3 }
        _win1Games.value = "$count1 (${count1 * 100 / _filteredGames.size}%)"
        val count2 = _filteredGames.count { it.result == 2 || it.result == 4 }
        _win2Games.value = "$count2 (${count2 * 100 / _filteredGames.size}%)"
        val countDraws = _filteredGames.count { it.result == 0 }
        _draws.value = "$countDraws (${countDraws * 100 / _filteredGames.size}%)"

        val avgSeconds =
            _filteredGames.sumOf { it.totalLength() } / _filteredGames.size
        val avgMoves =
            _filteredGames.sumOf { it.movesCount }.toFloat() / _filteredGames.size
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        val avgLengthInfo =
            "${DateUtils.formatElapsedTime(avgSeconds.toLong())} сек.; ${df.format(avgMoves)} ходов"
        _avgLength.value = avgLengthInfo

        val mostSeconds = _filteredGames.maxBy { it.totalLength() }.totalLength()
        val mostMoves = _filteredGames.maxBy { it.movesCount }.movesCount
        val longestGameInfo =
            "${DateUtils.formatElapsedTime(mostSeconds.toLong())} сек. или $mostMoves ходов"
        _longestGame.value = longestGameInfo

        val a = _filteredGames.groupBy { "(${it.width}*${it.height})" }
        val b = a.map { Pair(it.key, it.value.size) }.sortedBy { it.second }
            .map { "${it.first}: ${it.second} игр" }
        _sizesStat.value = b.joinToString(";  ")

    }

    private fun setNoData() {
        val noGames = "Игр не было"
        _win1Games.value = noGames
        _win2Games.value = noGames
        _avgLength.value = noGames
        _draws.value = noGames
        _longestGame.value = noGames
        _sizesStat.value = noGames
    }


}