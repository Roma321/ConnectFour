package com.example.inrow.start

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class StartViewModel : ViewModel() {
    private var _width = MutableLiveData(5)
    val width: LiveData<Int>
        get() = _width
    private var _height = MutableLiveData(5)
    val height: LiveData<Int>
        get() = _height
    private var _playerNames = MutableLiveData(mutableListOf("a", "b"))
    val playerNames: LiveData<MutableList<String>>
        get() = _playerNames
    private var _color1 = MutableLiveData(Color.rgb(187, 255, 144))
    val color1: LiveData<Int>
        get() = _color1
    private var _color2 = MutableLiveData(Color.rgb(134, 120, 255))
    val color2: LiveData<Int>
        get() = _color2
    private var _useBot = MutableLiveData(false)
    val useBot: LiveData<Boolean>
        get() = _useBot

    private val players = listOf(
        listOf("Крош", "Ёжик"),
        listOf("Биба", "Боба"),
        listOf("Онегин", "Ленский"),
        listOf("Пупа", "Лупа"),
        listOf("Бонни", "Клайд"),
        listOf("Чип", "Дейл"),
        listOf("Кетчуп", "Майонез"),
        listOf("Сталин", "Ленин"),
        listOf("Чук", "Гек"),
        listOf("Вупсень", "Пупсень"),
        listOf("Маркс", "Энгельс"),
        listOf("Том", "Джерри"),
        listOf("Тимон", "Пумба"),
        listOf("Акуна", "Матата"),
        listOf("Лило", "Стич"),
        listOf("Шрек", "Осёл"),
        listOf("Астерикс", "Обеликс"),
        listOf("Майк", "Салли"),
        listOf("Малыш", "Карлсон"),
        listOf("Салтыков", "Щедрин"),
        listOf("Римский", "Корсаков"),
        listOf("Интеграл", "Производная"),
        listOf("Первокурсник", "Демидович"),
        listOf("Пифагор", "Евклид"),
        listOf("Орёл", "Прометей"),
        listOf("Ахилл", "Гектор"),
        listOf("Дарвин", "Бог"),
        listOf("Мистер Икс", "Мистер Игрек"),
        listOf("Грека", "Рак"),
        listOf("Илья Муромец", "Алеша Попович"),
        listOf("Иванушка-дурачок", "Иван-дурак"),
        listOf("Гарри Поттер", "Лорд Волан-де-Морт"),
        listOf("Гарри Поттер", "Порри Гаттер"),
    )

    init {
        setRandomPlayerNames()
    }

    fun setRandomPlayerNames() {
        _playerNames.value = players.random().shuffled().toMutableList()
    }

    fun setSizes(newWidth: Int = width.value!!, newHeight: Int = height.value!!) {
        _width.value = newWidth
        _height.value = newHeight
    }

    private fun List<List<String>>.random(): List<String> {
        return this[Random.nextInt(0, this.size)]
    }

    fun updatePlayer1Name(string: String) {
        _playerNames.value?.set(0, string)
    }

    fun updatePlayer2Name(string: String) {
        _playerNames.value?.set(1, string)
    }

    fun updatePlayer1Color(color: Int) {
        _color1.value = color
    }

    fun updatePlayer2Color(color: Int) {
        _color2.value = color
    }
}