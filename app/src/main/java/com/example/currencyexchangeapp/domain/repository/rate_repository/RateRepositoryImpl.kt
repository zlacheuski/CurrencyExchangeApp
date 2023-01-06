package com.example.currencyexchangeapp.domain.repository.rate_repository

import com.example.currencyexchangeapp.db.dao.ICurrencyDAO
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.domain.CurrencyExchangeAPI
import com.example.currencyexchangeapp.domain.model.LatestCurrencyModel
import com.example.currencyexchangeapp.domain.model.states.Resource
import com.example.currencyexchangeapp.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RateRepositoryImpl @Inject constructor(
    private val api: CurrencyExchangeAPI,
    private val dbDao: ICurrencyDAO
) : RateRepository,RateDBRepository, BaseRepository() {

    override suspend fun getLatestCurrency(): Flow<Resource<LatestCurrencyModel>> =
        callOrError(api.getLatestCurrency())

    override fun getRates(): Flow<List<Rates>> = dbDao.getRates()

    override fun getRatesNotFlow(): List<Rates> = dbDao.getRatesNotFlow()

    override fun updateRateState(rateName: String, isLiked: Boolean) =
        dbDao.updateRateState(rateName, isLiked)

    override fun getRatesByName(searchQuery: String): List<Rates> =
        dbDao.getRatesByName(searchQuery)

    override fun insertRate(ratesList: List<Rates>) =
        dbDao.insertRate(ratesList)

    override fun updateRates(ratesList: List<Rates>) =
        dbDao.updateRates(ratesList)
}