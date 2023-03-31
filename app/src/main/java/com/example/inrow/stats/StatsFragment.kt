package com.example.inrow.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inrow.R
import com.example.inrow.database.GameDatabase
import com.example.inrow.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private lateinit var viewModel: StatsViewModel
    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_stats, container, false
        )
        val application = requireActivity().application
        val dao = GameDatabase.getInstance(application).getGameDatabaseDao()
        val viewModelFactory = StatsViewModelFactory(dao, application)

        viewModel = ViewModelProvider(this, viewModelFactory)[StatsViewModel::class.java]

        viewModel.allGames.observe(viewLifecycleOwner){
            binding.ttttt.text = it.toString()
        }
        return binding.root
    }

}