package com.example.currencyexchangeapp.domain

import com.example.currencyexchangeapp.domain.model.LatestCurrencyModel
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyExchangeAPI {

    @GET("latest")
    fun getLatestCurrency(): Deferred<Response<LatestCurrencyModel>>
}