package com.example.currencyexchangeapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.R
import com.example.currencyexchangeapp.databinding.ItemRateBinding
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.utils.IRateAdapter

class RateAdapter(private val rateAdapterImpl: IRateAdapter, val navigateToCalculateFragment: () -> Unit) :
    RecyclerView.Adapter<RateAdapter.CurrencyHolder>() {

    private lateinit var binding: ItemRateBinding
    private val rateList: MutableList<Rates> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        initBinding(parent)
        return CurrencyHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        holder.init(rateList[position])
    }

    override fun getItemCount(): Int = rateList.size

    @SuppressLint("NotifyDataSetChanged")
    fun addDataToAdapter(commonList: List<Rates>) {
        rateList.clear()
        rateList.addAll(commonList.sortedBy { !it.isLiked })
        this.notifyDataSetChanged()
    }

    private fun initBinding(parent: ViewGroup) {
        binding = ItemRateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    inner class CurrencyHolder(val binding: ItemRateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun init(rate: Rates) {
            with(binding) {
                itemTv.text = buildString {
                    append(rate.rateName)
                    append(" is ")
                    append(rate.rateValue)
                }
                if (rate.isLiked) {
                    currencyActionBtn.apply {
                        text = context.getString(R.string.rate_remove_text)
                        setOnClickListener {
                            rateAdapterImpl.updateRateState(
                                rate.rateName,
                                false
                            )
                        }
                    }
                } else {
                    currencyActionBtn.apply {
                    text = context.getString(R.string.rate_add_text)
                    setOnClickListener {
                        rateAdapterImpl.updateRateState(
                            rate.rateName,
                            true
                        )
                    }
                }
                }
                currencyItemCv.setOnLongClickListener {
                    rateAdapterImpl.apply {
                        addToSharedPreferences(rate.rateName, rate.rateValue)
                    }

                    navigateToCalculateFragment.invoke()
                    true
                }
            }
        }
    }
}