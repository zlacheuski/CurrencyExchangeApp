package com.example.currencyexchangeapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.databinding.ItemMyRateBinding
import com.example.currencyexchangeapp.db.entity.Rates

class MyRateAdapter : RecyclerView.Adapter<MyRateAdapter.MyRateViewHolder>() {

    private lateinit var binding: ItemMyRateBinding
    val rateList: MutableList<Rates> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRateViewHolder {
        initBinding(parent)
        return MyRateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyRateViewHolder, position: Int) {
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
        binding = ItemMyRateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    inner class MyRateViewHolder(val binding: ItemMyRateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun init(rate: Rates) {
            binding.itemTv.text = buildString {
                append(rate.rateName)
                append(" is ")
                append(rate.rateValue)
            }
        }
    }
}