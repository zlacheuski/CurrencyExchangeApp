package com.example.currencyexchangeapp.ui.fragment

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.databinding.FragmentLikedRateBinding
import com.example.currencyexchangeapp.ui.adapter.ItemTouchController
import com.example.currencyexchangeapp.ui.adapter.ItemTouchActions
import com.example.currencyexchangeapp.ui.activity.BaseRateActivity
import com.example.currencyexchangeapp.ui.adapter.MyRateAdapter
import com.example.currencyexchangeapp.viewmodel.RateViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MyRateFragment : Fragment() {

    lateinit var binding: FragmentLikedRateBinding
    private val rateViewModel: RateViewModel by viewModels()
    private var myRateAdapter: MyRateAdapter? = null

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

    private fun initBinding() {
        binding = FragmentLikedRateBinding.inflate(layoutInflater)
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
}