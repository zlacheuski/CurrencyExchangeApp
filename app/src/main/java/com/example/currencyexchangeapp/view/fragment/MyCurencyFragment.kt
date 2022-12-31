package com.example.currencyexchangeapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.currencyexchangeapp.databinding.FragmentMyCurencyBinding

class MyCurencyFragment : Fragment() {

    lateinit var binding: FragmentMyCurencyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()
        return binding.root
    }

    private fun initBinding(){
        binding = FragmentMyCurencyBinding.inflate(layoutInflater)
    }
}