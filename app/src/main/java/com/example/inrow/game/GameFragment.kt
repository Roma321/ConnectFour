package com.example.inrow.game

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
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
            inflater,
            R.layout.fragment_game,
            container,
            false
        )


        val args = GameFragmentArgs.fromBundle(requireArguments())
        Toast.makeText(
            context,
            "Вы хотите сыграть с полем размером ${args.height}*${args.width}",
            Toast.LENGTH_LONG
        ).show()


        binding.field.columnCount = 4
        binding.field.rowCount = 4

        val grid = GridLayout(context)

        for (i in 1..16){
            val button = Button(activity)
            button.text = "Boton: " + i
            val param = GridLayout.LayoutParams(
                GridLayout.spec(
                    GridLayout.UNDEFINED, GridLayout.FILL, 1f
                ),
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            )

            button.layoutParams = param
            grid.addView(button)
        }

        binding.lin.addView(grid)


//        for (i in 1..10) {
//            val tv = TextView(activity)
//            tv.text = i.toString()
//            tv.layoutParams = ViewGroup.LayoutParams(50, 50)
//            binding.lin.addView(tv)
//        }

//        val grid = binding.field
//        binding.field.columnCount = args.height + 5
//        binding.field.rowCount = args.width + 5
//        binding.field.childCount
//        for (i in 1..args.width * args.height) {
//            val titleText = TextView(context)
//            titleText.text = i.toString()
//            titleText.layoutParams = ViewGroup.LayoutParams(150,150)
//            titleText.setTextColor(Color.GREEN)
//            titleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
////            val param: GridLayout.LayoutParams = GridLayout.LayoutParams()
////            param.height = 100
////            param.width = 100
////            param.rightMargin = 5
////            param.topMargin = 5
////            param.setGravity(Gravity.CENTER)
////            param.columnSpec = GridLayout.spec(1)
////            param.rowSpec = GridLayout.spec(1)
////            titleText.layoutParams = param
//
//            val rowSpan: GridLayout.Spec = GridLayout.spec(GridLayout.UNDEFINED, 1)
//            val colspan: GridLayout.Spec = GridLayout.spec(GridLayout.UNDEFINED, 1)
//            val gridParam = GridLayout.LayoutParams(
//                rowSpan, colspan
//            )
//            binding.field.addView(titleText, gridParam)
//
//        }
//
//        Log.e("d", binding.field.childCount.toString())
//        Log.e("d", binding.field.children.toList().map { it as TextView }.map { it.text }.toString())
//


        return inflater.inflate(R.layout.fragment_game, container, false)
    }


}