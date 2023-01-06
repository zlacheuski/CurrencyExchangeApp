package com.example.currencyexchangeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.domain.model.LatestCurrencyModel
import com.example.currencyexchangeapp.domain.model.states.Resource
import com.example.currencyexchangeapp.domain.repository.rate_repository.RateRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RateViewModel @Inject constructor(private val repository: RateRepositoryImpl) :
    ViewModel() {

    private val _response =
        MutableStateFlow<Resource<LatestCurrencyModel>>(Resource.Progress())
    val response: StateFlow<Resource<LatestCurrencyModel>>
        get() = _response

    val dbRates = repository.getRates()

    fun getRates() {
        viewModelScope.launch { repository.getLatestCurrency().collect { _response.emit(it) } }
    }

    fun getRatesByName(searchQuery: String) = repository.getRatesByName(searchQuery)

    fun updateRateState(rateName: String, isLiked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateRateState(rateName, isLiked) }
    }

    fun updateRates(ratesList: List<Rates>) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateRates(ratesList) }
    }

    fun getRatesDB() = repository.getRatesNotFlow()

    fun insertRates(rates: List<Rates>){
        viewModelScope.launch(Dispatchers.IO) { repository.insertRate(rates) }
    }
}