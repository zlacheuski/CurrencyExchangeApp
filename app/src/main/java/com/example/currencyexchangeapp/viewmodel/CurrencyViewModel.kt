package com.example.currencyexchangeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.domain.model.LatestCurrencyModel
import com.example.currencyexchangeapp.domain.model.states.Resource
import com.example.currencyexchangeapp.domain.repository.latest_currencies.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(private val repository: CurrencyRepository) :
    ViewModel() {

    private val _response =
        MutableStateFlow<Resource<LatestCurrencyModel>>(Resource.Progress())
    val response: StateFlow<Resource<LatestCurrencyModel>>
        get() = _response

    val favouriteCurrency = repository.getCurrencyFromDatabase()

    fun getLatestCurrency() {
        viewModelScope.launch {
            repository.getLatestCurrency().collect { _response.emit(it) }
        }
    }

    fun insertRateToFavourites(favCurrency: Rates) {
        viewModelScope.launch(Dispatchers.IO) { repository.insertCurrencyToDatabase(favCurrency) }
    }

    fun deleteRateFromFavourites(favCurrency: Rates) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteCurrencyToDatabase(favCurrency) }
    }
}