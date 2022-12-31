package com.example.currencyexchangeapp.domain.repository.latest_currencies

import com.example.currencyexchangeapp.domain.model.LatestCurrencyModel
import com.example.currencyexchangeapp.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface ICurrencyRepository {
    suspend fun getLatestCurrency(): Flow<Resource<LatestCurrencyModel>>
}