package com.example.currencyexchangeapp.domain.repository.latest_currencies

import com.example.currencyexchangeapp.domain.CurrencyExchangeAPI
import com.example.currencyexchangeapp.domain.model.LatestCurrencyModel
import com.example.currencyexchangeapp.domain.model.Resource
import com.example.currencyexchangeapp.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyRepository @Inject constructor(private val api: CurrencyExchangeAPI) :
    ICurrencyRepository, BaseRepository() {

    override suspend fun getLatestCurrency(): Flow<Resource<LatestCurrencyModel>> =
        callOrError(api.getLatestCurrency())
}