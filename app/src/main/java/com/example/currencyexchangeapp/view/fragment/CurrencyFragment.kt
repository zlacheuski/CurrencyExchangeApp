package com.example.currencyexchangeapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.currencyexchangeapp.databinding.FragmentCurencyBinding
import com.example.currencyexchangeapp.domain.model.Resource
import com.example.currencyexchangeapp.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CurrencyFragment : Fragment() {

    private val viewModel: CurrencyViewModel by viewModels()
    lateinit var binding: FragmentCurencyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        collectUserDetailsData()
        viewModel.getLatestCurrency()
        initBinding()
        return binding.root
    }

    private fun initBinding(){
        binding = FragmentCurencyBinding.inflate(layoutInflater)
    }

    private fun collectUserDetailsData() {
        lifecycleScope.launch {
            viewModel.response.collect {
                when (it) {
                    is Resource.Success -> {
                        Timber.d("Success: ${it.data}")
                    }
                    is Resource.Progress -> {
                        Timber.d("Progress")
                    }
                    is Resource.Error -> {
                        Timber.d("Error: ${ it.errorData }")
                    }
                }
            }
        }
    }
}