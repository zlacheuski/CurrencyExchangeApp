package com.example.currencyexchangeapp.domain.model

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val errorData: String) : Resource<T>()
    data class Progress<T>(val data: T? = null) : Resource<T>()
}