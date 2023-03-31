package com.example.inrow.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.inrow.R
import com.example.inrow.databinding.FragmentGameBinding
import com.example.inrow.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        binding.buttonToGameFragment.setOnClickListener {
            Navigation.findNavController(it).navigate(
                StartFragmentDirections.actionStartFragmentToConfigureGameFragment()
            )
        }
        binding.buttonToStatsFragment.setOnClickListener {
            Navigation.findNavController(it).navigate(
                StartFragmentDirections.actionStartFragmentToStatsFragment()
            )
        }
        return binding.root
    }

}