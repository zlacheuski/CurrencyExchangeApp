package com.example.currencyexchangeapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.databinding.ItemCurrencyBinding
import com.example.currencyexchangeapp.domain.model.LatestCurrencyModel

private typealias Rate = LatestCurrencyModel.Rates

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.CurrencyHolder>() {

    lateinit var binding : ItemCurrencyBinding
    private val currencyList: MutableList<Rate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        initBinding(parent)
        return CurrencyHolder()
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        holder.init(currencyList[position])
    }

    override fun getItemCount(): Int = currencyList.size

    override fun getItemViewType(position: Int): Int = position

    private fun initBinding(parent: ViewGroup){
        binding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    fun addDataToAdapter(list: MutableList<Rate>) {
        this.currencyList.clear()
        this.currencyList.addAll(list)
        notifyDataSetChanged()
    }

    inner class CurrencyHolder : RecyclerView.ViewHolder(binding.root) {

        fun init(currencyList: Rate) {
            binding.itemTv.text = "${currencyList.rateName} is ${currencyList.rateValue}"
        }
    }
}