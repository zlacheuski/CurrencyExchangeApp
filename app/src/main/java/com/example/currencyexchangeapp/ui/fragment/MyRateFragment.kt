package com.example.currencyexchangeapp.ui.fragment

import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.R
import com.example.currencyexchangeapp.databinding.FragmentLikedRateBinding
import com.example.currencyexchangeapp.extension.onSearchTextChanged
import com.example.currencyexchangeapp.ui.adapter.ItemTouchController
import com.example.currencyexchangeapp.ui.adapter.ItemTouchActions
import com.example.currencyexchangeapp.ui.activity.BaseRateActivity
import com.example.currencyexchangeapp.ui.adapter.MyRateAdapter
import com.example.currencyexchangeapp.viewmodel.RateViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MyRateFragment : Fragment() {

    lateinit var binding: FragmentLikedRateBinding
    private val rateViewModel: RateViewModel by viewModels()
    private var myRateAdapter: MyRateAdapter? = null
    private var menuHost: MenuHost? = null
    private var menuItem: MenuItem? = null
    private val menuProvider = object : MenuProvider {

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            menuInflater.inflate(R.menu.toolbar_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.searchItem -> {
                    this@MyRateFragment.menuItem = menuItem
                    (menuItem.actionView as SearchView).onSearchTextChanged {
                        searchCurrency(query = it)
                    }
                    true
                }
                else -> true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()
        initRecyclerView()
        showBottomBar()
        observeRates()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initMenu()
        initMenuProvider()
    }

    private fun initBinding() {
        binding = FragmentLikedRateBinding.inflate(layoutInflater)
    }

    private fun initMenu() {
        menuHost = requireActivity()
    }

    override fun onStop() {
        super.onStop()
        destroyMenuProvider()
    }

    private fun initRecyclerView() {
        myRateAdapter = MyRateAdapter()
        val llm = LinearLayoutManager(activity?.baseContext)
        llm.orientation = RecyclerView.VERTICAL
        binding.recyclerView.adapter = myRateAdapter
        binding.recyclerView.layoutManager = llm

        context?.let {
            val swipeController =  ItemTouchController(it, object : ItemTouchActions() {
                override fun onRightClicked(position: Int) {
                    myRateAdapter?.let {
                        lifecycleScope.launch {
                            myRateAdapter?.rateList?.get(position)?.let {
                                rateViewModel.updateRateState(it.rateName, false)
                            }
                        }

                    }
                }
            })
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c)
            }
        })
        }

    }

    private fun showBottomBar() {
        (activity as BaseRateActivity).showBottomBar()
    }

    private fun observeRates() {
        lifecycleScope.launch {
            rateViewModel.getLikedRatesDB().collect {
                myRateAdapter?.addDataToAdapter(it)
            }
        }
    }

    private fun searchCurrency(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            rateViewModel.getLikedRatesByName(query).let { allRates ->
                withContext(Dispatchers.Main) {
                    myRateAdapter?.addDataToAdapter(allRates)
                }
            }
        }
    }

    private fun initMenuProvider() {
        menuHost?.addMenuProvider(menuProvider)
    }

    private fun destroyMenuProvider() {
        menuHost?.removeMenuProvider(menuProvider)
    }
}