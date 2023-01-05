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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.R
import com.example.currencyexchangeapp.databinding.FragmentCurencyBinding
import com.example.currencyexchangeapp.db.dao.ICurrencyDAO
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.domain.model.states.Resource
import com.example.currencyexchangeapp.extension.onSearchTextChanged
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
    private var menu: MenuHost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMenu()
        initMenuProvider()
    }

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

    private fun initMenu() {
        menu = requireActivity()
    }

    private fun createAdapterDelegateImp() = object : ICurrencyDAO {
        override fun getFavouriteCurrency(): Flow<List<Rates>> =
            viewModel.favouriteCurrency

        override fun insertFavouriteRate(entity: Rates) =
            viewModel.insertRateToFavourites(entity)

        override fun deleteFavouriteCurrency(entity: Rates) =
            viewModel.deleteRateFromFavourites(entity)
    }

    private fun MutableList<Rates>.filterRatesByName(text: String): MutableList<Rates> =
        this.filter { rate -> rate.rateName.startsWith(text) }.toMutableList()

    private fun initMenuProvider() {
        Timber.d("$menu")
        menu?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                Timber.d("$menu")
                return when (menuItem.itemId) {
                    R.id.searchItem -> {
                        (menuItem.actionView as SearchView).onSearchTextChanged {
                            val searchResult =
                                currencyAdapter?.rateList?.filterRatesByName(it) ?: mutableListOf()
                            val favouriteSearchResult =
                                currencyAdapter?.favouriteRateList?.filterRatesByName(it)
                                    ?: mutableListOf()
                            Timber.d("*** $menu")

                            currencyAdapter?.searchData(
                                searchResult,
                                favouriteSearchResult
                            )
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

    private fun getCurrencies() = viewModel.getLatestCurrency()

    private fun collectCurrenciesFromDB() = viewModel
        .favouriteCurrency
        .flowWithLifecycle(lifecycle)
        .launchIn(lifecycleScope)

    private fun collectCurrencies() {
        lifecycleScope.launch {
            viewModel.response.collect {
                when (it) {
                    is Resource.Success -> {
                        Timber.d("Success: ${it.data.ratesList}")
                        viewModel.favouriteCurrency.collect { fav ->
                            Timber.d("Success: $fav")
                            currencyAdapter?.addDataToAdapter(
                                it.data.ratesList,
                                fav.toMutableList()
                            )
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