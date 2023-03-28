package com.example.inrow.start

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
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
                viewModel.setSizes(newWidth = viewModel.width.value!! + pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        binding.heightSpinner.adapter = heightArrayAdapter
        binding.heightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                viewModel.setSizes(newHeight = viewModel.height.value!! + pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        binding.setSmallFieldButton.setOnClickListener {
            viewModel.setSizes(7, 6)
        }

        binding.setMediumFieldButton.setOnClickListener {
            viewModel.setSizes(8, 8)
        }

        binding.setBigFieldButton.setOnClickListener {
            viewModel.setSizes(10, 10)
        }

        binding.editTextNamePlayer1.doAfterTextChanged {
            viewModel.updatePlayer1(it.toString())
        }

        binding.editTextNamePlayer2.doAfterTextChanged {
            viewModel.updatePlayer2(it.toString())
        }


        viewModel.height.observe(viewLifecycleOwner) {
            setSizeLabelText()
        }

        viewModel.width.observe(viewLifecycleOwner) {
            setSizeLabelText()
        }

        viewModel.playerNames.observe(viewLifecycleOwner) {
            setPlayersNames()
        }

        binding.reloadImageView.setOnClickListener {
            viewModel.setRandomPlayerNames()
        }

        setPlayersNames()
        setSizeLabelText()
        return binding.root

    }



    private fun setPlayersNames() {
        binding.editTextNamePlayer1.setText(viewModel.playerNames.value!![0])
        binding.editTextNamePlayer2.setText(viewModel.playerNames.value!![1])
    }

    private fun goToGame(it: View) {
        Navigation.findNavController(it).navigate(
            StartFragmentDirections.actionStartFragmentToGameFragment(
                width = viewModel.width.value!!,
                height = viewModel.height.value!!
            )
        )
    }

    fun setCustomSizeOnClickListener(view: View) {
        viewModel.setSizes(5, 5)
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



    private fun setSizeLabelText() {
        binding.tvFieldSize.text = "Укажите размер поля. Выбрано: ${viewModel.width.value}x${viewModel.height.value}"
    }



}