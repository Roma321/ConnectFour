package com.example.inrow.configure

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.inrow.GameMode
import com.example.inrow.R
import com.example.inrow.databinding.FragmentConfigureBinding
import vadiole.colorpicker.ColorModel
import vadiole.colorpicker.ColorPickerDialog


class ConfigureGameFragment : Fragment() {

    private val spinnerFill = (5..17).toList()

    private lateinit var viewModel: ConfigureGameViewModel
    private lateinit var binding: FragmentConfigureBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        viewModel = ViewModelProvider(this)[ConfigureGameViewModel::class.java]
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_configure,
            container,
            false
        )


        binding.playButton.setOnClickListener {
            goToGame(it)
        }


        connectSpinners()
        connectSeiingSizeButtons()
        connectPlayersNamesEditTexts()
        connectTimeControlEditTexts()
        connectSizeDispatchLabel()
        connectColorPickers()
        connectModeRadioGroup()

        return binding.root

    }

    private fun connectModeRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonTwoPlayers -> viewModel.setTwoPlayersMode()
                R.id.radioButtonRandomBot -> viewModel.setRandomBotMode()
                R.id.radioButtonSmartBot -> viewModel.setSmartBotMode()
            }
        }

        viewModel.mode.observe(viewLifecycleOwner) {
            when (it) {
                GameMode.TWO_PLAYERS -> binding.radioGroup.check(R.id.radioButtonTwoPlayers)
                GameMode.RANDOM_BOT -> binding.radioGroup.check(R.id.radioButtonRandomBot)
                GameMode.SMART_BOT -> binding.radioGroup.check(R.id.radioButtonSmartBot)
            }
        }
    }

    private fun connectColorPickers() {
        viewModel.color1.observe(viewLifecycleOwner) {
            binding.firstPlayerColor.setBackgroundColor(it)
            Log.e("1", it.toString())
        }
        viewModel.color2.observe(viewLifecycleOwner) {
            binding.secondPlayerColor.setBackgroundColor(it)
            Log.e("2", it.toString())
        }


        binding.firstPlayerColor.setOnClickListener {
            //  create dialog
            val colorPicker: ColorPickerDialog = ColorPickerDialog.Builder()
                .setInitialColor(viewModel.color1.value!!)
                .setColorModel(ColorModel.RGB)
                .setColorModelSwitchEnabled(false)
                .setButtonOkText(android.R.string.ok)
                .setButtonCancelText(android.R.string.cancel)
                .onColorSelected { color: Int ->
                    viewModel.updatePlayer1Color(color)
                }
                .create()
            colorPicker.show(childFragmentManager, "color_picker")
        }

        binding.secondPlayerColor.setOnClickListener {
            //  create dialog
            val colorPicker: ColorPickerDialog = ColorPickerDialog.Builder()
                .setInitialColor(viewModel.color2.value!!)
                .setColorModel(ColorModel.RGB)
                .setColorModelSwitchEnabled(false)
                .setButtonOkText(android.R.string.ok)
                .setButtonCancelText(android.R.string.cancel)
                .onColorSelected { color: Int ->
                    viewModel.updatePlayer2Color(color)
                }
                .create()
            colorPicker.show(childFragmentManager, "color_picker")
        }
    }

    private fun connectSizeDispatchLabel() {
        viewModel.height.observe(viewLifecycleOwner) {
            setSizeLabelText()
        }

        viewModel.width.observe(viewLifecycleOwner) {
            setSizeLabelText()
        }

        setSizeLabelText()
    }

    private fun connectPlayersNamesEditTexts() {
        binding.editTextNamePlayer1.doAfterTextChanged {
            viewModel.updatePlayer1Name(it.toString())
        }

        binding.editTextNamePlayer2.doAfterTextChanged {
            viewModel.updatePlayer2Name(it.toString())
        }

        viewModel.playerNames.observe(viewLifecycleOwner) {
            setPlayersNames()
        }

        binding.reloadImageView.setOnClickListener {
            viewModel.setRandomPlayerNames()
        }

        setPlayersNames()

    }

    private fun connectTimeControlEditTexts() {
        binding.editTextMinutes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    viewModel.setMinutes(s!!.toString().toInt())
                } catch (_: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.editTextSeconds.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    viewModel.setSeconds(s!!.toString().toInt())
                } catch (_: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        viewModel.seconds.observe(viewLifecycleOwner) {
            binding.editTextSeconds.setText(it.toString())
        }

        viewModel.minutes.observe(viewLifecycleOwner) {
            binding.editTextMinutes.setText(it.toString())
        }
    }

    private fun connectSeiingSizeButtons() {
        binding.setCustomSizeButton.setOnClickListener(::setCustomSizeOnClickListener)

        binding.setSmallFieldButton.setOnClickListener {
            viewModel.setSizes(7, 6)
        }

        binding.setMediumFieldButton.setOnClickListener {
            viewModel.setSizes(8, 8)
        }

        binding.setBigFieldButton.setOnClickListener {
            viewModel.setSizes(10, 10)
        }
    }

    private fun connectSpinners() {
        val widthArrayAdapter: ArrayAdapter<Int> = ArrayAdapter<Int>(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            spinnerFill
        )

        val heightArrayAdapter: ArrayAdapter<Int> = ArrayAdapter<Int>(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            spinnerFill
        )


        binding.widthSpinner.adapter = widthArrayAdapter
        binding.widthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                viewModel.setSizes(newWidth = 5 + pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        binding.heightSpinner.adapter = heightArrayAdapter
        binding.heightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                viewModel.setSizes(newHeight = 5 + pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }


    private fun setPlayersNames() {
        binding.editTextNamePlayer1.setText(viewModel.playerNames.value!![0])
        binding.editTextNamePlayer2.setText(viewModel.playerNames.value!![1])
    }

    private fun goToGame(it: View) {
        Navigation.findNavController(it).navigate(
            ConfigureGameFragmentDirections.actionStartFragmentToGameFragment(
                width = viewModel.width.value!!,
                height = viewModel.height.value!!,
                player1Name = viewModel.playerNames.value!![0],
                player2Name = viewModel.playerNames.value!![1],
                color1 = viewModel.color1.value!!,
                color2 = viewModel.color2.value!!,
                mode = viewModel.mode.value!!,
                minutes = viewModel.minutes.value!!,
                seconds = viewModel.seconds.value!!,
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
        binding.tvFieldSize.text =
            "Укажите размер поля. Выбрано: ${viewModel.width.value}x${viewModel.height.value}"
    }


}