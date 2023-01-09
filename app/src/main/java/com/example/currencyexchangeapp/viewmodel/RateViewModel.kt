package com.example.currencyexchangeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.domain.model.LatestRateModel
import com.example.currencyexchangeapp.domain.model.states.Resource
import com.example.currencyexchangeapp.domain.repository.rate_repository.RateRepositoryImpl
import com.example.currencyexchangeapp.utils.EncryptedSharedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RateViewModel @Inject constructor(
    private val repository: RateRepositoryImpl,
    private val sp: EncryptedSharedPreferences
) : ViewModel() {

    private val _response =
        MutableStateFlow<Resource<LatestRateModel>>(Resource.Progress())
    val response: StateFlow<Resource<LatestRateModel>>
        get() = _response

    val dbRates = repository.getRates()

    fun getRates(rateName: String = "USD") {
        viewModelScope.launch { repository.getLatestCurrency(rateName).collect { _response.emit(it) } }
    }

    fun getRatesByName(searchQuery: String) = repository.getRatesByName(searchQuery)

    fun updateRateState(rateName: String, isLiked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateRateState(rateName, isLiked) }
    }

    fun updateRates(ratesList: List<Rates>) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateRates(ratesList) }
    }

    fun getRatesDB() = repository.getRatesNotFlow()

    fun insertRates(rates: List<Rates>) {
        viewModelScope.launch(Dispatchers.IO) { repository.insertRate(rates) }
    }

    fun addSharedPref(tag: String, value: String) {
        sp.addPreference(tag, value)
    }

    fun addFloatSharedPref(tag: String, value: Float) {
        sp.addFloatPreference(tag, value)
    }

    fun removeSharedPref(tag: String) {
        sp.remove(tag)
    }

    fun getSharedPref(tag: String): String? {
        return sp.getPreference(tag)
    }

    fun getFloatSharedPref(tag: String): Float {
        return sp.getFloatPreference(tag)
    }

    fun getAllPreferences(): Map<String, *>? {
        return sp.allPreferences
    }
}