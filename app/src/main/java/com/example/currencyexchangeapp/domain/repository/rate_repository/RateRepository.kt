package com.example.currencyexchangeapp.domain.repository.rate_repository

import com.example.currencyexchangeapp.domain.model.LatestRateModel
import com.example.currencyexchangeapp.domain.model.states.Resource
import kotlinx.coroutines.flow.Flow

interface RateRepository {
    suspend fun getLatestCurrency(rateName: String): Flow<Resource<LatestRateModel>>
}