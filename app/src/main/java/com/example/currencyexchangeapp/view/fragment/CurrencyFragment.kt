package com.example.currencyexchangeapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.databinding.FragmentCurencyBinding
import com.example.currencyexchangeapp.domain.model.Resource
import com.example.currencyexchangeapp.view.adapter.CurrencyAdapter
import com.example.currencyexchangeapp.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CurrencyFragment : Fragment() {

    private val viewModel: CurrencyViewModel by viewModels()
    private lateinit var binding: FragmentCurencyBinding
    private var currencyAdapter: CurrencyAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        collectCurrencies()
        getCurrencies()
        initBinding()
        initRecyclerView()
        return binding.root
    }

    private fun initBinding(){
        binding = FragmentCurencyBinding.inflate(layoutInflater)
    }

    private fun initRecyclerView() {
        currencyAdapter = CurrencyAdapter()
        val llm = LinearLayoutManager(activity?.baseContext)
        llm.orientation = RecyclerView.VERTICAL
        binding.recyclerView.adapter = currencyAdapter
        binding.recyclerView.layoutManager = llm
    }

    private fun getCurrencies() = viewModel.getLatestCurrency()

    private fun collectCurrencies() {
        lifecycleScope.launch {
            viewModel.response.collect {
                when (it) {
                    is Resource.Success -> {
                        Timber.d("Success: ${it.data.ratesList}")
                        currencyAdapter?.addDataToAdapter(it.data.ratesList)
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