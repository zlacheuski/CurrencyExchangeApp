package com.example.currencyexchangeapp.domain.model

import com.google.gson.annotations.SerializedName

data class LatestCurrencyModel(
    @SerializedName("rates")
    val rates: Map<String, Double>,
    @SerializedName("success")
    val success: Boolean
) {
    val ratesList: MutableList<Rates>
        get() = rates.map { Rates(it.key, it.value) }.sortedBy { it.rateName }.toMutableList()

    data class Rates(
        val rateName: String,
        val rateValue: Double
    )
}