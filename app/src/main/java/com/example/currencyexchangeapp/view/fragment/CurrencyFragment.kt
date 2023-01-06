package com.example.currencyexchangeapp.view.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.R
import com.example.currencyexchangeapp.databinding.FragmentCurencyBinding
import com.example.currencyexchangeapp.db.dao.ICurrencyDAO
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.domain.model.states.Resource
import com.example.currencyexchangeapp.extension.onSearchTextChanged
import com.example.currencyexchangeapp.view.adapter.CurrencyAdapter
import com.example.currencyexchangeapp.viewmodel.RateViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@AndroidEntryPoint
class CurrencyFragment : Fragment() {

    private val viewModel: RateViewModel by viewModels()
    private lateinit var binding: FragmentCurencyBinding
    private var currencyAdapter: CurrencyAdapter? = null
    private var menuHost: MenuHost? = null
    private var menuItem: MenuItem? = null
    private val ratesList: MutableList<Rates> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMenu()
        initMenuProvider()
        collectCurrencies()
        collectCurrenciesFromDB()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()
        initRecyclerView()
        getRatesFromDB()
        return binding.root
    }

    private fun initBinding() {
        binding = FragmentCurencyBinding.inflate(layoutInflater)
    }

    private fun initMenu() {
        menuHost = requireActivity()
    }

    private fun createAdapterDelegateImp() = object : ICurrencyDAO {

        override fun getRates(): Flow<List<Rates>> = viewModel.dbRates

        override fun getRatesNotFlow(): List<Rates> = viewModel.getRatesDB()

        override fun updateRateState(rateName: String, isLiked: Boolean) {
            viewModel.updateRateState(rateName, isLiked)
            menuItem?.collapseActionView()
        }
        override fun getRatesByName(searchQuery: String): List<Rates> =
            viewModel.getRatesByName(searchQuery)

        override fun insertRate(ratesList: List<Rates>) = viewModel.insertRates(ratesList)

        override fun updateRates(ratesList: List<Rates>) =
            viewModel.updateRates(ratesList)
    }

    private fun searchCurrency(query: String) {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.getRatesByName(query).let { allRates ->
                withContext(Dispatchers.Main) {
                    currencyAdapter?.addDataToAdapter(allRates.map {
                        Rates(it.rateName, it.rateValue, it.isLiked)
                    })
                }
            }
        }
    }

    private fun initMenuProvider() {
        menuHost?.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.searchItem -> {
                        this@CurrencyFragment.menuItem = menuItem
                        (menuItem.actionView as SearchView).onSearchTextChanged {
                            searchCurrency(query = it)
                        }
                        true
                    }
                    else -> true
                }
            }
        })
    }

    private fun initRecyclerView() {
        currencyAdapter = CurrencyAdapter(createAdapterDelegateImp())
        val llm = LinearLayoutManager(activity?.baseContext)
        llm.orientation = RecyclerView.VERTICAL
        binding.recyclerView.adapter = currencyAdapter
        binding.recyclerView.layoutManager = llm
    }

    private fun getRates() = viewModel.getRates()

    private fun getRatesFromDB() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.getRatesDB().let { rates ->
                ratesList.addAll(rates)
                getRates()
            }
        }
    }

    private fun collectCurrenciesFromDB() = viewModel
        .dbRates
        .onEach {
            currencyAdapter?.addDataToAdapter(it)
        }
        .flowWithLifecycle(lifecycle)
        .launchIn(lifecycleScope)

    private fun collectCurrencies() {
        lifecycleScope.launch {
            viewModel.response.collect { state ->
                when (state) {
                    is Resource.Success -> {
                        Timber.d("Success: ${state.data.ratesList}")
                        if (ratesList.isEmpty()) {
                            viewModel.insertRates(state.data.ratesList)
                        } else {
                            viewModel.updateRates(state.data.ratesList)
                            ratesList.filter { it.isLiked }.forEach {
                                viewModel.updateRateState(it.rateName, true)
                            }
                        }
                    }
                    is Resource.Progress -> {
                        Timber.d("Progress")
                    }
                    is Resource.Error -> {
                        Timber.d("Error: ${state.errorData}")
                    }
                }
            }
        }
    }
}