package com.example.currencyexchangeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchangeapp.domain.model.LatestCurrencyModel
import com.example.currencyexchangeapp.domain.model.Resource
import com.example.currencyexchangeapp.domain.repository.latest_currencies.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(val repository: CurrencyRepository) : ViewModel() {

    private val _response =
        MutableStateFlow<Resource<LatestCurrencyModel>>(Resource.Progress())
    val response: StateFlow<Resource<LatestCurrencyModel>>
        get() = _response

    fun getLatestCurrency() {
        viewModelScope.launch {
            repository.getLatestCurrency().collect { _response.emit(it) }
        }
    }
}