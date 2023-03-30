package com.example.inrow.game

import android.app.AlertDialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
        viewModelFactory = GameViewModelFactory(args.height, args.width, args.useBot)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]


        val width = getScreenWidth() / args.width
        val height =
            if (getScreenHeight() * 2 / 3 < args.height * width) getScreenHeight() * 2 / 3 / args.height else width
        viewModel.win.observe(viewLifecycleOwner) { win ->
            if (win != 0) {
                val dialog = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setTitle("Победа!")
                        .setMessage("Победил игрок $win (${if (win == 1) args.player1Name else args.player2Name})")
                        .setPositiveButton("УРА!") { dialog, _ ->
                            dialog.cancel()
//                            goBack()
                        }
                        .setCancelable(false)
                    builder.create()
                } ?: throw IllegalStateException("Activity cannot be null")
                dialog.show()
            }
        }

        viewModel.movesCount.observe(viewLifecycleOwner){ count ->
            if (count == viewModel.height * viewModel.width){
                val dialog = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setTitle("Ничья!")
                        .setMessage("Никто не выиграл, никто не проиграл. В целом, нормально")
                        .setPositiveButton("ОК") { dialog, _ ->
                            dialog.cancel()
//                            goBack()
                        }
                        .setCancelable(false)
                    builder.create()
                } ?: throw IllegalStateException("Activity cannot be null")
                dialog.show()
            }
        }

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
                viewModel.field[args.height - 1 - i / args.width][i % args.width].observe(
                    viewLifecycleOwner
                ) {
                    when (it) {
                        1 -> button.setBackgroundColor(args.color1)
                        2 -> button.setBackgroundColor(args.color2)
                    }
                }
                field.addView(button)
            }

            player1Tv.text = args.player1Name
            player2Tv.text = args.player2Name
            color1Tv.setBackgroundColor(args.color1)
            color2Tv.setBackgroundColor(args.color2)
        }



        return binding.root
    }

    private fun goBack(){
        val navController = this.findNavController()
        navController.navigateUp()
    }


    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

}