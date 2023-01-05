package com.example.currencyexchangeapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.databinding.ItemCurrencyBinding
import com.example.currencyexchangeapp.db.dao.ICurrencyDAO
import com.example.currencyexchangeapp.db.entity.Rates
import timber.log.Timber

class CurrencyAdapter(private val dao: ICurrencyDAO) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyHolder>() {

    lateinit var binding: ItemCurrencyBinding
    val rateList: MutableList<Rates> = mutableListOf()
    val favouriteRateList: MutableList<Rates> = mutableListOf()
    val searchRateList: MutableList<Rates> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        initBinding(parent)
        return CurrencyHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        holder.init(rateList[position])
    }

    override fun getItemCount(): Int = rateList.size

    override fun getItemViewType(position: Int): Int = position

    fun addDataToAdapter(commonList: MutableList<Rates>, favouriteList: MutableList<Rates>) {
        rateList.clear()
        favouriteRateList.clear()
        rateList.addAll(filterItems(commonList, favouriteList))
        favouriteRateList.addAll(favouriteList)
        this.notifyDataSetChanged()
    }

    fun searchData(commonList: MutableList<Rates>, favouriteList: MutableList<Rates>) {
        rateList.clear()
        rateList.addAll(filterItems(commonList, favouriteList))
        this.notifyDataSetChanged()
    }

    private fun initBinding(parent: ViewGroup) {
        binding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    private fun filterItems(commonList: MutableList<Rates>, favouriteList: MutableList<Rates>) =
        favouriteList.filter { it in commonList }
            .toMutableList()
            .also { it.addAll(commonList.filter { it !in favouriteList }) }

    inner class CurrencyHolder(val binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun init(rate: Rates) {
            with(binding) {
                itemTv.text = "${rate.rateName} is ${rate.rateValue}"
                if (rate in favouriteRateList) {
                    currencyActionBtn.text = "Remove"
                    currencyActionBtn.setOnClickListener { dao.deleteFavouriteCurrency(rate) }
                } else {
                    currencyActionBtn.text = "Add"
                    currencyActionBtn.setOnClickListener { dao.insertFavouriteRate(rate) }
                }
            }
        }
    }
}