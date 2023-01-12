package com.example.currencyexchangeapp.domain

import com.example.currencyexchangeapp.domain.model.LatestRateModel
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.HTTP
import retrofit2.http.Query

interface RateExchangeAPI {

    @HTTP(method = "GET", path = "latest")
    fun getLatestRateAsync(
        @Query("base") base: String = "USD"
    ): Deferred<Response<LatestRateModel>>
}