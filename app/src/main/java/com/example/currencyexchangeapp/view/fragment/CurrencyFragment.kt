package com.example.currencyexchangeapp.view.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.databinding.FragmentCurencyBinding
import com.example.currencyexchangeapp.db.dao.ICurrencyDAO
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.domain.model.states.Resource
import com.example.currencyexchangeapp.view.adapter.CurrencyAdapter
import com.example.currencyexchangeapp.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
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
        collectCurrenciesFromDB()
        collectCurrencies()
        initBinding()
        initRecyclerView()
        getCurrencies()
        return binding.root
    }

    private fun initBinding() {
        binding = FragmentCurencyBinding.inflate(layoutInflater)
    }

    private fun createAdapterDelegateImp() = object : ICurrencyDAO {
        override fun getFavouriteCurrency(): Flow<List<Rates>> {
            TODO("Not yet implemented")
        }

        override fun insertFavouriteRate(entity: Rates) =
            viewModel.insertRateToFavourites(entity)

        override fun deleteFavouriteCurrency(entity: Rates) =
            viewModel.deleteRateFromFavourites(entity)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initRecyclerView() {
        currencyAdapter = CurrencyAdapter(createAdapterDelegateImp())
        val llm = LinearLayoutManager(activity?.baseContext)
        llm.orientation = RecyclerView.VERTICAL
        binding.recyclerView.adapter = currencyAdapter
        binding.recyclerView.layoutManager = llm
    }

    private fun getCurrencies() = viewModel.getLatestCurrency()

    private fun collectCurrenciesFromDB() = viewModel.favouriteCurrency.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

    private fun collectCurrencies() {
        lifecycleScope.launch {
            viewModel.response.collect {
                when (it) {
                    is Resource.Success -> {
                        Timber.d("Success: ${it.data.ratesList}")
                        viewModel.favouriteCurrency.collect{ fav ->
                            Timber.d("Success: $fav")
                            currencyAdapter?.addDataToAdapter(it.data.ratesList, fav.toMutableList())
                            currencyAdapter?.notifyDataSetChanged()
                        }
                    }
                    is Resource.Progress -> {
                        Timber.d("Progress")
                    }
                    is Resource.Error -> {
                        Timber.d("Error: ${it.errorData}")
                    }
                }
            }
        }
    }
}