package com.example.inrow.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inrow.R
import com.example.inrow.databinding.FragmentGameBinding


class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game, container, false
        )

        generateFields()
        return binding.root
    }

    private fun generateFields() { // TODO
        val args = GameFragmentArgs.fromBundle(requireArguments())
        binding.apply {
            field.columnCount = args.width
            field.rowCount = args.height
            for (i in 0 until args.width * args.height) {
                Log.e("","${args.height} ${args.width}")
                val button = Button(context)
                button.id = View.generateViewId()
                button.layoutParams = ViewGroup.LayoutParams(100, 100)
//                button.setTextAppearance(R.style.button_style)
                button.setBackgroundResource(R.drawable.simple_rectangle)
                button.text = "$i"
                field.addView(button)
            }
        }
    }


}