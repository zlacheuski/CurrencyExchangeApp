package com.example.currencyexchangeapp.domain.repository.rate_repository

import com.example.currencyexchangeapp.db.entity.Rates
import kotlinx.coroutines.flow.Flow

interface RateDBRepository {
    fun getRates(): Flow<List<Rates>>
    fun getLikedRates(): Flow<List<Rates>>
    fun getRatesNotFlow(): List<Rates>
    fun updateRateState(rateName: String, isLiked: Boolean)
    fun getLikedRatesByName(searchQuery: String): List<Rates>
    fun getRatesByName(searchQuery: String): List<Rates>
    fun insertRate(ratesList: List<Rates>)
    fun updateRates(ratesList: List<Rates>)
}