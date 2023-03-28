package com.example.inrow.game

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.inrow.R

class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        val args = GameFragmentArgs.fromBundle(requireArguments())
        Toast.makeText(context, "Вы хотите сыграть с полем размером ${args.height}*${args.width}", Toast.LENGTH_LONG).show()
        return inflater.inflate(R.layout.fragment_game, container, false)
    }


}