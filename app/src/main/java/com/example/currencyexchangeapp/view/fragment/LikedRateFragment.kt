package com.example.currencyexchangeapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.currencyexchangeapp.databinding.FragmentLikedRateBinding
import com.example.currencyexchangeapp.view.activity.BaseRateActivity

class LikedRateFragment : Fragment() {

    lateinit var binding: FragmentLikedRateBinding

    override fun onResume() {
        super.onResume()
        showBottomBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()
        return binding.root
    }

    private fun initBinding(){
        binding = FragmentLikedRateBinding.inflate(layoutInflater)
    }

    private fun showBottomBar(){
        (activity as BaseRateActivity).showBottomBar()
    }
}