package com.example.currencyexchangeapp.domain.model

data class LatestCurrencyModel(
    val rates: Map<String, Double>,
    val success: Boolean
)