package com.example.inrow.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

//        viewModel.allGames.observe(viewLifecycleOwner){
//            binding.sizeStatTv.text = it.toString()
//        }


        connectSpinner()

        connectStats()

        return binding.root
    }

    private fun connectStats() {
        viewModel.sizesStat.observe(viewLifecycleOwner) {
            binding.sizeStatTv.text = it
        }
        viewModel.avgLength.observe(viewLifecycleOwner) {
            binding.avgGameTimeTv.text = it
        }
        viewModel.draws.observe(viewLifecycleOwner) {
            binding.drawsTv.text = it
        }
        viewModel.longestGame.observe(viewLifecycleOwner) {
            binding.longestGameTv.text = it
        }
        viewModel.totalGames.observe(viewLifecycleOwner) {
            binding.totalGamesTv.text = it
        }
        viewModel.win1games.observe(viewLifecycleOwner) {
            binding.wins1Tv.text = it
        }
        viewModel.win2games.observe(viewLifecycleOwner) {
            binding.wins2Tv.text = it
        }
    }

    private fun connectSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.stats_select_mode_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinner.adapter = adapter
        }
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                when (pos) {
                    0 -> viewModel.setAllGames()
                    1 -> viewModel.setTwoPlayersGames()
                    2 -> viewModel.setRandomBotGames()
                    3 -> viewModel.setSmartBotGames()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

}