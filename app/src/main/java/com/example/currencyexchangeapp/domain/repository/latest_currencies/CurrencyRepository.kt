package com.example.currencyexchangeapp.domain.repository.latest_currencies

import com.example.currencyexchangeapp.db.dao.ICurrencyDAO
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.domain.CurrencyExchangeAPI
import com.example.currencyexchangeapp.domain.model.LatestCurrencyModel
import com.example.currencyexchangeapp.domain.model.states.Resource
import com.example.currencyexchangeapp.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val api: CurrencyExchangeAPI,
    private val dbDao: ICurrencyDAO
) : ICurrencyRepository, BaseRepository() {

    override suspend fun getLatestCurrency(): Flow<Resource<LatestCurrencyModel>> =
        callOrError(api.getLatestCurrency())

    fun getCurrencyFromDatabase(): Flow<List<Rates>> =
        dbDao.getFavouriteCurrency()

    fun insertCurrencyToDatabase(favouriteRate: Rates) =
        dbDao.insertFavouriteRate(favouriteRate)

    fun deleteCurrencyToDatabase(favouriteRate: Rates) =
        dbDao.deleteFavouriteCurrency(favouriteRate)
}