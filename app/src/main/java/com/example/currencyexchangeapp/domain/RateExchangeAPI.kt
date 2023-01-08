package com.example.currencyexchangeapp.domain

import com.example.currencyexchangeapp.domain.model.LatestRateModel
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface RateExchangeAPI {

    @GET("latest")
    fun getLatestRate(): Deferred<Response<LatestRateModel>>
}