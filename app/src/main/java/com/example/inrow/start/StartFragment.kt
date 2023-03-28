package com.example.inrow.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.inrow.R
import com.example.inrow.databinding.FragmentStartBinding
import kotlin.random.Random


class StartFragment : Fragment() {

    private lateinit var viewModel: StartViewModel
    private lateinit var binding: FragmentStartBinding
    private var width = 5
    private var height = 5

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
        listOf("Инь", "Янь"),
        listOf("Салтыков", "Щедрин"),
        listOf("Римский", "Корсаков"),
        listOf("Интеграл", "Производная"),
        listOf("Первокурсник", "Демидович"),
        listOf("Пифагор", "Евклид"),
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[StartViewModel::class.java]
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_start,
            container,
            false
        )


        binding.playButton.setOnClickListener {
            goToGame(it)
        }

        val widthArrayAdapter: ArrayAdapter<Int> = ArrayAdapter<Int>(
            activity!!.applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            (5..12).toList()
        )

        val heightArrayAdapter: ArrayAdapter<Int> = ArrayAdapter<Int>(
            activity!!.applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            (5..12).toList()
        )

        binding.setCustomSizeButton.setOnClickListener(::setCustomSizeOnClickListener)
        binding.widthSpinner.adapter = widthArrayAdapter
        binding.widthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                setSizes(newWidth = width + pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        binding.heightSpinner.adapter = heightArrayAdapter
        binding.heightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                setSizes(newHeight = height + pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        binding.setSmallFieldButton.setOnClickListener {
            setSizes(7, 6)
        }

        binding.setMediumFieldButton.setOnClickListener {
            setSizes(8, 8)
        }

        binding.setBigFieldButton.setOnClickListener {
            setSizes(10, 10)
        }

        setRandomPlayerNames()

        binding.reloadImageView.setOnClickListener {
            setRandomPlayerNames()
        }

        return binding.root

    }

    private fun setRandomPlayerNames() {
        val a = players.random().shuffled()
        binding.editTextNamePlayer1.setText(a[0])
        binding.editTextNamePlayer2.setText(a[1])
    }

    private fun goToGame(it: View) {
        Navigation.findNavController(it).navigate(
            StartFragmentDirections.actionStartFragmentToGameFragment(
                width = width,
                height = height
            )
        )
    }

    fun setCustomSizeOnClickListener(view: View) {
        setSizes(5, 5)
        binding.linearLayoutStandardSizeButtons.visibility = View.GONE
        binding.linearLayoutSetCustomSizeElements.visibility = View.VISIBLE
        binding.setCustomSizeButton.text = "Выбрать стандартный размер"
        binding.setCustomSizeButton.setOnClickListener(::setStandardSizeOnClickListener)
    }

    fun setStandardSizeOnClickListener(view: View) {
        binding.linearLayoutStandardSizeButtons.visibility = View.VISIBLE
        binding.linearLayoutSetCustomSizeElements.visibility = View.GONE
        binding.setCustomSizeButton.text = "Настроить свой размер"
        binding.setCustomSizeButton.setOnClickListener(::setCustomSizeOnClickListener)
    }

    private fun setSizes(newWidth: Int = width, newHeight: Int = height) {
        width = newWidth
        height = newHeight
        binding.tvFieldSize.text = "Укажите размер поля. Выбрано: ${width}x${height}"
    }

    private fun List<List<String>>.random(): List<String> {
        return this[Random.nextInt(0, this.size)]
    }

}