package com.example.inrow.game

import android.content.res.Resources
import android.os.Bundle
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
    private lateinit var viewModelFactory: GameViewModelFactory
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game, container, false
        )

        val args = GameFragmentArgs.fromBundle(requireArguments())
        viewModelFactory = GameViewModelFactory(args.height, args.width, true)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]


        val width = getScreenWidth() / args.width
        val height =
            if (getScreenHeight() * 2 / 3 < args.height * width) getScreenHeight() * 2 / 3 / args.height else width

        binding.apply {
            field.columnCount = args.width
            field.rowCount = args.height
            for (i in 0 until args.width * args.height) {
                val button = Button(context)
                button.layoutParams = ViewGroup.LayoutParams(width, height)
                button.setBackgroundResource(R.drawable.empty_square)
                button.text = "$i"
                button.setOnClickListener {
                    viewModel.onCellClicked(i % args.width, args.height - 1 - i / args.width)
                }
                field.addView(button)
            }
        }

        return binding.root
    }


    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

}